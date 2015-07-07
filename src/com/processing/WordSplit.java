package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class WordSplit {

	public static void main(String[] args) throws IOException
	{
		WordSplit ws = new WordSplit();
		String path = "sub_graph/mrg_str.txt";
		String toPath = "sub_graph/keys0422.txt";
		String tNumsInGraphPath = "sub_graph/termNumsInGraph.new.txt";
		ws.getFeatures(path, toPath, tNumsInGraphPath);
	}

	/**
	 * 对文本进行分词并提取特征
	 */
	public void getFeatures(String path, String toPath, String tNumsInGraphPath)
			throws IOException
	{
		HashMap<String, String> hm;
		HashMap<String, String> hMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		BufferedWriter bw = new BufferedWriter(new FileWriter(toPath));
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(tNumsInGraphPath));

		String regEx_html = "http+://[^s]+\\w*/\\w*";
		Pattern p = Pattern.compile(regEx_html);

		String line = "";
		Analyzer analyzer = new IKAnalyzer(true);
		System.out.println("当前使用的分词器：" + analyzer.getClass().getSimpleName());
		int n = 0;
		while ((line = br.readLine()) != null)
		{
			int termNum = 0;
			hm = new HashMap<String, String>();
			System.out.println(n);
			n++;
			String[] s = line.split("::");
			Matcher m = p.matcher(s[1]);
			s[1] = m.replaceAll("").replaceAll("。|#|\\.", "");

			// 创建分词对象
			StringReader reader = new StringReader(s[1]);
			// 分词
			TokenStream ts = analyzer.tokenStream("content", reader);
			CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
			try
			{
				while (ts.incrementToken())
				{
					String termStr = term.toString();
					if (termStr.length() != 1)
					{
						int count = 1;
						if (hm.containsKey(termStr))
						{
							termNum++;
							count = Integer.parseInt(hm.get(termStr)) + 1;
							hm.put(termStr, count + "");
						}
						else
						{
							termNum++;
							hm.put(termStr, "1");
						}
					}
				}
				bw1.write(s[0] + "," + termNum + "\r\n");
				// 遍历Map
				Iterator iter = hm.entrySet().iterator();
				while (iter.hasNext())
				{
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
//					if (((String) key).length() != 1)
					{
						if (hMap.containsKey(key))
						{
							hMap.put((String) key, hMap.get(key) + ";" + s[0]
									+ "," + hm.get(key));
						}
						else
						{
							hMap.put((String) key, s[0] + "," + (String) val);
						}
						// bw.write(key + " " + s[0] + "," + val + "\r\n");
					}
				}
			} catch (IOException e)
			{
				System.out.println("error");
				e.printStackTrace();
			}
			reader.close();
		}
		System.out.println("特征词提取成功，共提取出特征词" + hMap.size() + "个");
		// 遍历Map
		Iterator iter = hMap.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			// if (((String) key).length() != 1)
			// {
			bw.write(key + " " + val + "\r\n");
			// }
		}
		br.close();
		bw.close();
		bw1.close();
	}
}
