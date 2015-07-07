package com.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.cypher.internal.commands.Count;

import scala.Enumeration.Value;

import com.sun.crypto.provider.HmacPKCS12PBESHA1;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String key = "魅族社区 未来 历史";	
//		ArrayList<String> al = getCountList(key);
//		for (int i = 0; i < al.size(); i++) {
//			System.out.println(al.get(i));
//		}
		
		
		Test t = new Test();
		t.mixKeys("sub_graph/scoreIR.new.txt", "sub_graph/score_mix.txt");
	}
	
	
	public void mixKeys(String fileName, String toFileName)
	{
		BufferedReader br = null;
		BufferedWriter bw = null;
		ArrayList<String> al = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			bw = new BufferedWriter(new FileWriter(toFileName));
			String line = "";
			while((line = br.readLine()) != null)
			{
				al.add(line);
			}
			long start = System.currentTimeMillis();	//开始时间
			int size = al.size();  //集合大小
			HashMap<String, String> hm,hm1 = null;   // hm第一个关键字的信息
													 // hm1 两个关键字都有的图的信息
			String[] str1,str2,arr1,arr2;
			String[] graphContent1,graphContent2;
			String graphId1,graphId2;
			String allKey,resultScore;
			double score;
			DecimalFormat df = new DecimalFormat("#.##");
			for (int i = 0; i < size-1; i++) 
			{
				System.out.println(i);
				long start1 = System.currentTimeMillis();	//开始时间
				hm = new HashMap<String, String>();
				allKey = "";
				str1 = al.get(i).split(" ");
				arr1 = str1[1].split(";");
				for (int k = 0; k < arr1.length; k++)
				{
					graphContent1 = arr1[k].split(",");
					graphId1 = graphContent1[0]+","+graphContent1[1];
					hm.put(graphId1, graphContent1[2]);
				}
				for (int j = i+1; j < size; j++) 
				{
					hm1 = new HashMap<String, String>();
					str2 = al.get(j).split(" ");   // 得到关键字   
					allKey = str1[0] + "," + str2[0];       // 组合关键字
					arr2 = str2[1].split(";");
					for (int k = 0; k < arr2.length; k++) {
						graphContent2 = arr2[k].split(",");
						graphId2 = graphContent2[0] + "," + graphContent2[1];
						String temp = hm.get(graphId2);
						if (temp != null)
						{
							score = Double.parseDouble(temp) + Double.parseDouble(graphContent2[2]);      
							resultScore = df.format(score);
							hm1.put(graphId2, resultScore);
						}
					}
					if (!hm1.isEmpty()) {
						bw.write(allKey+" ");
						Set set = hm1.entrySet();
						Iterator it = hm1.entrySet().iterator();
						while(it.hasNext())
						{
							Entry entry = (Entry) it.next();
							bw.write((String) entry.getKey() + "," + (String) entry.getValue() + ";");
						}
						bw.write("\r\n");
					}
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("总时间为:"+ (end - start));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				bw.close();
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	
/**
 * 获得包含关键字的记录集合
 * @param keyWords   用户输入的多个关键词，以空格间隔
 * @return
 */
	public static ArrayList<String> getCountList(String keyWords) {
		String[] keys = keyWords.split(" ");
		BufferedReader br = null;
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		try 
		{
			for (int i = 0; i < keys.length; i++) 
			{
				SimHash simHash = new SimHash(keys[i]);
				List<BigInteger> list = simHash.subByDistance1(simHash, 7);
				for (int j = 0; j < list.size(); j++) {
					// System.out.print(i+"::"+ list.get(i)+",");
					String fileName = "sub_graph/index" + j + "/" + list.get(j) + ".txt";
//					System.out.println(fileName);
					br = new BufferedReader(new FileReader(fileName));
					String line = "";
					while ((line = br.readLine()) != null) 
					{
						String[] readStr = line.split(" ");
						if (readStr[0].contains(keys[i]) || keys[i].contains(readStr[0])) 
						{
							if (!al.contains(readStr[0])) 
							{
								al.add(readStr[0]);
								result.add(line);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
		return result;
	}

}
