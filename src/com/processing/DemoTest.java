package com.processing;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoTest {

	public static void main(String[] args) throws Exception {
		// String s = "张个人收入novadnginaction又您老您不见不散";
		// Analyzer analyzer = new IKAnalyzer(true);
		// // 创建分词对象
		// StringReader reader = new StringReader(s);
		// // 分词
		// TokenStream ts = analyzer.tokenStream("content", reader);
		// CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
		// while (ts.incrementToken())
		// {
		// System.out.println(term.toString());
		// }
		DemoTest dt = new DemoTest();
		String s = "公务员.家庭勿扰,不是一个圈的。 http://t.cn/z8moPeM 公务员&&好多人专转本都成功了嘛，恭喜哇！"
				+ "但是自己做的决定不后悔，都22了，与其在学校多混两年，不如早点出来，早点积累经验，早点在社会拼搏。"
				+ "诶，我这暴脾气，我还就不信我比你本科生差了 公务员&&和@ooCi_Cioo 一起看了同桌的你，两个傻丫头。"
				+ "四个苹果四个橙子两杯果汁。[风扇]感谢有你。[给力] 同桌的你&&#魅族MX3#如此低投";
		System.out.println(dt.getChinese(s) + "");
	}

	LinkedList<DemoTest[]> list = new LinkedList<DemoTest[]>();

	// static String chinese = "[\u4e00-\u9fa5]+";
	String chinese = "([\u4E00-\u9FA5]|[\uFE30-\uFFA0]|[\\。\\&&\\.]|[0-9])+";
	Pattern p = Pattern.compile(chinese);

	// 提取中文
	public StringBuffer getChinese(String str) {
		Matcher m = p.matcher(str);
		StringBuffer str1 = new StringBuffer();
		while (m.find()) {
			String s = m.group(0);
			str1.append(s);
		}
		return str1;
	}
}
