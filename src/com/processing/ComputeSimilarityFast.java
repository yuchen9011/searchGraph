package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
/**
 * 此类计算图相似性
 */
public class ComputeSimilarityFast {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fromPath = "sub_graph/max_r_graph.txt";
		String toPath = "sub_graph/score.txt";
		saveScore(fromPath, toPath);
	}
	
	
	public static void saveScore(String fromPath,String toPath)
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fromPath));
			ArrayList<ArrayList<String>> al = new ArrayList<ArrayList<String>>();
			ArrayList<String> al1;
			String line = "";
			while((line = br.readLine()) != null)
			{
				al1 = new ArrayList<String>();
				String[] str = line.split(",|::");
				for (int i = 2; i < str.length; i++) 
				{
					al1.add(str[i]);
				}
				al.add(al1);
			}
			computeAllSimilarty(al, toPath);
			
		} catch (IOException e) {
			System.out.println("can't find "+ fromPath);
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("the close error");
				e.printStackTrace();
			}
		}
	}
	
	public static void computeAllSimilarty(ArrayList<ArrayList<String>> graphList,String toPath){
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(toPath));
			String score = "";
			for (int i = 0; i < graphList.size(); i++) 
			{
				for (int j = i+1; j < graphList.size(); j++) 
				{
					score = computeSimilarity(graphList.get(i), graphList.get(j));
					if (Double.parseDouble(score) != 0) 
					{
						bw.write(i+","+j+","+score+"\r\n");
					}
				}
			}
		} catch (IOException e) {
			System.out.println("open file error");
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				System.out.println("the close error");
				e.printStackTrace();
			}
		}
	}

	//两个图的相似性
	public static String computeSimilarity(ArrayList<String> sets1,ArrayList<String> sets2)
	{
		int sets1Size = sets1.size();
		int sets2Size = sets2.size();
		sets1.retainAll(sets2);
		int similarSize = sets1.size();
		int allSize = sets1Size + sets2Size - similarSize;	
		double similarity = (double) similarSize / allSize;
		DecimalFormat df=new DecimalFormat("0.####");
		String st=df.format(similarity);
		return st;
	}
	
}
