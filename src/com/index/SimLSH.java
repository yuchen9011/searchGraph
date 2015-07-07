package com.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SimLSH {

//	private final int distance = 7;
//	private final int hashBits = 64;

	public static void main(String[] args) {
		int distance = 7;
		int hashBits = 64;
		SimLSH simLSH = new SimLSH();
		String scoreIRPath = "sub_graph/scoreIR.new.txt";
		String hashTablePath = "sub_graph_bak_1";
		simLSH.hashInvertedList(scoreIRPath, hashTablePath, hashBits, distance);
	}

	/**
	 * 运用局部敏感哈希（SimHash）把倒排表分成若干个文件（文件以哈希值的某一部分命名）
	 * 
	 * @param sourchPath
	 * @param toPath
	 */
	public void hashInvertedList(String sourchPath, String toPath , int hashBits,int distance) {
		
		ToolsClass.deleteAllIndex(distance, hashBits);

		BufferedReader br = null;
		ArrayList<String> al = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(sourchPath));
			SimHash simHash;
			String line = "";
			int count=0;
			FileWriter fw = null;
			File file = null;
			while ((line = br.readLine()) != null) 
			{
				count++;
				System.out.println(count);
				String[] arr = line.split(" ");
				al.add(arr[0]);
				if (arr[0].length() <= 4) 
				{
					simHash = new SimHash(arr[0],true);
				}
				else 
				{
					simHash = new SimHash(arr[0]);
				}
				List<BigInteger> list = simHash.subByDistance1(simHash, distance);
				for (int i = 0; i < list.size(); i++) 
				{
					BigInteger hashId = list.get(i);
					file = new File(toPath +"/index"+(distance+1)+ "/index" + i + "/" + hashId + ".txt");
					if (!file.getParentFile().exists()) 
					{
						file.getParentFile().mkdirs();
					}
					if (!file.exists())
					{
						file.createNewFile();
					}
					fw = new FileWriter(file, true);
					fw.write(simHash.getStrSimHash()+" "+ line + "\r\n");  
					fw.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		System.out.println("倒排表哈希完成");
	}
}
