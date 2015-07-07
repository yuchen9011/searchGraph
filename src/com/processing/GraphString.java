package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.commom.GraphTools;
import com.commom.ModelGraph;
import com.commom.SplitGraph;
import com.commom.UserImpl;
import com.commom.WeiboImpl;
/**
 * 将子图的文本内容存入txt
 */
public class GraphString {

	

	public static void main(String[] args)
	{
		String READ_PATH = "sub_graph/mrg_num.txt";
		String WRITE_PATH = "sub_graph/mrg_str_extends.txt";
		String DB_PATH = "graph/graph_new.db";
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		GraphString gs = new GraphString();
		gs.getGraphStr(graphDb, READ_PATH, WRITE_PATH);
		graphDb.shutdown();
	}

	public void getGraphStr(GraphDatabaseService graphDb, String readStr, String writeStr)
	{
		BufferedReader br = null;
		BufferedWriter bw = null;
		try{
			br = new BufferedReader(new FileReader(readStr));
			bw = new BufferedWriter(new FileWriter(writeStr));

			String line = "";
			StringBuilder s;

			while ((line = br.readLine()) != null)
			{
				String[] idArr = line.split("::|,");
				s = new StringBuilder(idArr[0] + "::");
				UserImpl user;
				WeiboImpl weibo;
				for (int i = 2; i < idArr.length; i++)
				{
					Node node = graphDb.getNodeById(Integer.parseInt(idArr[i]));
					if (ProcessContent1.isUserNode(node))
					{
						user = new UserImpl(node);
						s.append("u"+" "+user.toString()+"&&");
					}
					else
					{
						weibo = new WeiboImpl(node);
						s.append(weibo.toString()+"&&");
					}
				}
				bw.write(s + "\r\n");
			}
			System.out.println("最大半径r图的文本信息获取成功");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				bw.close();
				br.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
