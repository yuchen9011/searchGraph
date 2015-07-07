package Cluster;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Vector {

	private ArrayList<String> keywords;
	private String graphId;
	private String offSet;

	public static void main(String[] args) {
		Vector v = new Vector("1::1::砸啊西安潇洒下阿萨");
		ArrayList<String> al = v.keywords;
		System.out.println(v.graphId);
		System.out.println(v.offSet);
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));
		}
 	}
	
	public Vector(String graphString) 
	{
		String[] arr = graphString.split("::| ");
		this.graphId = arr[0];
		this.offSet = arr[1];
		this.keywords = getWords(arr[2]);
	}
	
	public ArrayList<String> getWords(String graph)
	{
		Analyzer analyzer = new IKAnalyzer(true);
		StringReader reader = new StringReader(graph);
		ArrayList<String> al = new ArrayList<String>();
		TokenStream ts = analyzer.tokenStream("content", reader);
		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		String word = null;
		try {
			while (ts.incrementToken()) 
			{
				word = term.toString();
				if (word.length() != -1) 
				{
					al.add(word);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		analyzer.close();
		return al;
	}
}
