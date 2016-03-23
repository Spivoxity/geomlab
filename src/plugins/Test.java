package plugins;

import funbase.Primitive.PRIMITIVE;

public class Test {
    @PRIMITIVE
    public static double test(double a, double b, double c, double d,
                              double e, double f, double g, double h) {
        return (((((((a * 10 + b) 
                     * 10 + c) 
                    * 10 + d) 
                   * 10 + e) 
                  * 10 + f) 
                 * 10 + g) 
                * 10 + h);
    }

    @PRIMITIVE
    public static int inc(int x) { return x+1; }
}

