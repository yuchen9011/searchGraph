package com.index;

public class DemoString2Binary {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DemoString2Binary ds2b = new DemoString2Binary();
		String s = "";
		System.out.println(ds2b.StrToBinstr(s));
	}

	// 将字符串转换成二进制字符串（汉子为两个字符，每个字符8位）
	private String StrToBinstr(String str) {

		
		
		char[] charArr = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < charArr.length; i++) {
			
			String binary = Integer.toBinaryString(charArr[i]);
			int size = binary.length();
			String s = binary + "";
			if (size < 8) 
			{
				s = "00000000".substring(0, 8 - size) + binary;
			} 
			else if (size < 16 && size != 8) 
			{
				s = "00000000".substring(size - 8, 8) + binary;
			}
			sb.append(s);
		}

		return sb+"";
	}
}
