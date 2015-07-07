package com.commom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import jdk.internal.jfr.events.FileWriteEvent;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.impl.util.FileUtils;

/**
 * 批量导入数据
 */
public class ModelGraph {

	static final String WEIBO_INFO     = "sourceData/weibo.txt";
	static final String USER_INFO      = "sourceData/user.txt";
	static final String USER_RELATION  = "sourceData/userrelation.txt";
	static final String WEIBO_RELATION = "sourceData/weiborelation.txt";

	int nodeNums = 0;
	Index<Node> index = null;

	BufferedReader br = null;
	InputStreamReader isr = null;

	public static void main(String[] args)
	{
		String DB_PATH = "graph/graph_new.db";
		String numsOfNodesPath ="sub_graph_test/NumsOfNodes.txt";
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		ModelGraph mg = new ModelGraph();
		mg.createDB(graphDb, numsOfNodesPath);
	}

	public void createDB(GraphDatabaseService graphDb,String numsOfNodesPath)
	{
		ModelGraph mg = new ModelGraph();
		mg.createUserNode(graphDb);
		mg.createWeiboNode(graphDb,numsOfNodesPath);
		mg.createUserRelation(graphDb);
		mg.createWeiboRelation(graphDb);
	}

	public void createUserNode(GraphDatabaseService graphDb)
	{

		// graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		registerShutdownHook(graphDb);

		// 启动事务
		Transaction tx = graphDb.beginTx();
		Node node = null;

		index = graphDb.index().forNodes("nodes");

		try
		{
			isr = new InputStreamReader(new FileInputStream(USER_INFO), "UTF-8");
			br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null)
			{
				nodeNums++;
				String userType = "user";
				String[] userInfo = line.split(", ");
				String userId = userInfo[0];
				String userName = userInfo[1];
				String city = userInfo[5];
				String url = userInfo[6];
				String gender = userInfo[7];
				int followernum = Integer.parseInt(userInfo[8]);
				int friendsnum = Integer.parseInt(userInfo[9]);
				int statusesnum = Integer.parseInt(userInfo[10]);
//				int favouritesnum = Integer.parseInt(userInfo[11]);
				String created_at = userInfo[12];
				node = graphDb.createNode();
				// 添加节点属性
				node.setProperty("id", userId);
//				node.setProperty("nodeTyoe", userType);   //结点类型
				node.setProperty("name", userName);
				node.setProperty("city", city);
				node.setProperty("url", url);
				node.setProperty("gender", gender);
				node.setProperty("friendsnum", friendsnum);
				node.setProperty("followernum", followernum);
				node.setProperty("statusesnum", statusesnum);
				node.setProperty("created_at", created_at);
				// 给用户节点添加索引
				index.add(node, "id", node.getProperty("id"));
			}
			System.out.println("用户数据导入完成");

			tx.success();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tx.finish();
		}
	}

	public void createWeiboNode(GraphDatabaseService graphDb, String numsOfNodesPath)
	{
		// graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

		registerShutdownHook(graphDb);

		// 启动事务
		Transaction tx = graphDb.beginTx();
		Node node = null;

		Index<Node> index = graphDb.index().forNodes("nodes");

		try
		{
			isr = new InputStreamReader(new FileInputStream(WEIBO_INFO),"UTF-8");
			br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null)
			{
				nodeNums++;
				String weiboType = "weibo";
				String[] mbgStrings = line.split(", ");
				int length = mbgStrings.length;
				String mId = mbgStrings[0];
				String date = mbgStrings[1];
				String text = mbgStrings[2];
				String source = null;
				for (int j = 3; j < length - 5; j++)
				{
					source = source + mbgStrings[j];
				}
				int repostsnum = Integer.parseInt(mbgStrings[length - 5]);
				int commentsnum = Integer.parseInt(mbgStrings[length - 4]);
				int attitudesnum = Integer.parseInt(mbgStrings[length - 3]);
				String uid = mbgStrings[length - 2];
				String topic = mbgStrings[length - 1];

				// Weibo mNode = new Weibo(mId, date, text, source, uid,
				// repostsnum, commentsnum, attitudesnum, topic);
				// 创建节点
				node = graphDb.createNode();
				node.setProperty("id", mId);
//				node.setProperty("nodeTyoe", weiboType);   //结点类型
				node.setProperty("date", date);
				node.setProperty("text", text);
				node.setProperty("source", source);
				node.setProperty("uid", uid);
				node.setProperty("repostsnum", repostsnum);
				node.setProperty("commentsnum", commentsnum);
				node.setProperty("attitudesnum", attitudesnum);
				node.setProperty("topic", topic);

				// 构建索引
				index.add(node, "id", node.getProperty("id"));

				Node userNode = index.get("id", uid).getSingle();
				// 创建微博和用户之间的关系
				if (userNode != null)
				{
					userNode.createRelationshipTo(node, RelTypes.KNOWS);
				}
//				
			}
//			System.out.println(nodeNums);
			BufferedWriter bw = new BufferedWriter(new FileWriter(numsOfNodesPath));
			bw.write(nodeNums+"");
			bw.close();
			System.out.println("微博数据导入完成");

			tx.success();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tx.finish();
		}
	}

	public void createUserRelation(GraphDatabaseService graphDb)
	{

		// graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

		registerShutdownHook(graphDb);
		for (int i = 1; i < 14; i++)
		{
			// 启动事务
			Transaction tx = graphDb.beginTx();

			index = graphDb.index().forNodes("nodes");

			String line = null;

			try
			{
				isr = new InputStreamReader(new FileInputStream("sourceData/userrelation/userrelation" + "_" + i
						+ ".txt"), "UTF-8");
				br = new BufferedReader(isr);
				System.out.println(i);
				while ((line = br.readLine()) != null)
				{
					String[] userrelation = line.split(", ");
					String suid = userrelation[0];
					String tuid = userrelation[1];

					Node userNode1 = index.get("id", suid).getSingle();
					Node userNode2 = index.get("id", tuid).getSingle();
					if (userNode1 != null && userNode2 != null)
					{
						userNode1.createRelationshipTo(userNode2, RelTypes.KNOWS);
					}
				}
				tx.success();
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					br.close();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tx.finish();
			}
		}
		System.out.println("用户关系创建完成");
	}

	public void createWeiboRelation(GraphDatabaseService graphDb)
	{
		String line = null;

		registerShutdownHook(graphDb);

		// 启动事务
		Transaction tx = graphDb.beginTx();

		index = graphDb.index().forNodes("nodes");
		try
		{
			isr = new InputStreamReader(new FileInputStream(WEIBO_RELATION),
					"UTF-8");
			br = new BufferedReader(isr);
			int i = 0;
			while ((line = br.readLine()) != null)
			{
				String[] weiborelation = line.split(", ");
				String smid = weiborelation[0];
				String tmid = weiborelation[1];
				Node weiboNode1 = index.get("id", smid).getSingle();
				Node weiboNode2 = index.get("id", tmid).getSingle();
				// 创建微博之间的关系
				if (weiboNode1 != null && weiboNode2 != null)
				{
					weiboNode1.createRelationshipTo(weiboNode2, RelTypes.KNOWS);
				}
				i++;
			}
			System.out.println("微博关系创建完成");
			System.out.println("图创建成功");

			tx.success();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tx.finish();
		}
	}

	public static void registerShutdownHook(final GraphDatabaseService graphDB)
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run()
			{
				graphDB.shutdown();
			}
		});
	}
}
