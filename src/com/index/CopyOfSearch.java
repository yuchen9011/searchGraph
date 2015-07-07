package com.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.commom.ModelGraph;
import com.commom.UserImpl;
import com.commom.WeiboImpl;
import com.processing.ProcessContent;

public class CopyOfSearch {

	private int NumOfNode = 147808;
	private String FileName_Score = "sub_graph/scoreIR.new.txt";
	private String FileName_NUM_MAX_R_GRAPH = "sub_graph/mrg_num.txt";
	
	
	public static void main(String[] args) throws IOException {

		String DB_PATH = "sub_graph/";
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH); //开启数据库需要568ms
		
		String str = "小米 魅族 华为 微博";
		
		CopyOfSearch s = new CopyOfSearch();
		ArrayList<String> al = s.getContainString(str, 3, graphDb);
		for (int i = 0; i < al.size(); i++) 
		{
			System.out.println(i + "::" + al.get(i));
		}
	}

	/**
	 * 获得包含关键字的微博及用户记录
	 * @param str 用户输入的多关键词，以空格间隔
	 * @param n   所查询到的图里所包含用户输入的关键字个数
	 * @return
	 */
	public ArrayList<String> getContainString(String str, int n,GraphDatabaseService graphDb) 
	{
		ArrayList<String> aList = new ArrayList<String>();
		boolean[] boolArr = new boolean[NumOfNode];
		String[] sss = str.split("\\s+");
//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(ModelGraph.DB_PATH); //开启数据库需要568ms
		CopyOfSearch s = new CopyOfSearch();
		ArrayList<String> list = s.getRecord(str, FileName_Score, n);
		ArrayList<String> al = s.getGraphIdMessages(list, FileName_NUM_MAX_R_GRAPH);	
		// 循环所用 1500ms
		for (int i = 0; i < al.size(); i++) 
		{
			String[] arr = al.get(i).split(" ");
			for (int j = 0; j < arr.length; j++) 
			{
				int id = Integer.parseInt(arr[j]);
				boolean isHave = false;
				if (boolArr[id] == false) 
				{
					boolArr[id] = true;
					Node node = graphDb.getNodeById(id);
					if (ProcessContent.isUserNode(node)) 
					{
						UserImpl user = new UserImpl(node);
						String userString = user.getContent();
						for (int k = 0; k < sss.length; k++) 
						{
							if (userString.contains(sss[k])) 
							{
								isHave = true;
							}
						}
						if (isHave) 
						{
							aList.add(userString);
						}
					} 
					else 
					{
						WeiboImpl weibo = new WeiboImpl(node);
						String weiboString = weibo.getContent();
						for (int k = 0; k < sss.length; k++) 
						{
							if (weiboString.contains(sss[k])) 
							{
								isHave = true;
							}
						}
						if (isHave) 
						{
							aList.add(weiboString);
						}
					}
				}
			}
		}
		//关闭数据库需要70ms
		graphDb.shutdown();
		return aList;
	}

	// 132::14::96,99,101,123,131,132,133,14784,27151,44346,63778,63785,63788,63789
	/**
	 * 通过包含关键字的图记录，根据偏移量读取图的结点信息
	 * @param al   图记录 ，形式为： 图编号 图偏移量 包含的关键词数 得分
	 * @param graphPath   最大半径r图的文件    （484ms）
	 * @return
	 */
	public ArrayList<String> getGraphIdMessages(ArrayList<String> al, String graphPath) 
	{	
		ArrayList<String> result = new ArrayList<String>();
		RandomAccessFile raf = null;
		try 
		{
			raf = new RandomAccessFile(graphPath, "r");     //根据偏移量读取图的记录信息
			for (int i = 0; i < al.size(); i++) 
			{
				String alStr = al.get(i);
				String[] arrStrAl = alStr.split(" ");
				raf.seek(Integer.parseInt(arrStrAl[1]));  // 定位指针位置
				String graphId = raf.readLine(); 		  // 子图的结点集合
				String[] graphIdArr = graphId.split("::|,");
				StringBuffer sb = new StringBuffer();
				for (int j = 2; j < graphIdArr.length; j++) 
				{
					sb.append(graphIdArr[j] + " ");
				}
				result.add(sb + "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 用来得到包含 n 个及以上关键词的图记录的数组 // 图编号 位置 包含个数 分数
	 * @param keysStr   用户输入的关键词
	 * @param sourcePath 倒排索引表文件    (2ms)
	 * 
	 */
	public ArrayList<String> getRecord(String keysStr, String scorePath, int n) 
	{
		CopyOfSearch search = new CopyOfSearch();
		ArrayList<String> al = new ArrayList<String>();
		double[][] array = search.searchManyKeys(keysStr, scorePath);
		String str;
		for (int i = 0; i < array.length; i++) 
		{
			if (array[i][2] >= n) 
			{
				str = (int) array[i][0] + " " + (int) array[i][1] + " " + (int) array[i][2] + " " + array[i][3];
				al.add(str);
			}
		}
		return al;
	}

	/**
	 * 得到包含关键字的记录的二维数组 // 图编号 位置 包含个数 分数    （待修改） 
	 * @param keysStr   用户输入的关键词 
	 * @param indexStrPath 倒排索引表文件     （719ms）
	 */
	public double[][] searchManyKeys(String keysStr, String indexStrPath) {
		
		long start = System.currentTimeMillis();
		
		String[] keyWords = keysStr.split("\\s+"); // 用户输入的关键词，以空格分隔
		BufferedReader br = null;
		double[][] array = null;
		HashMap<String, String> hMap = new HashMap<String, String>();
		HashMap<String, Integer> graphIdMap = new HashMap<String, Integer>();
		HashMap<String, Double> graphScoreMap = new HashMap<String, Double>();
		try {
			br = new BufferedReader(new FileReader(indexStrPath));
			String line = "";
			// 读取索引信息
			while ((line = br.readLine()) != null) 
			{
				String[] arr = line.split(" ");
				hMap.put(arr[0], arr[1]);
			}
			// 多关键字查询
			for (int i = 0; i < keyWords.length; i++) 
			{
				String str = hMap.get(keyWords[i]);
				if (str != null) 
				{
					String[] arrStr = str.split(";"); 			 // str = 2,138243,0.56;1965,2039808,0.97;
					for (int j = 0; j < arrStr.length; j++) 
					{
						String[] arrStr1 = arrStr[j].split(","); // arrStr[j] = [2,138243,0.56];
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
							graphScoreMap.put(newStr, Double.parseDouble(arrStr1[2]));
						}
					}
				}
			}
			array = new double[graphScoreMap.size()][4];   // 存储记录
			// 遍历Map
			Iterator iter = graphIdMap.entrySet().iterator();
			int t = 0;
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				String[] arr = (key + "").split(",");
				array[t][0] = Double.parseDouble(arr[0]);
				array[t][1] = Double.parseDouble(arr[1]);
				array[t][2] = Double.parseDouble(val + "");
				array[t][3] = graphScoreMap.get(key);
				t++;
			}
			ToolsClass.sort(array, new int[] { 2, 3 });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("获取二维数组所用时间为 = "+(end-start) + "ms");
		return array;
	}

	// 标记首个字符的位置及行数（未完成）
	public void oldFun(String source) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(source, "r");
			String line = "";
			String line1 = "";
			int start = 0;
			while ((line = new String(raf.readLine().getBytes("8859_1"),"UTF-8")) != null) 
			{
				int count = 1;
				int end = 0;
				char c = line.charAt(0);
				while ((line1 = new String(raf.readLine().getBytes("8859_1"),"UTF-8")) != null) 
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
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getWordCount(String s) {
		int length = 0;
		for (int i = 0; i < s.length(); i++) {
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;

		}
		return length;
	}
}
