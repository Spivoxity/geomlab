package funjit;

import java.util.*;

import funbase.Value;
import funbase.Primitive;

import static funjit.Opcodes.Op.*;
import static funjit.Type.*;

/** A type that can be passed between functions */
public class Species {
    /** The Java class object */
    public final Class<?> klass;

    /** The class name used in the JVM code, or null if not a class */
    public final String clname;

    /** The type string used in the class file */
    public final Type type;

    private Species(Class<?> klass, String clname, Type type) {
        this.klass = klass; this.clname = clname; this.type = type;
    }
    
    /** A primitive type like Double.TYPE */
    private Species(Class<?> klass, Type type) {
        this(klass, null, type);
    }

    /** A genuine class */
    private Species(Class<?> klass, String clname) {
        this(klass, clname, Type.class_t(clname));
    }

    /** Generate code to convert from species to Value */
    public void widen(FunctionClass code) { 
        throw new Error("Can't widen from " + klass);
    }

    /** Generate code to convert from Value to species for named primitive */
    public void narrow(String prim, FunctionClass code) { 
        throw new Error("Can't narrow to " + klass);
    }

    /** Generate code to convert from Value to species in a Primitive object */
    public void primarg(int n, JitPrimFactory factory) {
        throw new Error("Can't narrow to " + klass);
    }

    /** Construct the JVM type string for a method */
    public static Type methType(Species pkinds[], Species rkind) {
        int n = pkinds.length;
        Type buf[] = new Type[n+1];
        for (int i = 0; i < n; i++)
            buf[i] = pkinds[i].type;
        buf[n] = rkind.type;
        return func_t(buf);
    }

    /** Table of species encountered so far */
    private static Map<Class<?>, Species> table = new HashMap<>();

    /** Register a species */
    private static void register(Species s) {
        table.put(s.klass, s);
    }

    /** Find a species, or create one for a new Value subclass,
        or a class annotated with PRIMPARAM. */
    public static Species find(Class<?> c) {
        Species s = table.get(c);

        if (s == null) {
            if (Value.class.isAssignableFrom(c))
                s = new SubclassSpecies(c);
            else if (c.getAnnotation(Primitive.PRIMPARAM.class) != null)
                s = new ParamSpecies(c);
            else
                throw new Error("no species for " + c);

            register(s);
        }

        return s;
    }

    /** Species for a type that can be wrapped as a Value */
    private static class WrappedSpecies extends Species {
        /** Name of the wrapper class */
        private final String wrap_cl;

        /** Name of the projection method on values */
        private final String project;

        /** Name of the projection method provided by Primitive */
        private final String primget;

        private final Type wrap_t;
        private final Type proj_t = func_t(type);
        private final Type primget_t = func_t(value_t, type);

        public WrappedSpecies(Class<?> klass, Type type, String wrap_cl, 
                              String project, String primget) {
            super(klass, type);
            this.wrap_cl = wrap_cl;
            this.project = project;
            this.primget = primget;
            this.wrap_t = func_t(type, class_t(wrap_cl));
        }

        public void widen(FunctionClass code) {
            code.gen(INVOKESTATIC, wrap_cl, "getInstance", wrap_t);
        }

        public void narrow(String prim, FunctionClass code) {
            code.access(project, proj_t, prim, wrap_cl);
        }

        public void primarg(int n, JitPrimFactory factory) {
            factory.accessArg(primget, n, primget_t);
        }
    }

    /** Species for castable parameter */
    private static class ParamSpecies extends Species {
        private ParamSpecies(Class<?> c) {
            super(c, className(c));
        }

        public void narrow(String prim, FunctionClass code) {
            code.cast(clname, prim);
        }

        public void primarg(int n, JitPrimFactory factory) {
            factory.castArg(clname, n);
        }

        // Can't widen to these types
    }

    /** Species for subclasses of Value, automatically created. */
    private static class SubclassSpecies extends ParamSpecies {
        private SubclassSpecies(Class<?> c) { super(c); }

        // In addition to casting as a parameter, we can also
        // silently widen to Value
        public void widen(FunctionClass code) { }
    }

    /** Species for standard primitive types */
    public static final Species VALUE = 
        new Species(Value.class, value_t) {
            public void widen(FunctionClass code) { }

            public void narrow(String prim, FunctionClass code) { }

            public void primarg(int n, JitPrimFactory factory) {
                factory.getArg(n);
            }
        };

    public static final Species NUMBER =
        new WrappedSpecies(Double.TYPE, double_t, numval_cl, 
                           "asNumber", "number");

    public static final Species BOOL =
        new WrappedSpecies(Boolean.TYPE, bool_t, boolval_cl, 
                           "asBoolean", "boolean");

    public static final Species INT =
        new WrappedSpecies(Integer.TYPE, int_t, numval_cl, 
                           "asInteger", "integer");

    public static final Species STRING =
        new WrappedSpecies(String.class, string_t, stringval_cl, 
                           "asString", "string");

    public static final Species VOID =
        new Species(Void.TYPE, void_t) {
            // A void result is represented by returning []
            public void widen(FunctionClass code) {
                code.gen(GETSTATIC, value_cl, "nil", value_t);
            }
        };

    public static final Species PRIMITIVE =
        // Just the type info, no narrowing or widening
        new Species(Primitive.class, prim_t);

    static {
        register(VALUE);
        register(NUMBER);
        register(BOOL);
        register(INT);
        register(STRING);
        register(VOID);
        register(PRIMITIVE);
    }
}
