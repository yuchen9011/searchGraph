package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.commom.SplitGraph;
import com.commom.UserImpl;
import com.commom.WeiboImpl;

//此类本作为分词，但处理速度太慢，弃之
public class ProcessContent {

	public static void main(String[] args) throws IOException
	{
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("graph/graph_test.db");
   
		getFeaWords(src, graphDb);
		graphDb.shutdown();
	}

	static String src = "sub_graph/max_r_graph.txt";
	private static void getFeaWords(String src, GraphDatabaseService graphDb)
			throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(src));
		BufferedWriter bw = new BufferedWriter(new FileWriter("sub_graph/keys"));
		String idString = "";
		int t = 1;

		String regEx_html = "http+://[^s]+\\w*/\\w*";
		Pattern p = Pattern.compile(regEx_html);
		HashMap<String, String> hm;
		HashMap<String, String> hMap = new HashMap<String, String>();
		while ((idString = br.readLine()) != null && t <= 10)
		{
			System.out.println(t);
			hm = new HashMap<String, String>();
			t++;
			String[] idArr = idString.split("::|,");
			Node node;
			UserImpl user;
			WeiboImpl weibo;
			Analyzer analyzer = new IKAnalyzer(true);
			for (int i = 2; i < idArr.length; i++)
			{
				System.out.println("i=" + i);
				int id = Integer.parseInt(idArr[i]);
				node = graphDb.getNodeById(id);
				String str;
				if (isUserNode(node))
				{
					user = new UserImpl(node);
					str = user.toString().replaceAll(" ", "");
				}
				else
				{
					weibo = new WeiboImpl(node);
					str = weibo.toString().replaceAll(" |#|//|\\[|\\]", "");
					Matcher m = p.matcher(str);
					str = m.replaceAll("");
				}
				// 下面开始分词
				// 创建分词对象
				StringReader reader = new StringReader(str);
				// 分词
				TokenStream ts = analyzer.tokenStream("content", reader);
				CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);

				while (ts.incrementToken())
				{
					int count = 1;
					if (hm.containsKey(term.toString()))
					{
						count = Integer.parseInt(hm.get(term.toString())) + 1;
						hm.put(term.toString(), count + "");
					}
					else
					{
						hm.put(term.toString(), "1");
					}
				}
				reader.close();
			}
			// 遍历Map
			Iterator iter = hm.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (hMap.containsKey(key))
				{
					hMap.put((String) key, hMap.get(key) + ";" + idArr[0] + ","
							+ hm.get(key));
				}
				else
				{
					hMap.put((String) key, idArr[0] + "," + (String) val);
				}
			}
		}
		// 遍历Map
		Iterator iter = hMap.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			bw.write(key + " " + val + "\r\n");
		}
		br.close();
		bw.close();
	}

	private static String regEx_html = "http+://[^s]+\\w*/\\w*";
	static String regEx_image = "\\[+[^x00-xff]+\\]"; // 匹配 [酷]
	static Pattern p = Pattern.compile(regEx_html);
	static Pattern p1 = Pattern.compile(regEx_image);

	/**
	 * 获取节点的内容
	 */
	private static String getNodeContent(Node node)
	{
		String str;
		if (isUserNode(node))
		{
			UserImpl user = new UserImpl(node);
			str = user.toString().replaceAll(" ", "");
		}
		else
		{
			WeiboImpl weibo = new WeiboImpl(node);
			str = weibo.toString().replaceAll(" |#|//|\\[|\\]", "");
			Matcher m = p.matcher(str);
			str = m.replaceAll("");
			Matcher s = p1.matcher(str);
			str = s.replaceAll("");
		}
		return str;
	}

	/**
	 * 判断节点是否为用户节点，是返回true，否返回false
	 */
	public static boolean isUserNode(Node node)
	{
		boolean flag = false;
		String id = (String) node.getProperty("id");
		if (id.length() <= 12)
		{
			flag = true;
		}
		return flag;
	}

}
