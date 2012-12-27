class BMark {
    public int f1(int n) {
	if (n == 0)
	    return 1;
	else
	    return f1(n-1) + f1(n-1);
    }

    public double f2(double n) {
	if (n == 0.0)
	    return 1.0;
	else
	    return f2(n-1.0) + f2(n-1.0);
    }

    public Double f3(Double n) {
	if (n == 0.0)
	    return 1.0;
	else
	    return f3(n-1.0) + f3(n-1.0);
    }

    public Boolean eq(Double x, Double y) { return (double) x == y; }
    public Double add(Double x, Double y) { return x + y; }
    public Double sub(Double x, Double y) { return x - y; }

    public Double f4(Double n) {
	if (eq(n, 0.0))
	    return 1.0;
	else
	    return add(f4(sub(n, 1.0)), f4(sub(n, 1.0)));
    }

    static class Name {
	public BMark bm;

	public Name(BMark bm) { this.bm = bm; }
    }

    static Name glob;
    static int count = 0;

    public void reset() { count = 0; }

    public Double f5(Double n) {
	if (++count >= 100) reset();

	if (glob.bm.eq(n, 0.0))
	    return 1.0;
	else
	    return glob.bm.add(glob.bm.f5(glob.bm.sub(n, 1.0)), 
			       glob.bm.f5(glob.bm.sub(n, 1.0)));
    }

    public static void main(String args[]) {
	BMark bm = new BMark();
	int k = Integer.parseInt(args[0]);
	int n = Integer.parseInt(args[1]);

	switch (k) {
	    case 1:
		System.out.println(bm.f1(n));
		break;
	    case 2:
		System.out.println(bm.f2(n));
		break;
	    case 3:
		System.out.println(bm.f3((double) n));
		break;
	    case 4:
		System.out.println(bm.f4((double) n));
		break;
	    case 5:
		glob = new Name(bm);
		System.out.println(glob.bm.f5((double) n));
		break;
	}
   }
}
