package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.commom.ModelGraph;
import com.commom.SplitGraph;
import com.index.GraphIndex;
import com.index.Search_New;
import com.index.SimLSH;
import com.index.ToolsClass;
import com.processing.GraphString;
import com.processing.MRGraph;
import com.processing.ProcessContent1;
import com.processing.SortScore;
import com.processing.WordSplit;

public class KeywordSearchTest {

	public static void main(String[] args) throws IOException {

		String keyWords = "小米 华为 魅族 苹果 酷派 大神";
		int containsNums = 4;
		String DB_PATH = "sub_graph_test/graph.db";
		String numsOfNodesPath = "sub_graph_test/numsOfNodes.txt";
		String rGraphPath = "sub_graph_test/r_graph_test.txt";
		String mrgPath = "sub_graph_test/max_r_graph_test.txt";
		String mrg_strPath = "sub_graph_test/mrg_str_extends_test.txt";
		
		
//		String keywordsPath = "sub_graph_test/keys_test.txt";
//		String termNumsInGraphPath = "sub_graph_test/termNumsInGraph_test.txt";
//		String scoreIRPath = "sub_graph_test/scoreIR_sort.txt";
//		String hashTablePath = "sub_graph_test";
		
		String keywordsPath = "sub_graph_test/keys_nlpir.txt";
		String termNumsInGraphPath = "sub_graph_test/termNumsInGraph_nlpir.txt";
		String scoreIRPath = "sub_graph_test/scoreIR_nlpir.txt";
		String scoreIRSortPath = "sub_graph_test/scoreIR_sort.txt";
		String hashTablePath = "sub_graph_nlpir";
		
		int distance = 7;
		int hashBits = 64;

//		 //导入数据到图数据库
//		 File f = new File(DB_PATH);
//		 ToolsClass.deleteDir(f);
//		 GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//		 ModelGraph mg = new ModelGraph();
//		 mg.createDB(graphDb, numsOfNodesPath);
//		 //分割图，得到半径r图
//		 SplitGraph sg = new SplitGraph();
//		 //获得半径r图，存入rGraphPath
//		 // GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//		 sg.getRGraph(graphDb,rGraphPath, 2);
//		 // graphDb.shutdown();
//		
//		 MRGraph mrg = new MRGraph();
//		 mrg.saveRgraph(rGraphPath, mrgPath);
//		 //获取子图文本信息
//		 // GraphDatabaseService graphDb1 = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
//		 GraphString gs = new GraphString();
//		 gs.getGraphStr(graphDb, mrgPath, mrg_strPath);
//		 graphDb.shutdown();
//		 //提取特征词
//		 ProcessContent1 pc = new ProcessContent1();
//		 HashMap<String, String> hm = pc.getFeaWords(mrg_strPath, keywordsPath, termNumsInGraphPath);
//		
//		 // 得出倒排索引表
//		 final double s = 0.2;
//		 GraphIndex gi = new GraphIndex();
//		 TreeMap<String, String> treeMap = gi.tfidf(keywordsPath, termNumsInGraphPath, mrgPath, s);
//		 gi.mapWrite(treeMap, scoreIRPath);
//		 SortScore.sortByScore(scoreIRPath, scoreIRSortPath);
//		 //根据倒排索引表，运用 simhash 构建哈希基础上的倒排索引
//		 SimLSH simLSH = new SimLSH();
//		 simLSH.hashInvertedList(scoreIRSortPath, hashTablePath, hashBits, distance);

		// 用户查询，返回包含关键词的子图信息
		Search_New sn = new Search_New();
		long satrt = System.currentTimeMillis();
		ArrayList<String> al = sn.getRecord(keyWords, hashTablePath, distance, containsNums);
		System.out.println(System.currentTimeMillis() - satrt);

		int k = 20;   //需要返回的子图个数
		
		if (k >= al.size()) 
		{
			for (int i = 0; i < al.size(); i++)
			{
				System.out.println(i +"\t" + al.get(i));
			}
		}
		else 
		{
			for (int i = 0; i < k; i++) 
			{
				System.out.println(i +"\t" + al.get(i));
			}
		}
	}
}
