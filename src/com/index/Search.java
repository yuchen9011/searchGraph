package com.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 暂时不用，改用 CopyOfSearch类
 * @author zyc
 *
 */

public class Search {

	public static void main(String[] args) throws IOException
	{
//		Search s = new Search();
////		String str = "小米SR  二雨在六上绿旗  陳家希JAZZ 败家女7  寄语翩跹";
		String str = "录用 忘惜-小东  鲁东大学新媒体平台  RUC李峰  蒋铁牛";
		
		ArrayList<String> al = Search.test(str);
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));
		}
	}
	
	public static ArrayList<String> test(String str)
	{
		Search s = new Search();
		String scorePath = "sub_graph/scoreIR.new.txt";
		String graphPath = "sub_graph/mrg_num.txt";
		
		ArrayList<String> al = s.getRecord(str, scorePath, 3);
		ArrayList<String> resultList = s.fun(al,graphPath);
		
		return resultList;
	}
	
	//132::14::96,99,101,123,131,132,133,14784,27151,44346,63778,63785,63788,63789
	public ArrayList<String> fun(ArrayList<String> al, String graphPath)
	{
		ArrayList<String> result = new ArrayList<String>();
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile(graphPath, "r");
			for (int i = 0; i < al.size(); i++)
			{
				String alStr = al.get(i);
				String[] arrStrAl = alStr.split(" ");       // 图编号
				raf.seek(Integer.parseInt(arrStrAl[1]));  	// 定位指针位置
				String graphId = raf.readLine();       	  	// 子图的结点集合
				String[] graphIdArr = graphId.split("::|,");
				StringBuffer sb = new StringBuffer();
				for (int j = 2; j < graphIdArr.length; j++)
				{
					sb.append(graphIdArr[j]+" ");
				}
				result.add(sb+"");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				raf.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 用来得到包含 n个及以上关键词的图记录的数组             // 图编号   位置   包含个数  分数
	 */
	public ArrayList<String> getRecord(String keysStr, String scorePath, int n)
	{
		Search search = new Search();
		ArrayList<String> al = new ArrayList<String>();
		double[][] array = search.searchManyKeys(keysStr, scorePath);
		String str;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i][2] >= n)
			{
				str = (int) array[i][0] + " " + (int) array[i][1] 
										+ " " + (int) array[i][2] + " " + array[i][3];
				al.add(str);
			}
		}
		return al;
	}

	/**
	 * 得到包含关键字的记录的二位数组 // 图编号 位置 包含个数 分数
	 */
	public double[][] searchManyKeys(String keysStr, String indexStrPath)
	{
		String[] keyWords = keysStr.split("\\s+"); // 用户输入的关键词，以空格分隔
		BufferedReader br = null;
		double[][] array = null;
		HashMap<String, String> hMap = new HashMap<String, String>();
		HashMap<String, Integer> graphIdMap = new HashMap<String, Integer>();
		HashMap<String, Double> graphScoreMap = new HashMap<String, Double>();
		try
		{
			br = new BufferedReader(new FileReader(indexStrPath));
			String line = "";
			// 读取索引信息
			while ((line = br.readLine()) != null)
			{
				String[] arr = line.split(" ");
				hMap.put(arr[0], arr[1]);
			}
			for (int i = 0; i < keyWords.length; i++)
			{
				String str = hMap.get(keyWords[i]);
				String[] arrStr = str.split(";"); // 2,138243,0.56;1965,2039808,0.97;
				for (int j = 0; j < arrStr.length; j++)
				{
					String[] arrStr1 = arrStr[j].split(","); // 2,138243,0.56;1965,2039808,0.97;
					Integer li = 0;
					Double score = (double) 0;
					String newStr = arrStr1[0] + "," + arrStr1[1];
					if ((li = graphIdMap.get(newStr)) != null)
					{
						li += 1;
						graphIdMap.put(newStr, li);
					}
					else
					{
						graphIdMap.put(newStr, 1);
					}
					if ((score = graphScoreMap.get(newStr)) != null)
					{
						score = score + Double.parseDouble(arrStr1[2]);
						graphScoreMap.put(newStr, score);
					}
					else
					{
						graphScoreMap.put(newStr,
								Double.parseDouble(arrStr1[2]));
					}
				}
			}

			array = new double[graphScoreMap.size()][4];
			// 遍历Map
			Iterator iter = graphIdMap.entrySet().iterator();
			int t = 0;
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				// System.out.println(key + " " + val + "," +
				// graphScoreMap.get(key));
				String[] arr = (key + "").split(",");
				array[t][0] = Double.parseDouble(arr[0]);
				array[t][1] = Double.parseDouble(arr[1]);
				array[t][2] = Double.parseDouble(val + "");
				array[t][3] = graphScoreMap.get(key);
				t++;
			}
			ToolsClass.sort(array, new int[] { 2, 3 });
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return array;
	}

	// 标记首个字符的位置及行数（未完成）
	public void oldFun(String source)
	{
		RandomAccessFile raf = null;
		try
		{
			raf = new RandomAccessFile(source, "r");
			String line = "";
			String line1 = "";
			int start = 0;
			while ((line = new String(raf.readLine().getBytes("8859_1"),
					"UTF-8")) != null)
			{
				int count = 1;
				int end = 0;
				char c = line.charAt(0);
				while ((line1 = new String(raf.readLine().getBytes("8859_1"),
						"UTF-8")) != null)
				{
					start = (int) raf.getFilePointer();
					{
						if (line1.charAt(0) == c)
						{
							end = (int) raf.getFilePointer();
							count++;
						}
						else
						{
							break;
						}
					}
					if (count == 1)
					{
						end = start;
					}
				}
				System.out.println(line);
				System.out.println("end=" + end);
				System.out.println("count=" + count);
				System.out.println("----------------------");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				raf.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getWordCount(String s)
	{
		int length = 0;
		for (int i = 0; i < s.length(); i++)
		{
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255) length++;
			else length += 2;

		}
		return length;
	}
}
