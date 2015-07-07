package com.commom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

import scala.inline;

public class SplitGraph {

	// 总节点个数
	final static int AllNodeSize = 147808;
	// 记录第i个节点是否被访问过
	final static boolean[] visited = new boolean[AllNodeSize + 1];

	public static void main(String[] args) throws IOException
	{
		SplitGraph sg = new SplitGraph();

		//GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("graph/graph_new.db"); 
		//getRGraph(graphDb,"sub_graph/r_graph_new_1_2.txt", 2);
		//graphDb.shutdown();
		
		String rGraph = "sub_graph/r_graph.txt";
		sg.getMaxRGraphToTxt(rGraph, "sub_graph/max_r_graph.txt.bak");
	}

	/**
	 * 简单划分 获取以i为中心节点的深度为2的图数据
	 */
	private static String splitGraph(GraphDatabaseService graphDb, int i)
	{
		StringBuffer s = new StringBuffer();
		String s1 = "";
		if (!visited[i])
		{
			ArrayList<Node> nodeList = new ArrayList<Node>();
			TraversalDescription td = Traversal.description()
					.relationships(RelTypes.KNOWS).depthFirst()
					.evaluator(Evaluators.excludeStartPosition())
					.evaluator(Evaluators.toDepth(2));
			Node node = graphDb.getNodeById(i);
			Traverser traverse = td.traverse(node);

			for (Node nodes : traverse.nodes())
			{
				int id = (int) nodes.getId();
				if (!visited[id])
				{
					s.append(id).append(",");
					visited[id] = true;
				}
			}
			if (!s.equals(""))
			{
				s1 = i +"::"+ s;
				s1 = s1.substring(0, s1.length() - 1);
			}
			visited[i] = true;
		}
		return s1;
	}

	/**
	 * 将子图存入文档(包含的节点id)
	 */
	public void subGraphToTxt(GraphDatabaseService graphDb, String path)
			throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		for (int i = 1; i <= AllNodeSize; i++)
		{
			System.out.println(i);
			String graphStr = splitGraph(graphDb, i);
			if (graphStr != "")
			{
				bw.write(graphStr + "\r\n");
			}
		}
		bw.close();
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

	/**
	 * 判断节点是否为微博节点，是返回true，否返回false
	 */
	public static boolean isWeiboNode(Node node)
	{
		boolean flag = false;
		String id = (String) node.getProperty("id");
		if (id.length() >= 16)
		{
			flag = true;
		}
		return flag;
	}

	
	/**
	 * 判断图是否是R半径图,nodeId 图起始节点的id bs遍历深度 (大于等于2)
	 */

	boolean[] isvisit = new boolean[AllNodeSize + 1];
	boolean[] has = new boolean[AllNodeSize + 1];

	public void getRGraph(GraphDatabaseService graphDb, String path, int bs)
			throws IOException
	{
		if (bs < 2)
		{
			System.out.println("the radius can not less than two");
			return;
		}
		HashMap<Integer, ArrayList<Integer>> hs = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		ArrayList<Integer> rgraph = null;
		for (int i = 1; i <= AllNodeSize; i++)
		{
			System.out.println(i);
			if (!has[i])
			{
				has[i] = true;
				ArrayList<Integer> graph = getGraph(graphDb, i, bs);
				for (Integer nodeid : graph)
				{
					has[nodeid] = true;
					ArrayList<Integer> graph_1 = null;
					ArrayList<Integer> idList = hs.get(nodeid);
					if (idList == null)
					{
						graph_1 = getGraph(graphDb, nodeid, bs - 1);
						hs.put(nodeid, graph_1);
					}
					else
					{
						graph_1 = idList;
					}
					if (graph_1.containsAll(graph))
					{
						isvisit[i] = true;
					}
				}
				if (!isvisit[i])
				{
//					Collections.sort(graph);
					if (!list.contains(graph))
					{
						/*****************************************/
						// for (int j = 0; j < list.size(); j++)
						// {
						// if (list.get(j).contains(graph))
						// {
						//
						// }
						// if(graph.contains(list.get(j)))
						// {
						// list.set(j, graph);
						// }
						// else
						// {
						// list.add(graph);
						// }
						// }
						/*****************************************/
						bw.write(
						// i+"::"+
						graph.toString() + "\r\n");
						list.add(graph);
					}
				}
			}
		}
		System.out.println("半径r图获取完毕");
		bw.close();
	}

	/**
	 * 通过节点id获得深度为bs的图的所有节点的id的集合,nodeId 起始节点的id, bs 遍历的深度
	 */
	private static ArrayList<Integer> getGraph(GraphDatabaseService graphDb, int nodeId, int bs)
	{
		if (bs < 1)
		{
			System.err.println("bs can not less than 1");
			return null;
		}
		ArrayList<Integer> nodeIdList = new ArrayList<Integer>();
		TraversalDescription td = Traversal.description()
				.relationships(RelTypes.KNOWS).breadthFirst()
				.evaluator(Evaluators.toDepth(bs));
		Node node = graphDb.getNodeById(nodeId);
		Traverser traverse = td.traverse(node);
		for (Node nodes : traverse.nodes())
		{
			int id = (int) nodes.getId();
			nodeIdList.add(id);
		}
		return nodeIdList;
	}

	/**
	 * 返回R半径图的HaspMap <Integer,ArrayList<Integer>>: Integer为图编号，也是图起始节点的nodeId
	 * ArrayList<Integer>为图节点的集合, bs 深度
	 */
	public HashMap<Integer, ArrayList<Integer>> getRGraphDemo(
			GraphDatabaseService graphDb, int bs)
	{
		HashMap<Integer, ArrayList<Integer>> graphMap = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 1; i <= AllNodeSize; i++)
		{
			boolean isRGraph = true;
			ArrayList<Integer> graphNodeIdList = getGraph(graphDb, i, bs);
			for (int j = 0; j < graphNodeIdList.size(); j++)
			{
				ArrayList<Integer> graph = getGraph(graphDb,
						graphNodeIdList.get(j), bs - 1);
				if (graph.containsAll(graphNodeIdList))
				{
					isRGraph = false;
				}
			}
			if (isRGraph)
			{
				graphMap.put(i, graphNodeIdList);
			}
			System.out.println("遍历到了第" + i + "个节点");
		}
		return graphMap;
	}

	/**
	 * 一个图所包含的结点id, nodeId 开始节点, nodeId 也作为图编号
	 */
	private static String getGraphToTxt(GraphDatabaseService graphDb, int nodeId, int bs)
			throws IOException
	{
		TraversalDescription td = Traversal.description()
				.relationships(RelTypes.KNOWS).depthFirst()
				.evaluator(Evaluators.toDepth(bs));
		Node node = graphDb.getNodeById(nodeId);
		Traverser traverse = td.traverse(node);
		String s = nodeId + "::";

		for (Node nodes : traverse.nodes())
		{
			int id = (int) nodes.getId();
			s += id + ",";
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}

	/**
	 * 图节点id存入txt文件中， 一行为一个图 ，start:开始节点，end:结束节点，strPath:存储路径
	 */

	public void graphToTxt(GraphDatabaseService graphDb, int start, int end,
			String strPath) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(strPath, true));
		for (int i = start; i <= end; i++)
		{
			bw.write(getGraphToTxt(graphDb, i, 2) + "\r\n");
			System.out.println(i);
		}
		bw.close();
	}

	public static void max_r_Graph(ArrayList<ArrayList<String>> list,
			String toPath) throws IOException
	{
		// 略微耗时
		for (int i = 0; i < list.size(); i++)
		{
			for (int j = i + 1; j < list.size(); j++)
			{
				if (list.get(i) != null && list.get(j) != null
						&& list.get(i).containsAll(list.get(j)))
				{
					list.set(j, null);
				}
				else if (list.get(i) != null && list.get(j) != null
						&& list.get(j).containsAll(list.get(i)))
				{
					list.set(i, null);
					break;
				}
			}
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter(toPath));
		for (int i = 0; i < list.size(); i++)
		{
			for (int j = 0; j < list.get(i).size(); j++)
			{
				bw.write(list.get(i).get(j) + " ");
			}
			bw.write("\r\n");
		}
		System.out.println("最大半径r子图获取完毕，共得到子图："+list.size()+"个");
		bw.close();
	}

	/**
	 * fromPath 半径r图路径，toPath 最大半径r图存储路径
	 */
	public void getMaxRGraphToTxt(String fromPath, String toPath)
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(fromPath));
			ArrayList<ArrayList<String>> al = new ArrayList<ArrayList<String>>();
			ArrayList<String> al1;
			String line = "";
			while ((line = br.readLine()) != null)
			{
				al1 = new ArrayList<String>();
				String[] str = line.split("[|,|]");
				for (int i = 0; i < str.length; i++)
				{
					al1.add(str[i]);
				}
				al.add(al1);
			}
			max_r_Graph(al, toPath);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (Exception e2)
			{
				System.err.println(" BufferedReader close error");
			}

		}
	}
}
