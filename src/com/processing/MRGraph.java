package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 此类求出最大半径r图
 */
public class MRGraph {

	public static void main(String[] args) throws IOException
	{
		String sourcePath = "sub_graph/r_graph.txt";
		String savePath = "sub_graph/max_r_graph.txt";
		MRGraph mrg = new MRGraph();
		mrg.saveRgraph(sourcePath, savePath);
	}

	public void saveRgraph(String sourcePath, String savePath)
	{
		BufferedReader br = null;
		ArrayList<ArrayList<Integer>> al = null;
		try
		{
			br = new BufferedReader(new FileReader(sourcePath));
			al = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> al1;
			String line = "";
			try
			{
				while ((line = br.readLine()) != null)
				{
					al1 = new ArrayList<Integer>();
					String[] str = line.split("\\[|, |\\]");
					for (int i = 1; i < str.length; i++)
					{
						al1.add(Integer.parseInt(str[i]));
					}
					al.add(al1);
				}
			} catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e)
		{
			System.out.println("can't find " + sourcePath);
			e.printStackTrace();
		}
		try
		{
			br.close();
			max_r_Graph(al, savePath);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void max_r_Graph(ArrayList<ArrayList<Integer>> list,
			String savePath) throws IOException
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

		BufferedWriter bw = new BufferedWriter(new FileWriter(savePath));
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) != null)
			{
				ArrayList<Integer> list1 = list.get(i);
				int id = list1.get(0);
				int n = list1.size();
				Collections.sort(list1);
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < n - 1; j++)
				{
					sb.append(list1.get(j)).append(",");
				}
				sb.append(list1.get(n - 1));
				bw.write(id + "::" + n + "::" + sb + "\r\n");
			}
		}
		System.out.println("最大半径r图获取完毕，共获得子图：" + list.size() + "个子图");
		bw.close();
	}
}
