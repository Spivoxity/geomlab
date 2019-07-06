package geomlab;

import funbase.Value;
import funbase.Name;
import funbase.FunCode;

import java.util.ArrayList;

public abstract class Bootstrap {
    public abstract void boot();

    public void D(String s, Value v) {
        Name name = Name.find(s);
        name.bootDef(v);
    }

    public Value B(boolean b) {
        return Value.bool(b);
    }

    public Value S(String s) {
        return Value.string(s);
    }

    public Value N(String s) { return Name.find(s); }

    public Value C(FunCode f) { return f.makeClosure(new Value[1]); }

    private ArrayList<Integer> code = new ArrayList<>();

    public void I(FunCode.Opcode op) {
        code.add(op.ordinal());
    }

    public void I(FunCode.Opcode op, int rand) {
        code.add(op.ordinal()); code.add(rand);
    }

    public FunCode F(String name, int arity, Code body, Value... consts) {
        body.gen();
        int n = code.size();
        int ops[] = new int[n];
        for (int i = 0; i < n; i++) {
            ops[i] = code.get(i);
        }
        code.clear();
        return new FunCode(name, arity, ops, consts);
    }

    public interface Code { public void gen(); }
}
