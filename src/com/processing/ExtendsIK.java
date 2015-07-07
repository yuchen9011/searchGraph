package com.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ExtendsIK {

	public static void main(String[] args) {
		getExtDic("data/user.txt", "src/ext_user.dic");
	}

	/**
	 * 获取扩展词文件 ext_user.dic
	 * 
	 * @param fromPath
	 * @param toPath
	 */
	public static void getExtDic(String fromPath, String toPath) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(fromPath));
			bw = new BufferedWriter(new FileWriter(toPath));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] s = line.split(", ");
				bw.write(s[1] + "\r\n");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {

				bw.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
