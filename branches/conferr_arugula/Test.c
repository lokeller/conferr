/*
 *  Test.c
 *  StaticToolToDetermineMultipleDirectives
 *
 *  Created by Silviu Andrica on 2/20/10.
 *  Copyright 2010 EPFL. All rights reserved.
 *
 */

#include <stdio.h>
#include <string.h>

char* stuff[] = {"<A>", "<B>"};//, "<C>", "<D>", "<E>"};
char* stuff2[] = {"<Am>", "<Bm>"};//, "<C>", "<D>", "<E>"};
#define macro(x, y) strcat(x,y)

void define(const char* name){
	printf("%s\n", name);
}

void define2(int init, const char* name){
}
int main(){
	char str[2*strlen("Silviu")+1];
	macro(str, "Silviu");
	macro(str, "Silviu");
	define(str);
	define("G");
	define("H");
//	define("I");
//	define("J");
//	define("K");
//	define("L");
//	define("M");
	define2(0, "N");
	define2(1, "O");
	define2(2, "P");
	define2(3, "Q");
	define2(4, "R");
	define2(5, "S");
}

