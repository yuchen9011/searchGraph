package com.commom;

import java.io.RandomAccessFile;
/**
 * 将大文件分割成若干个小文件
 * 用于切分节点关系的文件（创建图时节约时间）
 */
public class SplitTxt {
	
	final static long gap=10485760/2;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		SplitTxt test=new SplitTxt();
		String name="userrelation";
		test.splitTxt(name);
	}
	
	public void splitTxt(String name) throws Exception{
		
//		String path="D:\\J2seWorkspace\\searchGraph\\"+name+".txt";
		String path= name+".txt";
		RandomAccessFile raf = new RandomAccessFile(path, "r");
		String lr=null;
		int count=0;
		while((lr=raf.readLine())!=null)
		{
			count++;
			RandomAccessFile fw=new RandomAccessFile("D:\\J2seWorkspace\\searchGraph\\sourceData\\"+name+"\\"+name+"_"+count+".txt","rw");
			while(fw.length()<=gap && lr!=null)
			{
				fw.writeBytes(lr+"\r\n");
				lr=raf.readLine();
			}
		}
		
	}

}
