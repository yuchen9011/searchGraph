package com.commom;

import java.util.ArrayList;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

public class GraphTools {
	
	/**
	 * 创建第i个图数据库
	 */
	public GraphDatabaseService createGraphDatabase(int i)
	{
		String DB_PATH = "graph/graph_new.db";
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH + i);

		return graphDb;
	}
	
	/**
	 * 创建两个节点之间的关系
	 */
	public void createRelationForTwoNode(Node startNode, Node endNode)
	{
		startNode.createRelationshipTo(endNode, RelTypes.KNOWS);
	}
	
	/**
	 * 创建节点
	 */
	public Node createNewNode(GraphDatabaseService graphDb, Node node)
	{
		Node newNode = graphDb.createNode();
		Iterator<String> iterKey = node.getPropertyKeys().iterator();
		while (iterKey.hasNext())
		{
			String pro = iterKey.next();
			newNode.setProperty(pro, node.getProperty(pro));
		}
		return newNode;
	}
	
	/**
	 * 获得当前节点的相邻节点的数组(广度优先遍历)
	 */
	private static ArrayList<Node> getRelNode(Node node)
	{
		ArrayList<Node> nodeList = new ArrayList<Node>();
		TraversalDescription td = Traversal.description()
				.relationships(RelTypes.KNOWS).breadthFirst()
				.evaluator(Evaluators.excludeStartPosition())
				.evaluator(Evaluators.toDepth(1));
		Traverser traverse = td.traverse(node);
		for (Node nodes : traverse.nodes()){
				nodeList.add(nodes);
		}
		return nodeList;
	}
	
	/**
	 * 获得数组里的节点的所有邻接节点
	 * 
	 * @param nodeList 存有节点的数组
	 * @return nodeRelList 二位数组 存放数组节点的邻接节点
	 */
	public ArrayList<ArrayList<Node>> getAllRelNode(ArrayList<Node> nodeList)
	{

		ArrayList<ArrayList<Node>> nodeRelList = new ArrayList<ArrayList<Node>>();
		for (int i = 0; i < nodeList.size(); i++)
		{
			ArrayList<Node> newList = getRelNode(nodeList.get(i));
			nodeRelList.add(newList);
		}
		return nodeRelList;
	}
	
	/**
	 * 判断节点是否为用户节点，是返回true，否返回false
	 */
	public static boolean isUserNode(Node node)
	{
		boolean flag = false;
		String nodeType = (String) node.getProperty("nodeType");
		if (nodeType == "user")
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
		String nodeType = (String) node.getProperty("nodeType");
		if (nodeType == "weibo")
		{
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 获得图数据库的所有节点信息的文本信息
	 */
	public String getGraphString(GraphDatabaseService graphDb)
	{

		Iterator<Node> iter = graphDb.getAllNodes().iterator();
		String str = "";
		while (iter.hasNext())
		{
			Node node = iter.next();
			if (isUserNode(node))
			{
				UserImpl user = new UserImpl(node);
				str += user.toString();
			}
			else
			{
				WeiboImpl weibo = new WeiboImpl(node);
				str += weibo.toString();
			}
		}
		return str;
	}
}


