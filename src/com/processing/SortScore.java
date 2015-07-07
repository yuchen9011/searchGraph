package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class SortScore {

	public static void main(String[] args) {
		
		String scorePath = "sub_graph_test/scoreIR_nlpir11.txt";
		String toPath = "sub_graph_test/scoreIR_sort.txt";
		sortByScore(scorePath, toPath);
	}

	@SuppressWarnings("resource")
	public static void sortByScore(String scorePath, String toPath) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(scorePath));
			bw = new BufferedWriter(new FileWriter(toPath));
			String line = "";
			int n = 0;
			while ((line = br.readLine()) != null) {
				System.out.println(n);
				n++;
				String[] arr = line.split(" |;");
				if (arr.length > 2) {
					String keyword = arr[0];
					String[][] arrDouble = new String[arr.length - 1][3];
					for (int i = 1; i < arr.length; i++) {
						String[] arr1 = arr[i].split(",");
						arrDouble[i - 1][0] = arr1[0];
						arrDouble[i - 1][1] = arr1[1];
						arrDouble[i - 1][2] = arr1[2];
					}

					sort(arrDouble, 2);
					StringBuffer sb = new StringBuffer();
					sb.append(keyword + " ");
					for (int i = 0; i < arrDouble.length; i++) {
						for (int j = 0; j < arrDouble[0].length - 1; j++) {
							sb.append(arrDouble[i][j] + ",");
						}
						sb.append(arrDouble[i][arrDouble[0].length - 1]);
						sb.append(";");
					}
					bw.write(sb + "\r\n");
				} else {
					bw.write(line + "\r\n");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				bw.close();
			} catch (Exception e2) {
				System.out.println("资源关闭失败");
			}
		}
	}

	/**
	 * 排序，先根据order[0]列比较，若相同则再比较order[1]列,依次类推,使用比较器实现
	 */
	private static void sort(String[][] ob, final int order) {
		Arrays.sort(ob, new Comparator<Object>() {
			public int compare(Object o1, Object o2) 
			{
				String[] one = (String[]) o1;
				String[] two = (String[]) o2;
				int k = order;
				if (Double.parseDouble(one[k]) < Double.parseDouble(two[k]))
				{
					return 1;
				}
				else if (Double.parseDouble(one[k]) > Double.parseDouble(two[k])) 
				{
					return -1;
				}
				return 0;
			}
		});
	}
}
