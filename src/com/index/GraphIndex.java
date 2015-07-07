package com.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.CollationKey;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 此类计算关键字在图中的得分情况
 */
public class GraphIndex {
	
	final static double s = 0.2;

	public static void main(String[] args) throws Exception
	{
		String KEYWORDS = "sub_graph/keys.new.txt";
		String termNumsPath = "sub_graph/termNumsInGraph.new.txt";
		String scoreIRPath = "sub_graph/scoreIR.new.txt_new";
		GraphIndex gi = new GraphIndex();
		TreeMap<String, String> treeMap = gi.tfidf(KEYWORDS, termNumsPath, "sub_graph/mrg_num.txt", s);
		gi.mapWrite(treeMap, scoreIRPath);
	}

	public TreeMap<String, String> tfidf(String keysPath, String termNumsPath, String mrg_numPath,double s)
	{
		BufferedReader keysReader = null, termNumsReader = null;
		TreeMap<String, String> scoreMap = null;
		HashMap<String, String>termNumMap = null;
		HashMap<Integer, Integer> graphIndexMap = null;
		try
		{
			graphIndexMap = getGraphFileIndex(mrg_numPath);
			scoreMap = new TreeMap<String, String>();
			termNumMap = new HashMap<String, String>();
			termNumsReader = new BufferedReader(new FileReader(termNumsPath));
			keysReader = new BufferedReader(new FileReader(keysPath));
			String tline = "";
			int avg = 0;
			while ((tline = termNumsReader.readLine()) != null)
			{
				String[] tarr = tline.split(",");
				termNumMap.put(tarr[0], tarr[1]);
				avg += Integer.parseInt(tarr[1]);
			}
			int N = termNumMap.size();
			avg /= N;

			String line = "";
			while ((line = keysReader.readLine()) != null)
			{
				String[] arr = line.split(" |;");
				String keyword = arr[0];
				int NKI = arr.length - 1;
				StringBuffer sb = new StringBuffer();
				for (int i = 1; i <= NKI; i++)
				{
					String[] arr1 = arr[i].split(",");
					String graphID = arr1[0];
					int tf = Integer.parseInt(arr1[1]);
					double ntf = 1 + Math.log(1 + Math.log(1 + tf));
					double idf = Math.log((N + 1) / (NKI + 1));
					// the number of all terms in graph G
					double tl = Double.parseDouble(termNumMap.get(graphID));
					double scoreIR = (ntf * idf) / (1 - s + s * (tl / avg));
					DecimalFormat df = new DecimalFormat("0.##");
					String scIR = df.format(scoreIR);
					// System.out.println(graphIndexMap.get(graphID));
					sb.append(graphID)
							.append(",")
							.append(graphIndexMap.get(Integer.parseInt(graphID)))
							.append(",").append(scIR).append(";");
				}
				scoreMap.put(keyword, sb + "");
			}
		} catch (IOException e)
		{
			System.err.println("error");
			e.printStackTrace();
		} finally
		{
			try
			{
				termNumsReader.close();
				keysReader.close();
			} catch (Exception e)
			{
				System.err.println("close error");
				e.printStackTrace();
			}
		}
		System.out.println("tfidf 打分完毕");
		return scoreMap;
	}

	public void mapWrite(Map<String, String> hMap, String path)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(path));
			// 遍历Map
			Iterator iter = hMap.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				bw.write(key + " " + val + "\r\n");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
			} catch (IOException e)
			{
				System.err.println("close error");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 得出图所在的行标，以便很快定位到文件中的某行（即某个子图）
	 */
	public static HashMap<Integer, Integer> getGraphFileIndex(String graphPath)
	{
		RandomAccessFile raf = null;
		// BufferedWriter bw = null;
		HashMap<Integer, Integer> mp = null;
		try
		{
			raf = new RandomAccessFile(graphPath, "r");
			// bw = new BufferedWriter(new FileWriter("sub_graph/mrg_index111.txt"));
			mp = new HashMap<Integer, Integer>();
			int n = 0;
			String s = "";
			while ((s = raf.readLine()) != null)
			{
				String[] arr = s.split("::");
				mp.put(Integer.parseInt(arr[0]), n);
				n = (int) raf.getFilePointer();
			}
			/*******************************************************
			 * // 遍历Map Iterator iter = mp.entrySet().iterator(); int t = 0;
			 * while (iter.hasNext()) { HashMap.Entry entry = (HashMap.Entry)
			 * iter.next(); Object key = entry.getKey(); Object val =
			 * entry.getValue(); // System.out.print("key: "+key); //
			 * System.out.println("val: "+val); bw.write(key + "::" + val +
			 * "\r\n"); }
			 ********************************************************/
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				raf.close();
				// bw.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return mp;
	}
}

class myComparator implements Comparator {

	private Collator collator = Collator.getInstance();

	public int compare(Object o1, Object o2)
	{
		CollationKey key1 = collator.getCollationKey(o1.toString());// 要想不区分大小写进行比较用o1.toString().toLowerCase()
		CollationKey key2 = collator.getCollationKey(o2.toString());
		return key1.compareTo(key2);// 返回的分别为1,0,-1
									// 分别代表大于，等于，小于。要想按照字母降序排序的话
									// 加个“-”号
	}
}
