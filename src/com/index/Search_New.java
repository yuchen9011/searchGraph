package com.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.commom.ModelGraph;
import com.commom.UserImpl;
import com.commom.WeiboImpl;
import com.processing.ProcessContent;

public class Search_New {

	private static final int hashDistance = 3;   // 汉明距离
	private static final int NumOfNode = 147808; // 图总结点数
	private static final String FileName_NUM_MAX_R_GRAPH = "sub_graph/mrg_num.txt";
	private static final String indexFileDir = "sub_graph_bak"; // hash索引所在的文件夹

	public static void main(String[] args) throws IOException {
		Search_New sn = new Search_New();

		long satrt = System.currentTimeMillis();

		ArrayList<String> al = sn.getRecord("小米 华为 魅族 苹果 酷派 大神", indexFileDir, hashDistance, 4);
		System.out.println(System.currentTimeMillis() - satrt);

		for (int i = 0; i < al.size(); i++) 
		{
			System.out.println(al.get(i));
		}
	}

	
	/**
	 * 获得包含关键字的微博及用户记录
	 * 
	 * @param str 用户输入的多关键词，以空格间隔
	 * @param n   所查询到的图里所包含用户输入的关键字个数
	 * @return
	 */
	public ArrayList<String> getContainString(String str, String indexFileDir, String DB_PATH, int n) {
		ArrayList<String> aList = new ArrayList<String>();
		boolean[] boolArr = new boolean[NumOfNode];
		String[] sss = str.split("\\s+");
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase(DB_PATH); // 开启数据库需要568ms
		Search_New sn = new Search_New();
		ArrayList<String> list = sn.getRecord(str, indexFileDir,hashDistance, n);
		ArrayList<String> al = sn.getGraphIdMessages(list,
				FileName_NUM_MAX_R_GRAPH);
		// 循环所用 1500ms
		for (int i = 0; i < al.size(); i++) 
		{
			String[] arr = al.get(i).split(" ");
			for (int j = 0; j < arr.length; j++) 
			{
				int id = Integer.parseInt(arr[j]);
				boolean isHave = false;
				if (boolArr[id] == false) 
				{
					boolArr[id] = true;
					Node node = graphDb.getNodeById(id);
					if (ProcessContent.isUserNode(node)) 
					{
						UserImpl user = new UserImpl(node);
						String userString = user.getContent();
						for (int k = 0; k < sss.length; k++) 
						{
							if (userString.contains(sss[k])) 
							{
								isHave = true;
								// break;
							}
						}
						if (isHave) 
						{
							aList.add(userString);
						}
					} 
					else 
					{
						WeiboImpl weibo = new WeiboImpl(node);
						String weiboString = weibo.getContent();
						for (int k = 0; k < sss.length; k++) 
						{
							if (weiboString.contains(sss[k])) 
							{
								isHave = true;
							}
						}
						if (isHave)
						{
							aList.add(weiboString);
						}
					}
				}
			}
		}
		// 关闭数据库 70ms
		graphDb.shutdown();
		return aList;
	}

	// 132::14::96,99,101,123,131,132,133,14784,27151,44346,63778,63785,63788,63789
	/**
	 * 通过包含关键字的图记录，根据偏移量读取图的结点信息
	 * 
	 * @param al  图记录 ，形式为： 图编号 图偏移量 包含的关键词数 得分
	 * @param graphPath  最大半径r图的文件 （484ms）
	 * @return  图结点的id号
	 */
	public ArrayList<String> getGraphIdMessages(ArrayList<String> al, String graphPath) {
		ArrayList<String> result = new ArrayList<String>();
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(graphPath, "r"); // 根据偏移量读取图的记录信息
			for (int i = 0; i < al.size(); i++) 
			{
				String alStr = al.get(i);
				String[] arrStrAl = alStr.split(" ");
				raf.seek(Integer.parseInt(arrStrAl[1])); // 定位指针位置
				String graphId = raf.readLine(); // 子图的结点集合
				String[] graphIdArr = graphId.split("::|,");
				StringBuffer sb = new StringBuffer();
				for (int j = 2; j < graphIdArr.length; j++) 
				{
					sb.append(graphIdArr[j] + " ");
				}
				result.add(sb + "");
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				raf.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 用来得到包含 n 个及以上关键词的图记录的数组  // 图编号 位置 包含个数 分数
	 * 
	 * @param keysStr 用户输入的关键词 (2ms)
	 * 
	 */
	public ArrayList<String> getRecord(String keysStr, String indexFileDir, int hashDistance, int n) {
		Search_New search = new Search_New();
		ArrayList<String> al = new ArrayList<String>();
		double[][] array;
		try {
			String[] keyWords = keysStr.split("\\s+"); // 用户输入的关键词，以空格分隔
			if (n <= keyWords.length && n > 0) {
				array = search.searchManyKeys(keyWords, indexFileDir, hashDistance, n);
				String str;
				for (int i = 0; i < array.length; i++) 
				{
					if (array[i][2] >= n) 
					{
						str = (int) array[i][0]+"\t"+(int)array[i][1]+"\t"+(int) array[i][2]+"\t"+ array[i][3];
						al.add(str);
					}
				}
			}
			else 
			{
				System.err.println("n不能大于查询词的个数");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return al;
	}

	

	/**
	 * 得到包含关键字的记录的二维数组 // 图编号 位置 包含个数 分数 （待修改）
	 * 
	 * @param keysStr
	 *            用户输入的关键词
	 * @param indexStrPath
	 *            倒排索引表文件所在的文件夹 （300ms）
	 * @throws IOException
	 */
	public double[][] searchManyKeys(String[] keyWords, String indexFileDir, int hashDistance, int n) throws IOException {
		BufferedReader br = null;
		ArrayList<String> indexList; // 存储包含此关键字的索引记录信息
		HashMap<Integer, HashMap<String, String>> hhMap = new HashMap<>();
		HashMap<String, String> resultMap; // 存储图记录
		double[][] array = null;
		SimHash simHash;
//		String[] keyWords = keysStr.split("\\s+"); // 用户输入的关键词，以空格分隔
			for (int i = 0; i < keyWords.length; i++) 
			{
				resultMap = new HashMap<String, String>(); // 存储图记录
				indexList = new ArrayList<String>();
				// 算出查询关键字的hash值
				simHash = new SimHash(keyWords[i], true);
				String queryHash = simHash.getStrSimHash();
				ArrayList<BigInteger> al = simHash.subByDistance1(simHash, hashDistance); // 得到一个关键字哈希值，并分成八个哈希值
				for (int j = 0; j < al.size(); j++) 
				{
					br = new BufferedReader(new FileReader(indexFileDir+"/index"+(hashDistance+1)+"/index"+j+"/"+ al.get(j)+".txt"));
					String line = "";
					while ((line = br.readLine()) != null) 
					{
						String[] arr = line.split(" "); // arr[0] 为索引词，arr[1] 为 arr[0]所对应的图
						if (!indexList.contains(arr[1]))
						{
							if (simHash.getDistance(arr[0], queryHash) < 4)
							{
								indexList.add(arr[1]); // 如下形式：[2,138243,0.56;1965,2039808,0.97;],[1,121231,0.13;]
								String[] strArr = arr[2].split(";"); 
								for (int k = 0; k < strArr.length; k++) 
								{
									String[] arr1 = strArr[k].split(",");
									String graphIdAndOffset = arr1[0] + "," + arr1[1];
									String score = resultMap.get(graphIdAndOffset);
									if (score != null) 
									{
										double score1 = Double.parseDouble(score)+ Double.parseDouble(arr1[2]);
										resultMap.put(graphIdAndOffset, score1 + "");
									} 
									else
									{
										resultMap.put(graphIdAndOffset, arr1[2]);
									}
								}
							}
						}
					}
					br.close();
				}
				hhMap.put(i, resultMap);
			}
			HashMap<String, String> hMap = new HashMap<>();
			for (int i = 0; i < hhMap.size(); i++) {
				HashMap<String, String> tempMap = hhMap.get(i);
				// 遍历Map
				Iterator iter = tempMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = entry.getKey() + "";
					String val = entry.getValue() + "";
					String s = hMap.get(key);
					if (s != null) {
						String[] countAndScore = s.split(",");
						int count = Integer.parseInt(countAndScore[0]) + 1;
						double score = Double.parseDouble(val)
								+ Double.parseDouble(countAndScore[1]);
						hMap.put(key, count + "," + score);
					} else {
						hMap.put(key, 1 + "," + val);
					}
				}
			}
			array = new double[hMap.size()][4]; // 存储记录
			// 遍历Map
			Iterator iter = hMap.entrySet().iterator();
			int t = 0;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				String[] arr = (key + "").split(",");
				String[] arr1 = (val + "").split(",");
				array[t][0] = Double.parseDouble(arr[0]);
				array[t][1] = Double.parseDouble(arr[1]);
				array[t][2] = Double.parseDouble(arr1[0]);
				array[t][3] = Double.parseDouble(ToolsClass.getTwoDecimal(Double.parseDouble(arr1[1])/array[t][2]));
				t++;
			}
			ToolsClass.sort(array, new int[] { 2, 3 });
		return array;
	}
}
