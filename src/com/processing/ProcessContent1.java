package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import scala.sys.process.processInternal;

import com.commom.SplitGraph;
import com.commom.UserImpl;
import com.commom.WeiboImpl;

public class ProcessContent1 {

	public static void main(String[] args) throws IOException
	{
		ProcessContent1 pc = new ProcessContent1();
		String src = "sub_graph/mrg_str_extends.txt";
		String toPath = "sub_graph/keys.new.txt";
		String termNumsInGraphPath = "sub_graph/termNumsInGraph.new.txt";
		HashMap<String, String> hm = pc.getFeaWords(src, toPath, termNumsInGraphPath);
	}

	/**
	 * 提取特征词，以及在各个图中的词频
	 */
	public HashMap<String, String> getFeaWords(String mrgStrPath, String TfInGraphPath ,String termNumsInGraphPath)
			throws IOException
	{
		BufferedReader readerMrgDoc = new BufferedReader(new FileReader(mrgStrPath));
		BufferedWriter writerTfInGraph = new BufferedWriter(new FileWriter(TfInGraphPath));
		BufferedWriter writerTnumInGraph = new BufferedWriter(new FileWriter(termNumsInGraphPath));
		String str = "";
		int t = 1;

		// String regEx_html = "http+://[^s]+\\w*/\\w*";
		// Pattern p = Pattern.compile(regEx_html);
		
		HashMap<String, String> hm;     //<关键字，词频>  临时存放每个图所含的tf
		HashMap<String, String> tfInGraph = new HashMap<String, String>();  //<关键字，（图编号，词频）>
		Analyzer analyzer = new IKAnalyzer(true);
		while ((str = readerMrgDoc.readLine()) != null)
		{
			HashSet<String> keysSet = new HashSet<String>();   //每个图中所包含的关键字集合（不重复）
			int termNum = 0;
//			System.out.println("t=" + t);
			t++;
			hm = new HashMap<String, String>();   
			String[] graphArr = str.split("::|&&");

			for (int i = 1; i < graphArr.length; i++)
			{
				String[] nodeArr = graphArr[i].split(" ");
				if (nodeArr[0].equals("u"))
				{
					for (int j = 1; j < nodeArr.length; j++)
					{
						int count;
						String key = nodeArr[j];
						keysSet.add(nodeArr[j]);
						if (hm.containsKey(key))
						{
							count = Integer.parseInt(hm.get(key)) + 1;
							hm.put(key, count + "");
						}
						else
						{
							hm.put(key, "1");
						}
					}
				}
				else
				{
					StringBuffer str1 = getChinese(graphArr[i]);
					// 创建分词对象
					StringReader reader = new StringReader(str1+"");
					// 分词
					TokenStream ts = analyzer.tokenStream("content", reader);
					CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);

					while (ts.incrementToken())
					{
						String termStr = term.toString();
						if (termStr.length() != 1)
						{
							keysSet.add(termStr);
							int count;
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
					}
					reader.close();
				}
			}
			writerTnumInGraph.write(graphArr[0] + "," + keysSet.size() + "\r\n");
			
			// 遍历Map
			Iterator iter = hm.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (tfInGraph.containsKey(key))
				{
					tfInGraph.put((String) key, tfInGraph.get(key) + ";" + graphArr[0]
							+ "," + hm.get(key));
				}
				else
				{
					tfInGraph.put((String) key, graphArr[0] + "," + (String) val);
				}
			}
		}
		System.out.println("特征词提取成功，共提取出特征词" + tfInGraph.size() + "个");
		// 遍历Map
		Iterator iter = tfInGraph.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			writerTfInGraph.write(key + " " + val + "\r\n");
		}
		analyzer.close();
		readerMrgDoc.close();
		writerTfInGraph.close();
		writerTnumInGraph.close();
		return tfInGraph;
	}
	
//	static	String chinese = "[\u4e00-\u9fa5]+";
	static	String chinese = "([\u4E00-\u9FA5]|[\uFE30-\uFFA0]|[\\。\\.\\#])+";
	static Pattern p = Pattern.compile(chinese);
	//提取中文
	public static  StringBuffer getChinese(String str)
	{
		Matcher m = p.matcher(str);
		StringBuffer str1 = new StringBuffer();
		while (m.find()) 
		{
			String s = m.group(0);
			str1.append(s);
		}
		return str1;
	}
	
	
	static String regEx_html = "http+://[^s]+\\w*/\\w*";//匹配   http://t.cn/8sm0eqk
	static String regEx_image = "\\[+[^x00-xff]+\\]";   // 匹配: [酷]
	static Pattern p2 = Pattern.compile(regEx_html);
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
			Matcher m = p2.matcher(str);
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
