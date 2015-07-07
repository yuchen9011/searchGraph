package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.index.CopyOfSearch;
import com.index.Search;
import com.index.Search_New;

public class CopyOfMainFrame extends JFrame implements ActionListener {

	JButton jb = null;
	JPanel jPanel = null;
	JTextField jtf = null;
	JTextField jtfTopK = null;
	JTextArea area = null;
	String DB_PATH = "graph/graph_new.db";
	
	int containsNums = 3;
	int distance = 7;
	String hashTablePath = "sub_graph_test";
	
//	GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH); //开启数据库需要568ms
	GraphDatabaseService graphDb;
	//关闭
	
	
	public CopyOfMainFrame() {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH); //开启数据库需要568ms
		
		this.setTitle("基于图结构的多关键词查询系统");
		this.setBounds(300, 200, 400, 500);
		this.setResizable(true);
		jPanel = new JPanel();
		jb = new JButton("查询");
		jb.addActionListener(this);
		jtf = new JTextField(30);
		jtfTopK = new JTextField(2);
		jtf.setBounds(150, 10, 270, 30);

		area = new JTextArea(30, 60);
		area.setVisible(true);

		JScrollPane scroll = new JScrollPane(area);
		// 分别设置水平和垂直滚动条总是出现
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		jPanel.add(jtf);
		jPanel.add(jb);
		jPanel.add(scroll);

		this.add(jPanel);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setSize(800, 600);
		this.setVisible(true);

		
	}

	public static void main(String[] args) 
	{
		
		CopyOfMainFrame t = new CopyOfMainFrame();
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{

		CopyOfSearch cos = new CopyOfSearch();
		if (event.getSource() == jb) // 点击 “查询” 按钮
		{
			area.setText("");   	// 清空area
			String keywords = jtf.getText().trim();  //文本框输入的内容，关键字以空格间隔
			int topk = Integer.parseInt(jtfTopK.getText().trim());
			// ArrayList<String> al = s.test(keywords);
			if (keywords.equals("")) 
			{
				System.out.println("请输入关键词");
				area.append("请输入关键词");
			}
			else
			{
				// 用户查询，返回包含关键词的子图信息
				Search_New sn = new Search_New();
				long satrt = System.currentTimeMillis();
				
				ArrayList<String> al = sn.getRecord(keywords, hashTablePath , distance, containsNums);
				System.out.println(System.currentTimeMillis() - satrt);
//				

				int k = 20;   //需要返回的子图个数
				
				
				for (int i = 0; i < al.size(); i++) 
				{
					System.out.println(al.get(i));
				}
				
				
				area.setText(""); // 清空
				if (al.size() == 0) 
				{
					area.append("没有查询到结果");
				}
				else
				{
					area.append("序号"+"\t"+"图编号"+"\t"+"偏移量"+"\t"+"包含个数"+"\t"+"得分"+"\r\n");
					if (k >= al.size()) 
					{
						for (int i = 0; i < al.size(); i++)
						{
							area.append(i +"\t" + al.get(i)+"\r\n");
						}
					}
					else 
					{
						for (int i = 0; i < k; i++) 
						{
							area.append(i +"\t" + al.get(i)+"\r\n");
						}
					}
				}
			}
		}
	}
}
