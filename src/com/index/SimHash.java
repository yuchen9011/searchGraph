package com.index;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SimHash {

	private int hashbits = 64;
	
	private String tokens;
	private BigInteger intSimHash;
	private String strSimHash;
	
	public String getStrSimHash() 
	{
		return strSimHash;
	}

	public void setStrSimHash(String strSimHash) 
	{
		this.strSimHash = strSimHash;
	}

	public SimHash(String tokens) 
	{
		this.tokens = tokens;
		this.intSimHash = this.simHash();
	}

	public SimHash(String tokens, int hashbits) 
	{
		this.tokens = tokens;
		this.hashbits = hashbits;
		this.intSimHash = this.simHash();
	}
	
	public SimHash(String tokens, boolean flag)
	{
		this.tokens = tokens;
		this.intSimHash = this.simHashWithoutSplitWords();
	}

	HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

	/**
	 * 返回文本的 simHash 值
	 */
	public BigInteger simHash() {
		// 定义特征向量/数组
		int[] v = new int[this.hashbits];
		Analyzer analyzer = new IKAnalyzer(true);
		StringReader reader = new StringReader(this.tokens);
		// 分词
		TokenStream ts = analyzer.tokenStream("content", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		String word = null;
		try {
			while (ts.incrementToken()) {
				word = term.toString();
				if (word.length() != -1) {
					// 去掉停用词
					// System.out.println(word);
					// 将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
					BigInteger t = this.hash(word);
					for (int i = 0; i < this.hashbits; i++) 
					{
						BigInteger bitmask = new BigInteger("1").shiftLeft(i);
						// 建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
						if (t.and(bitmask).signum() != 0) 
						{
						// 计算整个文档的所有特征的向量和（未引入权值）
							v[i] += 1;
						}
						else
						{
							v[i] -= 1;
						}
					}
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		BigInteger fingerprint = new BigInteger("0");
		StringBuffer simHashBuffer = new StringBuffer();
		for (int i = 0; i < this.hashbits; i++) 
		{
			// 对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
			if (v[i] >= 0) 
			{
				fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
				simHashBuffer.append("1");
			}
			else
			{
				simHashBuffer.append("0");
			}
		}
		this.strSimHash = simHashBuffer.toString();
		return fingerprint;
	}
	
	
	/**
	 * 返回文本的 simHash 值
	 */
	public BigInteger simHashWithoutSplitWords() {
		// 定义特征向量/数组
		int[] v = new int[this.hashbits];
		// 将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
		BigInteger t = this.hash(this.tokens);
		for (int i = 0; i < this.hashbits; i++) 
		{
			BigInteger bitmask = new BigInteger("1").shiftLeft(i);
			// 建立一个长度为64的整数数组(64位的数字指纹),
			// 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
			if (t.and(bitmask).signum() != 0) 
			{
				// 计算整个文档的所有特征的向量和（未引入权值）
				v[i] += 1;
			}
			else
			{
				v[i] -= 1;
			}
		}

		BigInteger fingerprint = new BigInteger("0");
		StringBuffer simHashBuffer = new StringBuffer();
		for (int i = 0; i < this.hashbits; i++) 
		{
			// 对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
			if (v[i] >= 0) 
			{
				fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
				simHashBuffer.append("1");
			}
			else 
			{
				simHashBuffer.append("0");
			}
		}
		this.strSimHash = simHashBuffer.toString();
		return fingerprint;
	}

	private BigInteger hash(String source) {
		if (source == null || source.length() == 0) 
		{
			return new BigInteger("0");
		}
		else 
		{
			char[] sourceArray = source.toCharArray();
			BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
			BigInteger m = new BigInteger("1000003");
			BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
			for (char item : sourceArray) 
			{
				BigInteger temp = BigInteger.valueOf((long) item);
				x = x.multiply(m).xor(temp).and(mask);
			}
			x = x.xor(new BigInteger(String.valueOf(source.length())));
			if (x.equals(new BigInteger("-1"))) 
			{
				x = new BigInteger("-2");
			}
			return x;
		}
	}

	// 汉明距离
	public int hammingDistance(SimHash other) {

		BigInteger x = this.intSimHash.xor(other.intSimHash);
		int tot = 0;

		// 统计x中二进制位数为1的个数
		while (x.signum() != 0)
		{
			tot += 1;
			x = x.and(x.subtract(new BigInteger("1")));
		}
		return tot;
	}

	// 两个字符串之间的距离 100001，100010 距离为2
	public int getDistance(String str1, String str2) {
		int distance;
		if (str1.length() != str2.length()) {
			distance = -1;
		} else {
			distance = 0;
			for (int i = 0; i < str1.length(); i++) {
				if (str1.charAt(i) != str2.charAt(i)) {
					distance++;
				}
			}
		}
		return distance;
	}

	/**
	 * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用
	 * 
	 * @param simHash
	 * @param distance
	 * @return
	 */

	public List subByDistance(SimHash simHash, int distance) {
		// 分成几组来检查
		int numEach = this.hashbits / (distance + 1);
		List characters = new ArrayList();
		StringBuffer buffer = new StringBuffer();
		int k = 0;
		for (int i = 0; i < this.intSimHash.bitLength(); i++) {
			// 当且仅当设置了指定的位时，返回 true
			boolean sr = this.intSimHash.testBit(i);
			if (sr) {
				buffer.append("1");
			} else {
				buffer.append("0");
			}
			if ((i + 1) % numEach == 0) {
				// 将二进制转为BigInteger
				BigInteger eachValue = new BigInteger(buffer.toString(), 2);
				// System.out.println("----" + eachValue);
				buffer.delete(0, buffer.length());
				characters.add(eachValue);
			}
		}

		return characters;
	}

	/**
	 * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用(短文本一般distance 取 7)
	 * 
	 * @param simHash
	 * @param distance
	 * @return
	 */

	public ArrayList<BigInteger> subByDistance1(SimHash simHash, int distance) {
		// 分成几组来检查
		int numEach = this.hashbits / (distance + 1);
		ArrayList<BigInteger> characters = new ArrayList<BigInteger>();

		StringBuffer buffer = new StringBuffer();
		String s = simHash.strSimHash;
		for (int i = 0; i < s.length(); i++) {
			if ((i + 1) % numEach == 0) {
				buffer.append(s.substring(i - numEach + 1, i + 1));
				// 将二进制转为BigInteger
				BigInteger eachValue = new BigInteger(buffer.toString(), 2);
				// System.out.println("----" + eachValue);
				buffer.delete(0, buffer.length());
				characters.add(eachValue);
			}
		}
		return characters;
	}

	
	/**
	 * 如果海明距离取3，则分成四块，并得到每一块的bigInteger值 ，作为索引值使用(短文本一般distance 取 7)
	 * 
	 * @param simHash
	 * @param distance
	 * @return
	 */

	public ArrayList<BigInteger> subByDistance2(SimHash simHash, int distance) {
		// 分成几组来检查
		int numEach = this.hashbits / (distance + 1);
		ArrayList<BigInteger> characters = new ArrayList<BigInteger>();

		StringBuffer buffer = new StringBuffer();
		String s = simHash.strSimHash;
		for (int i = 0; i < simHash.strSimHash.length(); i++) {
			if ((i + 1) % numEach == 0) {
				buffer.append(s.substring(i - numEach + 1, i + 1));
				// 将二进制转为BigInteger
				BigInteger eachValue = new BigInteger(buffer.toString(), 2);
				// System.out.println("----" + eachValue);
				buffer.delete(0, buffer.length());
				characters.add(eachValue);
			}
		}
		return characters;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		String s1 = "个人收入";
		String s2 = "今日";
		SimHash hash1 = new SimHash(s1);
		SimHash hash2 = new SimHash(s2);
		
		
		System.out.println(hash1.getStrSimHash());
		System.out.println(hash2.getStrSimHash());
		System.out.println(hash1.hammingDistance(hash2));
		System.out.println("======");
		
		System.out.println(hash1.getDistance("1001", "0101"));
		
		System.out.println("hash1.hash:::" + hash1.strSimHash);
		System.out.println("hash1.bit:::" + hash1.hashbits);
		System.out.println("hash1.intSimhash:::" + hash1.intSimHash);

		
		// 根据鸽巢原理（也称抽屉原理，如果两个签名的海明距离在 3 以内，它们必有一块签名subByDistance()完全相同。
		int dis2 = hash1.getDistance(hash2.strSimHash, hash1.strSimHash);
		System.out.println(hash2.hammingDistance(hash1) + " " + dis2);
	}
}