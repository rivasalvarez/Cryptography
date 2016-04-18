import java.util.Scanner;

public class DES2 {
private static int[] IP = new int[64];
private static int[] IP1 = new int[64];
private static int[] E = new int[48];
private static int[] P = new int[32];
private static int[] PC1 = new int[56];
private static int[] PC2 = new int[48];
private static int[] shiftTable = new int[16];
private static char[][] SubBox1 = new char[4][16];
private static char[][] SubBox2 = new char[4][16];
private static char[][] SubBox3 = new char[4][16];
private static char[][] SubBox4 = new char[4][16];
private static char[][] SubBox5 = new char[4][16];
private static char[][] SubBox6 = new char[4][16];
private static char[][] SubBox7 = new char[4][16];
private static char[][] SubBox8 = new char[4][16];
private static long[] CN = new long[17];
private static long[] DN = new long[17];
private static long[] Keys = new long[16];
private static long LH;
private static long RH;
private static long cipher;
	
	public static void main(String[] args) {
		Scanner fin = new Scanner(System.in);
	    long originalKey;
		long pText;
		
		init();
		
        System.out.println("Enter 64 bit Plain Text(In Hex): ");
		pText = Long.parseUnsignedLong(fin.next(), 16); System.out.println();
		
		System.out.println("Enter 64 bit Key(In Hex): ");
		originalKey = Long.parseUnsignedLong(fin.next(), 16); System.out.println();

        long start = System.nanoTime();
        KeyGen(originalKey);
        IP(pText);
        Cycle();
        InverseIP();
        long  end = System.nanoTime();
        
        System.out.println("Cypher Text is(In Hex): ");
        System.out.println(Long.toHexString(cipher));
        System.out.print(end - start);
        
        fin.close();
	}
	
	public static void IP(long input){
	  long temp = input;
	  input = 0;
		
		for(int i = 0; i < 64; i++)
		  input |= ( ((temp>>(64 - IP[i]) ) &1)) << (63-i);
	    
		for(int i = 0;i < 32;i++)
		LH |= ( ((input>>(63 - i) ) &1)) << (31-i) ;
		
		for(int i = 32;i < 64;i++)
		RH |= ( ((input>>(63 - i) ) &1)) << (63-i) ;
	}
	
	public static void Cycle(){
	long tempLeft = 0;
	
	     for(int round = 0;round < 16; round++){
	     tempLeft = LH;
	     LH = RH;
	     f(round);
	     RH ^= tempLeft;
	     }
	}
	
	public static void InverseIP (){
	 long temp = LH;
	 RH = Long.rotateLeft(RH, 32);
	 temp |= RH;
	 
	   for(int i = 0;i < 64; i++)
	     cipher |= (((temp>>(64 - IP1[i]) ) &1)) << (63-i) ;
	}
	
	public static void KeyGen(long Originalkey){
		long kPlus = 0;
		long KN = 0;
		
		for(int i = 0;i < 56;i++)
		  kPlus |= ( ((Originalkey>>(64 - PC1[i]) ) &1)) << (55-i) ;
		
		
		for(int i = 0;i < 28;i++)
		CN[0] |= ( ((kPlus>>(55 - i) ) &1)) << (27-i) ;
		for(int i = 28;i < 56;i++)
		DN[0] |= ( ((kPlus>>(55 - i) ) &1)) << (55-i) ;
			
		
		for(int i = 0; i < 16; i++){
		  int j = shiftTable[i];
		   CN[i+1] = CN[i] << j;
		   DN[i+1] = DN[i] << j;
		   if(j == 1){
			 CN[i+1] |= (((CN[i+1]>>28)&1));
			 DN[i+1] |= (((DN[i+1]>>28)&1));
		   }
		   if(j == 2){
			 CN[i+1] |= (((CN[i+1]>>28)&1));
			 CN[i+1] |= (((CN[i+1]>>29)&1)) << 1;
			 DN[i+1] |= (((DN[i+1]>>28)&1));
			 DN[i+1] |= (((DN[i+1]>>29)&1)) << 1;
		   }
			 CN[i+1] &= 268435455;
			 DN[i+1] &= 268435455;
		}
		
		for(int i = 1;i < 17; i++){
		  KN |= CN[i] << 28;
		  KN |= DN[i];
		  for(int j = 0;j < 48;j++)
			  Keys[i-1] |= (((KN>>(56 - PC2[j])) &1))<< (47-j);
		  KN = 0;
		}
	}
	
	public static void f(int round){
	long ERH = 0;
	long tempRH = 0;
	String sBox = "";
	byte row = 0x00;
	byte col = 0x00;
	
	   for(int i = 0;i < 48; i++)
	    ERH |= ( ((RH>>(32 - E[i]) ) &1)) << (47-i);
	  
	   long temp = 0;
	   ERH ^= Keys[round];
	   ERH = Long.rotateRight(ERH, 42);
    
	   for(int j = 0; j < 8;j++){
	   temp = ERH & 63;
	   ERH = Long.rotateLeft(ERH, 6);
	   
         if((temp & 32) != 0) row |= 0b00000010;
         if((temp & 1) != 0) row |= 0b00000001;
       
         if((temp & 16) != 0) col |= 0b00001000;
         if((temp & 8) != 0) col |= 0b00000100;
         if((temp & 4) != 0) col |= 0b00000010;
         if((temp & 2) != 0) col |= 0b00000001;
         
         if(j == 0)sBox += SubBox1[row][col];
         if(j == 1)sBox += SubBox2[row][col];
         if(j == 2)sBox += SubBox3[row][col];
         if(j == 3)sBox += SubBox4[row][col];
         if(j == 4)sBox += SubBox5[row][col];
         if(j == 5)sBox += SubBox6[row][col];
         if(j == 6)sBox += SubBox7[row][col];
         if(j == 7)sBox += SubBox8[row][col];
       row &= 0x00;
       col &= 0x00;
	   }
	   
       tempRH = Long.parseLong(sBox, 16);
       RH = 0;
       for(int i = 0;i < 32; i++)
 		  RH |= ( ((tempRH>>(32 - P[i]) ) &1)) << (31-i) ;
	}
	
	public static void init(){
		initIP();
		initExpand();
		initKey();
	}
	
	public static void initIP(){

    IP[0]=58;  IP[1]=50;  IP[2]=42;  IP[3]=34;  IP[4]=26;  IP[5]=18;  IP[6]=10;  IP[7]=2;
    IP[8]=60;  IP[9]=52;  IP[10]=44; IP[11]=36; IP[12]=28; IP[13]=20; IP[14]=12; IP[15]=4;
    IP[16]=62; IP[17]=54; IP[18]=46; IP[19]=38; IP[20]=30; IP[21]=22; IP[22]=14; IP[23]=6;
    IP[24]=64; IP[25]=56; IP[26]=48; IP[27]=40; IP[28]=32; IP[29]=24; IP[30]=16; IP[31]=8;
    IP[32]=57; IP[33]=49; IP[34]=41; IP[35]=33; IP[36]=25; IP[37]=17; IP[38]=9;  IP[39]=1;
    IP[40]=59; IP[41]=51; IP[42]=43; IP[43]=35; IP[44]=27; IP[45]=19; IP[46]=11; IP[47]=3;
    IP[48]=61; IP[49]=53; IP[50]=45; IP[51]=37; IP[52]=29; IP[53]=21; IP[54]=13; IP[55]=5;
    IP[56]=63; IP[57]=55; IP[58]=47; IP[59]=39; IP[60]=31; IP[61]=23; IP[62]=15; IP[63]=7;  
	   
    IP1[0]=40;  IP1[1]=8;  IP1[2]=48;  IP1[3]=16;  IP1[4]=56;  IP1[5]=24;  IP1[6]=64;   IP1[7]=32;
    IP1[8]=39;  IP1[9]=7;  IP1[10]=47; IP1[11]=15; IP1[12]=55; IP1[13]=23; IP1[14]=63;  IP1[15]=31;
    IP1[16]=38; IP1[17]=6; IP1[18]=46; IP1[19]=14; IP1[20]=54; IP1[21]=22; IP1[22]=62;  IP1[23]=30;
    IP1[24]=37; IP1[25]=5; IP1[26]=45; IP1[27]=13; IP1[28]=53; IP1[29]=21; IP1[30]=61;  IP1[31]=29;
    IP1[32]=36; IP1[33]=4; IP1[34]=44; IP1[35]=12; IP1[36]=52; IP1[37]=20; IP1[38]=60;  IP1[39]=28;
    IP1[40]=35; IP1[41]=3; IP1[42]=43; IP1[43]=11; IP1[44]=51; IP1[45]=19; IP1[46]=59;  IP1[47]=27;
    IP1[48]=34; IP1[49]=2; IP1[50]=42; IP1[51]=10; IP1[52]=50; IP1[53]=18; IP1[54]=58;  IP1[55]=26;
    IP1[56]=33; IP1[57]=1; IP1[58]=41; IP1[59]=9;  IP1[60]=49; IP1[61]=17; IP1[62]=57;  IP1[63]=25;  
	}
	
	public static void initExpand(){
    E[0]=32;  E[1]=1;   E[2]=2;   E[3]=3;   E[4]=4;   E[5]=5;
    E[6]=4;   E[7]=5;   E[8]=6;   E[9]=7;   E[10]=8;  E[11]=9;
    E[12]=8;  E[13]=9;  E[14]=10; E[15]=11; E[16]=12; E[17]=13;
    E[18]=12; E[19]=13; E[20]=14; E[21]=15; E[22]=16; E[23]=17;
    E[24]=16; E[25]=17; E[26]=18; E[27]=19; E[28]=20; E[29]=21;
    E[30]=20; E[31]=21; E[32]=22; E[33]=23; E[34]=24; E[35]=25;
    E[36]=24; E[37]=25; E[38]=26; E[39]=27; E[40]=28; E[41]=29;
    E[42]=28; E[43]=29; E[44]=30; E[45]=31; E[46]=32; E[47]=1;
	}
	
	public static void initKey(){
	PC1[0]=57;  PC1[1]=49;  PC1[2]=41;  PC1[3]=33;  PC1[4]=25;  PC1[5]=17;  PC1[6]=9;
	PC1[7]=1;   PC1[8]=58;  PC1[9]=50;  PC1[10]=42; PC1[11]=34; PC1[12]=26; PC1[13]=18;
	PC1[14]=10; PC1[15]=2;  PC1[16]=59; PC1[17]=51; PC1[18]=43; PC1[19]=35; PC1[20]=27;
	PC1[21]=19; PC1[22]=11; PC1[23]=3;  PC1[24]=60; PC1[25]=52; PC1[26]=44; PC1[27]=36;
	PC1[28]=63; PC1[29]=55; PC1[30]=47; PC1[31]=39; PC1[32]=31; PC1[33]=23; PC1[34]=15;
	PC1[35]=7;  PC1[36]=62; PC1[37]=54; PC1[38]=46; PC1[39]=38; PC1[40]=30; PC1[41]=22;
	PC1[42]=14; PC1[43]=6;  PC1[44]=61; PC1[45]=53; PC1[46]=45; PC1[47]=37; PC1[48]=29;
	PC1[49]=21; PC1[50]=13; PC1[51]=5;  PC1[52]=28; PC1[53]=20; PC1[54]=12; PC1[55]=4;
	
	
	shiftTable[0] = 1; shiftTable[1] = 1; shiftTable[2] = 2; shiftTable[3] = 2;
	shiftTable[4] = 2; shiftTable[5] = 2; shiftTable[6] = 2; shiftTable[7] = 2;
	shiftTable[8] = 1; shiftTable[9] = 2; shiftTable[10] = 2; shiftTable[11] = 2;
	shiftTable[12] = 2; shiftTable[13] = 2; shiftTable[14] = 2; shiftTable[15] = 1;
	
	PC2[0]=14;  PC2[1]=17;  PC2[2]=11;  PC2[3]=24;  PC2[4]=1;   PC2[5]=5;
	PC2[6]=3;   PC2[7]=28;  PC2[8]=15;  PC2[9]=6;   PC2[10]=21; PC2[11]=10;
	PC2[12]=23; PC2[13]=19; PC2[14]=12; PC2[15]=4;  PC2[16]=26; PC2[17]=8;
	PC2[18]=16; PC2[19]=7;  PC2[20]=27; PC2[21]=20; PC2[22]=13; PC2[23]=2;
	PC2[24]=41; PC2[25]=52; PC2[26]=31; PC2[27]=37; PC2[28]=47; PC2[29]=55;
	PC2[30]=30; PC2[31]=40; PC2[32]=51; PC2[33]=45; PC2[34]=33; PC2[35]=48;
	PC2[36]=44; PC2[37]=49; PC2[38]=39; PC2[39]=56; PC2[40]=34; PC2[41]=53;
	PC2[42]=46; PC2[43]=42; PC2[44]=50; PC2[45]=36; PC2[46]=29; PC2[47]=32;
	
	P[0]=16;  P[1]=7;   P[2]=20;  P[3]=21;  P[4]=29;  P[5]=12;  P[6]=28;  P[7]=17;
	P[8]=1;   P[9]=15;  P[10]=23; P[11]=26; P[12]=5;  P[13]=18; P[14]=31; P[15]=10;
	P[16]=2;  P[17]=8;  P[18]=24; P[19]=14; P[20]=32; P[21]=27; P[22]=3;  P[23]=9;
	P[24]=19; P[25]=13; P[26]=30; P[27]=6;  P[28]=22; P[29]=11; P[30]=4;  P[31]=25;
	
SubBox1[0][0]='e';SubBox1[0][1]='4';SubBox1[0][2]='d';SubBox1[0][3]='1';SubBox1[0][4]='2';SubBox1[0][5]='f';SubBox1[0][6]='b';SubBox1[0][7]='8';SubBox1[0][8]='3';SubBox1[0][9]='a';SubBox1[0][10]='6';SubBox1[0][11]='c';SubBox1[0][12]='5';SubBox1[0][13]='9';SubBox1[0][14]='0';SubBox1[0][15]='7';
SubBox1[1][0]='0';SubBox1[1][1]='f';SubBox1[1][2]='7';SubBox1[1][3]='4';SubBox1[1][4]='e';SubBox1[1][5]='2';SubBox1[1][6]='d';SubBox1[1][7]='1';SubBox1[1][8]='a';SubBox1[1][9]='6';SubBox1[1][10]='c';SubBox1[1][11]='b';SubBox1[1][12]='9';SubBox1[1][13]='5';SubBox1[1][14]='3';SubBox1[1][15]='8';
SubBox1[2][0]='4';SubBox1[2][1]='1';SubBox1[2][2]='e';SubBox1[2][3]='8';SubBox1[2][4]='d';SubBox1[2][5]='6';SubBox1[2][6]='2';SubBox1[2][7]='b';SubBox1[2][8]='f';SubBox1[2][9]='c';SubBox1[2][10]='9';SubBox1[2][11]='7';SubBox1[2][12]='3';SubBox1[2][13]='a';SubBox1[2][14]='5';SubBox1[2][15]='0';	
SubBox1[3][0]='f';SubBox1[3][1]='c';SubBox1[3][2]='8';SubBox1[3][3]='2';SubBox1[3][4]='4';SubBox1[3][5]='9';SubBox1[3][6]='1';SubBox1[3][7]='7';SubBox1[3][8]='5';SubBox1[3][9]='b';SubBox1[3][10]='3';SubBox1[3][11]='e';SubBox1[3][12]='a';SubBox1[3][13]='0';SubBox1[3][14]='6';SubBox1[3][15]='d';

SubBox2[0][0]='f';SubBox2[0][1]='1';SubBox2[0][2]='8';SubBox2[0][3]='e';SubBox2[0][4]='6';SubBox2[0][5]='b';SubBox2[0][6]='3';SubBox2[0][7]='4';SubBox2[0][8]='9';SubBox2[0][9]='7';SubBox2[0][10]='2';SubBox2[0][11]='d';SubBox2[0][12]='c';SubBox2[0][13]='0';SubBox2[0][14]='5';SubBox2[0][15]='a';
SubBox2[1][0]='3';SubBox2[1][1]='d';SubBox2[1][2]='4';SubBox2[1][3]='7';SubBox2[1][4]='f';SubBox2[1][5]='2';SubBox2[1][6]='8';SubBox2[1][7]='e';SubBox2[1][8]='c';SubBox2[1][9]='0';SubBox2[1][10]='1';SubBox2[1][11]='a';SubBox2[1][12]='6';SubBox2[1][13]='9';SubBox2[1][14]='b';SubBox2[1][15]='5';
SubBox2[2][0]='0';SubBox2[2][1]='e';SubBox2[2][2]='7';SubBox2[2][3]='b';SubBox2[2][4]='a';SubBox2[2][5]='4';SubBox2[2][6]='d';SubBox2[2][7]='1';SubBox2[2][8]='5';SubBox2[2][9]='8';SubBox2[2][10]='c';SubBox2[2][11]='6';SubBox2[2][12]='9';SubBox2[2][13]='3';SubBox2[2][14]='2';SubBox2[2][15]='f';
SubBox2[3][0]='d';SubBox2[3][1]='8';SubBox2[3][2]='a';SubBox2[3][3]='1';SubBox2[3][4]='3';SubBox2[3][5]='f';SubBox2[3][6]='4';SubBox2[3][7]='2';SubBox2[3][8]='b';SubBox2[3][9]='6';SubBox2[3][10]='7';SubBox2[3][11]='c';SubBox2[3][12]='0';SubBox2[3][13]='5';SubBox2[3][14]='e';SubBox2[3][15]='9';
	

SubBox3[0][0]='a';SubBox3[0][1]='0';SubBox3[0][2]='9';SubBox3[0][3]='e';SubBox3[0][4]='6';SubBox3[0][5]='3';SubBox3[0][6]='f';SubBox3[0][7]='5';SubBox3[0][8]='1';SubBox3[0][9]='d';SubBox3[0][10]='c';SubBox3[0][11]='7';SubBox3[0][12]='b';SubBox3[0][13]='4';SubBox3[0][14]='2';SubBox3[0][15]='8';
SubBox3[1][0]='d';SubBox3[1][1]='7';SubBox3[1][2]='0';SubBox3[1][3]='9';SubBox3[1][4]='3';SubBox3[1][5]='4';SubBox3[1][6]='6';SubBox3[1][7]='a';SubBox3[1][8]='2';SubBox3[1][9]='8';SubBox3[1][10]='5';SubBox3[1][11]='e';SubBox3[1][12]='c';SubBox3[1][13]='b';SubBox3[1][14]='f';SubBox3[1][15]='1';
SubBox3[2][0]='d';SubBox3[2][1]='6';SubBox3[2][2]='4';SubBox3[2][3]='9';SubBox3[2][4]='8';SubBox3[2][5]='f';SubBox3[2][6]='3';SubBox3[2][7]='0';SubBox3[2][8]='b';SubBox3[2][9]='1';SubBox3[2][10]='2';SubBox3[2][11]='c';SubBox3[2][12]='5';SubBox3[2][13]='a';SubBox3[2][14]='e';SubBox3[2][15]='7';
SubBox3[3][0]='1';SubBox3[3][1]='a';SubBox3[3][2]='d';SubBox3[3][3]='0';SubBox3[3][4]='6';SubBox3[3][5]='9';SubBox3[3][6]='8';SubBox3[3][7]='7';SubBox3[3][8]='4';SubBox3[3][9]='f';SubBox3[3][10]='e';SubBox3[3][11]='3';SubBox3[3][12]='b';SubBox3[3][13]='5';SubBox3[3][14]='2';SubBox3[3][15]='c';


SubBox4[0][0]='7';SubBox4[0][1]='d';SubBox4[0][2]='e';SubBox4[0][3]='3';SubBox4[0][4]='0';SubBox4[0][5]='6';SubBox4[0][6]='9';SubBox4[0][7]='a';SubBox4[0][8]='1';SubBox4[0][9]='2';SubBox4[0][10]='8';SubBox4[0][11]='5';SubBox4[0][12]='b';SubBox4[0][13]='c';SubBox4[0][14]='4';SubBox4[0][15]='f';
SubBox4[1][0]='d';SubBox4[1][1]='8';SubBox4[1][2]='b';SubBox4[1][3]='5';SubBox4[1][4]='6';SubBox4[1][5]='f';SubBox4[1][6]='0';SubBox4[1][7]='3';SubBox4[1][8]='4';SubBox4[1][9]='7';SubBox4[1][10]='2';SubBox4[1][11]='c';SubBox4[1][12]='1';SubBox4[1][13]='a';SubBox4[1][14]='e';SubBox4[1][15]='9';
SubBox4[2][0]='a';SubBox4[2][1]='6';SubBox4[2][2]='9';SubBox4[2][3]='0';SubBox4[2][4]='c';SubBox4[2][5]='b';SubBox4[2][6]='7';SubBox4[2][7]='d';SubBox4[2][8]='f';SubBox4[2][9]='1';SubBox4[2][10]='3';SubBox4[2][11]='e';SubBox4[2][12]='5';SubBox4[2][13]='2';SubBox4[2][14]='8';SubBox4[2][15]='4';
SubBox4[3][0]='3';SubBox4[3][1]='f';SubBox4[3][2]='0';SubBox4[3][3]='6';SubBox4[3][4]='a';SubBox4[3][5]='1';SubBox4[3][6]='d';SubBox4[3][7]='8';SubBox4[3][8]='9';SubBox4[3][9]='4';SubBox4[3][10]='5';SubBox4[3][11]='b';SubBox4[3][12]='c';SubBox4[3][13]='7';SubBox4[3][14]='2';SubBox4[3][15]='e';
	

SubBox5[0][0]='2';SubBox5[0][1]='c';SubBox5[0][2]='4';SubBox5[0][3]='1';SubBox5[0][4]='7';SubBox5[0][5]='a';SubBox5[0][6]='b';SubBox5[0][7]='6';SubBox5[0][8]='8';SubBox5[0][9]='5';SubBox5[0][10]='3';SubBox5[0][11]='f';SubBox5[0][12]='d';SubBox5[0][13]='0';SubBox5[0][14]='e';SubBox5[0][15]='9';
SubBox5[1][0]='e';SubBox5[1][1]='b';SubBox5[1][2]='2';SubBox5[1][3]='c';SubBox5[1][4]='4';SubBox5[1][5]='7';SubBox5[1][6]='d';SubBox5[1][7]='1';SubBox5[1][8]='5';SubBox5[1][9]='0';SubBox5[1][10]='f';SubBox5[1][11]='a';SubBox5[1][12]='3';SubBox5[1][13]='9';SubBox5[1][14]='8';SubBox5[1][15]='6';
SubBox5[2][0]='4';SubBox5[2][1]='2';SubBox5[2][2]='1';SubBox5[2][3]='b';SubBox5[2][4]='a';SubBox5[2][5]='d';SubBox5[2][6]='7';SubBox5[2][7]='8';SubBox5[2][8]='f';SubBox5[2][9]='9';SubBox5[2][10]='c';SubBox5[2][11]='5';SubBox5[2][12]='6';SubBox5[2][13]='3';SubBox5[2][14]='0';SubBox5[2][15]='e';
SubBox5[3][0]='b';SubBox5[3][1]='8';SubBox5[3][2]='c';SubBox5[3][3]='7';SubBox5[3][4]='1';SubBox5[3][5]='e';SubBox5[3][6]='2';SubBox5[3][7]='d';SubBox5[3][8]='6';SubBox5[3][9]='f';SubBox5[3][10]='0';SubBox5[3][11]='9';SubBox5[3][12]='a';SubBox5[3][13]='4';SubBox5[3][14]='5';SubBox5[3][15]='3';
	

SubBox6[0][0]='c';SubBox6[0][1]='1';SubBox6[0][2]='a';SubBox6[0][3]='f';SubBox6[0][4]='9';SubBox6[0][5]='2';SubBox6[0][6]='6';SubBox6[0][7]='8';SubBox6[0][8]='0';SubBox6[0][9]='d';SubBox6[0][10]='3';SubBox6[0][11]='4';SubBox6[0][12]='e';SubBox6[0][13]='7';SubBox6[0][14]='5';SubBox6[0][15]='b';
SubBox6[1][0]='a';SubBox6[1][1]='f';SubBox6[1][2]='4';SubBox6[1][3]='2';SubBox6[1][4]='7';SubBox6[1][5]='c';SubBox6[1][6]='9';SubBox6[1][7]='5';SubBox6[1][8]='6';SubBox6[1][9]='1';SubBox6[1][10]='d';SubBox6[1][11]='e';SubBox6[1][12]='0';SubBox6[1][13]='b';SubBox6[1][14]='3';SubBox6[1][15]='8';
SubBox6[2][0]='9';SubBox6[2][1]='e';SubBox6[2][2]='f';SubBox6[2][3]='5';SubBox6[2][4]='2';SubBox6[2][5]='8';SubBox6[2][6]='c';SubBox6[2][7]='3';SubBox6[2][8]='7';SubBox6[2][9]='0';SubBox6[2][10]='4';SubBox6[2][11]='a';SubBox6[2][12]='1';SubBox6[2][13]='d';SubBox6[2][14]='b';SubBox6[2][15]='6';
SubBox6[3][0]='4';SubBox6[3][1]='3';SubBox6[3][2]='2';SubBox6[3][3]='c';SubBox6[3][4]='9';SubBox6[3][5]='5';SubBox6[3][6]='f';SubBox6[3][7]='a';SubBox6[3][8]='b';SubBox6[3][9]='e';SubBox6[3][10]='1';SubBox6[3][11]='7';SubBox6[3][12]='6';SubBox6[3][13]='0';SubBox6[3][14]='8';SubBox6[3][15]='d';
	

SubBox7[0][0]='4';SubBox7[0][1]='b';SubBox7[0][2]='2';SubBox7[0][3]='e';SubBox7[0][4]='f';SubBox7[0][5]='0';SubBox7[0][6]='8';SubBox7[0][7]='d';SubBox7[0][8]='3';SubBox7[0][9]='c';SubBox7[0][10]='9';SubBox7[0][11]='7';SubBox7[0][12]='5';SubBox7[0][13]='a';SubBox7[0][14]='6';SubBox7[0][15]='1';
SubBox7[1][0]='d';SubBox7[1][1]='0';SubBox7[1][2]='b';SubBox7[1][3]='7';SubBox7[1][4]='4';SubBox7[1][5]='9';SubBox7[1][6]='1';SubBox7[1][7]='a';SubBox7[1][8]='e';SubBox7[1][9]='3';SubBox7[1][10]='5';SubBox7[1][11]='c';SubBox7[1][12]='2';SubBox7[1][13]='f';SubBox7[1][14]='8';SubBox7[1][15]='6';
SubBox7[2][0]='1';SubBox7[2][1]='4';SubBox7[2][2]='b';SubBox7[2][3]='d';SubBox7[2][4]='c';SubBox7[2][5]='3';SubBox7[2][6]='7';SubBox7[2][7]='e';SubBox7[2][8]='a';SubBox7[2][9]='f';SubBox7[2][10]='6';SubBox7[2][11]='8';SubBox7[2][12]='0';SubBox7[2][13]='5';SubBox7[2][14]='9';SubBox7[2][15]='2';
SubBox7[3][0]='6';SubBox7[3][1]='b';SubBox7[3][2]='d';SubBox7[3][3]='8';SubBox7[3][4]='1';SubBox7[3][5]='4';SubBox7[3][6]='a';SubBox7[3][7]='7';SubBox7[3][8]='9';SubBox7[3][9]='5';SubBox7[3][10]='0';SubBox7[3][11]='f';SubBox7[3][12]='e';SubBox7[3][13]='2';SubBox7[3][14]='3';SubBox7[3][15]='c';	


SubBox8[0][0]='d';SubBox8[0][1]='2';SubBox8[0][2]='8';SubBox8[0][3]='4';SubBox8[0][4]='6';SubBox8[0][5]='f';SubBox8[0][6]='b';SubBox8[0][7]='1';SubBox8[0][8]='a';SubBox8[0][9]='9';SubBox8[0][10]='3';SubBox8[0][11]='e';SubBox8[0][12]='5';SubBox8[0][13]='0';SubBox8[0][14]='c';SubBox8[0][15]='7';
SubBox8[1][0]='1';SubBox8[1][1]='f';SubBox8[1][2]='d';SubBox8[1][3]='8';SubBox8[1][4]='a';SubBox8[1][5]='3';SubBox8[1][6]='7';SubBox8[1][7]='4';SubBox8[1][8]='c';SubBox8[1][9]='5';SubBox8[1][10]='6';SubBox8[1][11]='b';SubBox8[1][12]='0';SubBox8[1][13]='e';SubBox8[1][14]='9';SubBox8[1][15]='2';
SubBox8[2][0]='7';SubBox8[2][1]='b';SubBox8[2][2]='4';SubBox8[2][3]='1';SubBox8[2][4]='9';SubBox8[2][5]='c';SubBox8[2][6]='e';SubBox8[2][7]='2';SubBox8[2][8]='0';SubBox8[2][9]='6';SubBox8[2][10]='a';SubBox8[2][11]='d';SubBox8[2][12]='f';SubBox8[2][13]='3';SubBox8[2][14]='5';SubBox8[2][15]='8';
SubBox8[3][0]='2';SubBox8[3][1]='1';SubBox8[3][2]='e';SubBox8[3][3]='7';SubBox8[3][4]='4';SubBox8[3][5]='a';SubBox8[3][6]='8';SubBox8[3][7]='d';SubBox8[3][8]='f';SubBox8[3][9]='c';SubBox8[3][10]='9';SubBox8[3][11]='0';SubBox8[3][12]='3';SubBox8[3][13]='5';SubBox8[3][14]='6';SubBox8[3][15]='b';
	}
}
