package funbase;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.math.BigInteger;

/** A numeric value represented as a double-precision float */
public abstract class NumValue extends Value {
    private static final int MIN = -1, MAX = 2000;
    private static BignumValue smallints[] = new BignumValue[MAX-MIN+1];

    public static BignumValue getInstance(long n) {
        if (n < MIN || n > MAX)
            return new BignumValue(BigInteger.valueOf(n));
        else {
            int nn = (int) n;
            if (smallints[nn-MIN] == null)
                smallints[nn-MIN] = new BignumValue(BigInteger.valueOf(n));
            return smallints[nn-MIN];
        }
    }

    public static final BigInteger 
        MIN_SMALL = BigInteger.valueOf(MIN),
        MAX_SMALL = BigInteger.valueOf(MAX);

    public static NumValue getInstance(BigInteger n) {
        if (n.compareTo(MIN_SMALL) >= 0 
            && n.compareTo(MAX_SMALL) <= 0)
            return getInstance(n.intValue());
        else
            return new BignumValue(n);
    }

    public static NumValue getInstance(double val) {
        long n = (long) val;

        if (val == n)
            return getInstance(n);
        else
            return new DoubleValue(val);
    }

    private static BigInteger 
        MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE),
        MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    public static NumValue makeInteger(String rep) {
        BigInteger val = new BigInteger(rep);
        if (val.compareTo(MIN_INT) >= 0 && val.compareTo(MAX_INT) <= 0)
            return getInstance(val.intValue());
        else
            return new BignumValue(val);
    }

    public static NumValue makeReal(String rep) {
        return getInstance(Double.valueOf(rep));
    }

    public abstract boolean isInteger();
    public abstract int asInteger();
    public abstract float asFloat();
    public abstract double asDouble();
    protected BigInteger asBigInteger() { return BigInteger.ZERO; }

    public abstract NumValue add(NumValue other);
    public abstract NumValue sub(NumValue other);
    public abstract NumValue mul(NumValue other);
    public abstract NumValue div(NumValue other);
    public abstract NumValue intdiv(NumValue other);
    public abstract NumValue neg();

    public abstract int compareTo(NumValue other);

    public static final NumValue ZERO = getInstance(0);

    public Value matchPlus(Value iv) {
        NumValue inc = (NumValue) iv;
        if (! inc.isInteger()) return null;
        if (this.compareTo(inc) < 0) return null;
        NumValue diff = this.sub(inc);
        if (diff.compareTo(ZERO) < 0) return null;
        return diff;
    }

    @Override
    public void dump(PrintWriter out) {
        double val = this.asInteger();
        out.printf("number %d\n", (int) val);
    }

    private static class IntValue extends NumValue {
        private static final long serialVersionUID = 1L;

        /** The value */
        private final int val;
	
        public IntValue(int val) { this.val = val; }
	
        @Override
        public boolean isInteger() { return true; }

        @Override
        public int asInteger() { return val; }

        @Override
        public float asFloat() { return (float) val; }

        @Override
        public double asDouble() { return (double) val; }

        @Override 
        public NumValue add(NumValue other) {
            if (other instanceof IntValue)
                return getInstance((long) val + other.asInteger());
            else if (other.isInteger())
                return getInstance
                    (this.asBigInteger().add(other.asBigInteger()));
            else
                return getInstance(val + other.asDouble());
        }

        @Override 
        public NumValue sub(NumValue other) {
            if (other instanceof IntValue)
                return getInstance((long) val - other.asInteger());
            else if (other.isInteger())
                return getInstance
                    (this.asBigInteger().subtract(other.asBigInteger()));
            else
                return getInstance(val - other.asDouble());
        }

        @Override 
        public NumValue mul(NumValue other) {
            if (other instanceof IntValue)
                return getInstance((long) val * other.asInteger());
            else if (other.isInteger())
                return getInstance
                    (this.asBigInteger().multiply(other.asBigInteger()));
            else
                return getInstance(val * other.asDouble());
        }

        @Override 
        public NumValue div(NumValue other) {
            return getInstance(val / other.asDouble());
        }

        @Override
        public NumValue intdiv(NumValue other) {
            if (other instanceof IntValue)
                return getInstance(myDiv(val, other.asInteger()));
            else if (other.isInteger())
                return getInstance(myDiv(this.asBigInteger(),
                                         other.asBigInteger()));
            else
                return getInstance(Math.floor(val / other.asDouble()));
        }

        @Override
        public NumValue neg() {
            return getInstance(- (long) val);
        }

        @Override
        public int compareTo(NumValue other) {
            if (other instanceof IntValue)
                return Integer.compare(val, other.asInteger());
            else if (other.isInteger())
                return this.asBigInteger().compareTo(other.asBigInteger());
            else
                return Double.compare(val, other.asDouble());
        }

        @Override
        public void printOn(PrintWriter out) {
            out.print(val);
        }

        @Override
        public boolean equals(Object a) {
            return (a instanceof NumValue && this.equals((NumValue) a));
        }
	
        public boolean equals(NumValue other) {
            if (other instanceof IntValue)
                return val == other.asInteger();
            else if (other.isInteger())
                return this.asBigInteger().equals(other.asBigInteger());
            else
                return Double.valueOf(val) == other.asDouble();
        }

        @Override
        public int hashCode() {
            return val;
        }
    }

    private static class BignumValue extends NumValue {
        private static final long serialVersionUID = 1L;

        /** The value */
        private final BigInteger val;
	
        public BignumValue(BigInteger val) { this.val = val; }
	
        @Override
        public boolean isInteger() { return true; }

        @Override
        public int asInteger() { return val.intValue(); }

        @Override
        public float asFloat() { return val.floatValue(); }

        @Override
        public double asDouble() { return val.doubleValue(); }

        @Override
        protected BigInteger asBigInteger() { return val; }

        @Override 
        public NumValue add(NumValue other) {
            if (other.isInteger())
                return getInstance(val.add(other.asBigInteger()));
            else
                return getInstance(val.doubleValue() + other.asDouble());
        }

        @Override 
        public NumValue sub(NumValue other) {
            if (other.isInteger())
                return getInstance(val.subtract(other.asBigInteger()));
            else
                return getInstance(val.doubleValue() - other.asDouble());
        }

        @Override 
        public NumValue mul(NumValue other) {
            if (other.isInteger())
                return getInstance(val.multiply(other.asBigInteger()));
            else
                return getInstance(val.doubleValue() * other.asDouble());
        }

        @Override 
        public NumValue div(NumValue other) {
            return getInstance(val.doubleValue() / other.asDouble());
        }

        @Override
        public NumValue intdiv(NumValue other) {
            if (other.isInteger())
                return getInstance(myDiv(val, other.asBigInteger()));
            else
                return getInstance
                    (Math.floor(val.doubleValue() / other.asDouble()));
        }

        @Override
        public NumValue neg() {
            return getInstance(val.negate());
        }

        @Override
        public int compareTo(NumValue other) {
            if (other.isInteger())
                return val.compareTo(other.asBigInteger());
            else
                return Double.compare(val.doubleValue(), other.asDouble());
        }

        @Override
        public void printOn(PrintWriter out) {
            out.print(val.toString());
        }

        @Override
        public boolean equals(Object a) {
            if (a instanceof BignumValue)
                return val.equals(((BignumValue) a).val);
            else
                return (a instanceof NumValue 
                        && val.doubleValue() == ((NumValue) a).asDouble());
        }
	
        @Override
        public int hashCode() {
            return val.hashCode();
        }
    }

    private static class DoubleValue extends NumValue {
        private static final long serialVersionUID = 1L;

        /** The value */
        private final double val;
	
        public DoubleValue(double val) { this.val = val; }
	
        @Override
        public boolean isInteger() {  return val == (int) val; }

        @Override
        public int asInteger() { return (int) val; }

        @Override
        public float asFloat() { return (float) val; }

        @Override
        public double asDouble() { return val; }

        @Override 
        public NumValue add(NumValue other) {
            return getInstance(val + other.asDouble());
        }

        @Override 
        public NumValue sub(NumValue other) {
            return getInstance(val - other.asDouble());
        }

        @Override 
        public NumValue mul(NumValue other) {
            return getInstance(val * other.asDouble());
        }

        @Override 
        public NumValue div(NumValue other) {
            return getInstance(val / other.asDouble());
        }

        @Override
        public NumValue intdiv(NumValue other) {
            return getInstance(Math.floor(val / other.asDouble()));
        }

        @Override
        public NumValue neg() {
            return getInstance(-val);
        }

        @Override
        public int compareTo(NumValue other) {
            return Double.compare(val, other.asDouble());
        }

        @Override
        public void printOn(PrintWriter out) {
            printNumber(out, val);
        }

        @Override
        public boolean equals(Object a) {
            return (a instanceof NumValue 
                    && val == ((NumValue) a).asDouble());
        }
	
        @Override
        public int hashCode() {
            long x = Double.doubleToLongBits(val);
            return (int) (x ^ (x >> 32));
        }

        public Value matchPlus(Value iv) {
            double inc = ((NumValue) iv).asDouble();
            double x = val - inc;
            if (inc > 0 && x >= 0 && x == (int) x)
                return getInstance(x);
            else
                return null;
        }
    }

    /** Print a number nicely. */
    public static void printNumber(PrintWriter out, double x) {
	if (x == (int) x)
	    out.print((int) x);
	else if (Double.isNaN(x))
	    out.print("NaN");
	else {
	    double y = x;
	    if (y < 0.0) {
		out.print('-');
		y = -y;
	    }

	    if (Double.isInfinite(y))
		out.print("Infinity");
	    else {
		// Sometimes stupid persistence is the best way ...
		String pic;
		if (y < 0.001)           pic = "0.0######E0";
		else if (y < 0.01)       pic = "0.000#######";
		else if (y < 0.1)        pic = "0.00#######";
		else if (y < 1.0)        pic = "0.0#######";
		else if (y < 10.0)       pic = "0.0######";
		else if (y < 100.0)      pic = "#0.0#####";
		else if (y < 1000.0)     pic = "##0.0####";
		else if (y < 10000.0)    pic = "###0.0###";
		else if (y < 100000.0)   pic = "####0.0##";
		else if (y < 1000000.0)  pic = "#####0.0#";
		else if (y < 10000000.0) pic = "######0.0";
		else                     pic = "0.0######E0";
		NumberFormat fmt = new DecimalFormat(pic);
		out.print(fmt.format(y));
	    }
	}
    }

    public static long myDiv(long a, long b) {
        if (b < 0) {
            a = -a; b = -b;
        }

        if (a >= 0)
            return a / b;
        else
            return (a+1)/b - 1;
    }

    public static BigInteger myDiv(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) < 0) {
            a = a.negate(); b = b.negate();
        }

        if (a.compareTo(BigInteger.ZERO) >= 0)
            return a.divide(b);
        else
            return a.add(BigInteger.ONE).divide(b).subtract(BigInteger.ONE);
    }
}
    
