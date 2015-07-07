package com.commom;

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

public class MainFrame extends JFrame implements ActionListener {

	JButton jb = null;
	JPanel jPanel = null;
	JTextField jtf = null;
	JTextArea area = null;
	String DB_PATH = "graph/graph_new.db";
//	GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH); //开启数据库需要568ms
	GraphDatabaseService graphDb;
	//关闭
	
	
	public MainFrame() {

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH); //开启数据库需要568ms
		
		this.setTitle("基于图结构的多关键词查询系统");
		this.setBounds(300, 200, 400, 500);
		this.setResizable(true);
		jPanel = new JPanel();
		jb = new JButton("查询");
		jb.addActionListener(this);
		jtf = new JTextField(30);
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
		// TODO Auto-generated method stub
		MainFrame t = new MainFrame();
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{

		CopyOfSearch cos = new CopyOfSearch();
		if (event.getSource() == jb) // 点击 “查询” 按钮
		{
			area.setText("");   // 清空area
			String keywords = jtf.getText().trim();  //文本框输入的内容，关键字以空格间隔
			// ArrayList<String> al = s.test(keywords);
			if (keywords.equals("")) 
			{
				System.out.println("请输入关键词");
				area.append("请输入关键词");
			}
			else
			{
				ArrayList<String> al = cos.getContainString(keywords, 3, graphDb);
				graphDb.shutdown();
				area.setText(""); // 清空
				if (al.size() == 0) 
				{
					area.append("没有查询到结果");
				}
				else
				{
					for (int i = 0; i < al.size(); i++) 
					{
						area.append(al.get(i) + "\r\n"); // 显示结果
					}
				}
			}
		}
	}
	
}
