#include <stdlib.h>
#include <stdio.h>

int f(int n) {
     if (n == 0)
	  return 1;
     else
	  return f(n-1) + f(n-1);
}

void main(int argc, char *argv[]) {
     printf("%d\n", f(atoi(argv[1])));
}
