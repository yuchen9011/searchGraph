package com.commom;

import org.neo4j.graphdb.Node;

public class WeiboImpl implements Weibo {

//	static final String NODEID = "nodeid";
	static final String ID = "id";
	static final String DATE = "date";
	static final String TEXT = "text";
	static final String SOURCE = "source";
	static final String REPOSTSNUM = "repostsnum";
	static final String COMMENTSNUM = "commentsnum";
	static final String ATTITUDESNUM = "attitudesnum";
	static final String UID = "uid";
	static final String TOPIC = "topic";

	private final Node weiboNode;

	public WeiboImpl(Node node) {
		this.weiboNode = node;
	}

	public Node getWeiboNode() {
		return this.weiboNode;
	}

//	public String getNodeID() {
//		return (String) this.weiboNode.getProperty(NODEID);
//	}
	
	public String getID() {
		return (String) this.weiboNode.getProperty(ID);
	}

	public String getDate() {
		return (String) this.weiboNode.getProperty(DATE);
	}

	public String getText() {
		return (String) this.weiboNode.getProperty(TEXT);
	}

	public String getSource() {
		return (String) this.weiboNode.getProperty(SOURCE);
	}

	public String getUid() {
		return (String) this.weiboNode.getProperty(UID);
	}

	public int getRepostsnum() {
		return (int) this.weiboNode.getProperty(REPOSTSNUM);
	}

	public int getCommentsnum() {
		return (int) this.weiboNode.getProperty(COMMENTSNUM);
	}

	public int getAttitudesnum() {
		return (int) this.weiboNode.getProperty(ATTITUDESNUM);
	}

	public String getTopic() {
		return (String) this.weiboNode.getProperty(TOPIC);
	}
	
	public String getContent() {
		return 
//				  this.getNodeID() +","+
				  this.getID() + ","+
				  this.getDate() + ","+
				  this.getText() + ","+
				  this.getSource() + ","+
				  this.getRepostsnum() + ","+
				  this.getCommentsnum() + ","+
				  this.getAttitudesnum() + ","+
				  this.getUid() + ","+
				  this.getTopic();
	}
	
	@Override
	public String toString() {
		return 
//				  this.getNodeID() +
//				  this.getID() +
//				  this.getDate() +
				  this.getText() +" "+
//				  this.getSource() +
//				  this.getRepostsnum() +
//				  this.getCommentsnum() +
//				  this.getAttitudesnum() +
//				  this.getUid() +
				  this.getTopic();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof UserImpl && this.weiboNode.equals(((WeiboImpl) obj).getWeiboNode());
	}
	@Override
	public int hashCode() {
		return this.weiboNode.hashCode();
	}
}
