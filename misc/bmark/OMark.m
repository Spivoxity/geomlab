MODULE OMark;

IMPORT Args, Conv, Out;

PROCEDURE f(n: INTEGER): INTEGER;
BEGIN
  IF n = 0 THEN
    RETURN 1
  ELSE
    RETURN f(n-1) + f(n-1)
  END
END f;

VAR buf: ARRAY 32 OF CHAR;

BEGIN
  Args.GetArg(1, buf);
  Out.Int(f(Conv.IntVal(buf)), 0);
  Out.Ln
END OMark.
