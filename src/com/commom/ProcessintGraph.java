package com.commom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class ProcessintGraph {

	String path = "sub_graph/MaxRGraphNum.txt";
	
	
	public static void main(String[] args)
	{
		
	}
	
	/**
	 * 将现有的节点复制到新的数据库中（创建新节点）
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
	 * 创建两个节点之间的关系
	 */
	public void createRelationForTwoNode(Node startNode, Node endNode)
	{
		startNode.createRelationshipTo(endNode, RelTypes.KNOWS);
	}
	
	/**
	 * 半径r图找出最大半径r图
	 * @param list 半径r图的结点集合
	 * @param toPath  存储路径
	 */

	public void max_r_Graph(ArrayList<ArrayList<String>> list, String toPath)
			throws IOException
	{
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
		bw.close();
	}
}
