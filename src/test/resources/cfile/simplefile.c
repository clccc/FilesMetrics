void complex_test(int a, int b, int c) {
start:	A();
	for (; x < 5;) {
		x++;
		while (y) {
			y--;
			if (c) {
				goto start;
			} else if (c - b) {
				goto end;
			} else if (c - a) {
				continue;
			}
			break;
		}
		c--;
	}
end:
	switch (a) {
		case 1:
		case 2: B();
		case 3: C(); break;
		default: D();
	}
}