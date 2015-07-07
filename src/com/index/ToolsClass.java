package com.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

public class ToolsClass {

	public static void main(String[] args) {

//		ToolsClass.deleteAllIndex(7,64);
		double s = 1.2343;
		System.out.println(getTwoDecimal(s));
	}
	
	/**
	 * 删除文件夹及其下的所有的文件
	 */
	public static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	//读取结点的总个数
	public static int readNumOfNode(String path)
	{
		BufferedReader br = null;
		int numOfNodes = 0;
		try 
		{
			br = new BufferedReader(new FileReader(path));
			numOfNodes = Integer.parseInt(br.readLine());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally{
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return numOfNodes;
	}
	
	// double值保留两位小数
	public static String getTwoDecimal(double score)
	{
		DecimalFormat df = new DecimalFormat("#.00");  
		String result = df.format(score);
		return result;
	}
	
	/**
	 * 排序，先根据order[0]列比较，若相同则再比较order[1]列,依次类推,使用比较器实现
	 */
	public static void sort(double[][] ob, final int[] order)  
	{
		Arrays.sort(ob, new Comparator<Object>() {
			public int compare(Object o1, Object o2)
			{
				double[] one = (double[]) o1;
				double[] two = (double[]) o2;
				for (int i = 0; i < order.length; i++)
				{
					int k = order[i];
					if (one[k] < two[k])
					{
						return 1;
					}
					else if (one[k] > two[k])
					{
						return -1;
					}
					else
					{
						continue; // 如果按一条件比较结果相等，就使用第二个条件进行比较。
					}
				}
				return 0;
			}
		});
	}
	
	
	
	/**
	 * 删除局部散列哈希的index文件
	 */
	public static void deleteAllIndex(int distance, int bitCount) {
		int maxIndex = (int)Math.pow(2, bitCount/(distance+1));
//		System.out.println(maxIndex);
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j < maxIndex; j++) {
				File f = new File("sub_graph/index" + i + "/" + j + ".txt");
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}
	
	/**
	 * 删除局部散列哈希的index文件
	 */
	public static void deleteAllIndexFile(int distance, int bitCount,String indexFileDir) {
		int maxIndex = (int)Math.pow(2, bitCount/(distance+1));
		System.out.println(maxIndex);
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j < maxIndex; j++) {
				File f = new File(indexFileDir + "/index" + (distance+1) + "/" + j + ".txt");
				if (f.exists()) {
					f.delete();
				}
			}
		}
	}

	//将content追加写入fileName文件中
	public static void write(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打印数组到工作台
	 */
	public void printArray(double[][] array) {
		System.out.println("图编号" + "\t" + "位置" + "\t" + "关键字个数" + "\t" + "分数");
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if (j != 3) {
					System.out.print((int) array[i][j] + "\t");
				} else {
					System.out.print(array[i][j] + "\t");
				}
			}
			System.out.println();
		}
	}

	/**
	 * 将数组写入文件，第3列为分数，为double类型，其他为int类型
	 */
	public void writeArray(double[][] array, String toPath) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(toPath));
			bw.write("图编号" + "\t" + "位置" + "\t" + "包含个数" + "\t" + "分数" + "\r\n");
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[i].length; j++) {
					if (j != 3) {
						bw.write((int) array[i][j] + "\t");
					} else {
						bw.write(array[i][j] + "\t");
					}
				}
				bw.write("\r\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
