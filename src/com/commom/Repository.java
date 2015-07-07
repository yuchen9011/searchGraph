package com.commom;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class Repository {
	
	private final GraphDatabaseService graphDb;
	private final Index<Node> index;
	private final Node userRefNode;

	public Repository(GraphDatabaseService db, Index<Node> index) {
		this.graphDb = db;
		this.index = index;
		this.userRefNode = getUserRootNode(db);
	}

	private Node getUserRootNode(GraphDatabaseService graphDb) {
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
				RelTypes.KNOWS, Direction.OUTGOING);
		if (rel != null) {
			return rel.getEndNode();
		} else {
			Transaction tx = graphDb.beginTx();
			try {
				Node refNode = graphDb.createNode();
				graphDb.getReferenceNode().createRelationshipTo(refNode,
						RelTypes.KNOWS);
				tx.success();
				return refNode;
			} finally {
				tx.finish();
			}
		}
	}

	//创建一个用户
	public UserImpl createUser(String id, String userName, String city, String url,
			String gender, String followernum, String friendsnum,
			String statusesnum, String favouritesnum, String created_at)
			throws Exception {

		Transaction tx = this.graphDb.beginTx();
		try {

			Node newUserNode = this.graphDb.createNode();
			userRefNode.createRelationshipTo(newUserNode, RelTypes.KNOWS);
			Node alreadyExist = index.get(UserImpl.ID, id).getSingle();
			if (alreadyExist != null) {
				tx.failure();
				throw new Exception("User whit this name already exists");
			}
			newUserNode.setProperty(UserImpl.ID, id);
			newUserNode.setProperty(UserImpl.USERNAME, userName);
			newUserNode.setProperty(UserImpl.CITY, city);
			newUserNode.setProperty(UserImpl.URL, url);
			newUserNode.setProperty(UserImpl.GENDER, gender);
			newUserNode.setProperty(UserImpl.FOLLOWERSNUM, followernum);
			newUserNode.setProperty(UserImpl.FRIENDSSUM, friendsnum);
			newUserNode.setProperty(UserImpl.STATUSESNUM, statusesnum);
//			newUserNode.setProperty(UserImpl.FAVOURITESNUM, favouritesnum);
			newUserNode.setProperty(UserImpl.CREATED_AT, created_at);
			index.add(newUserNode, UserImpl.ID, id);
			tx.success();
			return new UserImpl(newUserNode);
		} finally {
			tx.finish();
		}
	}

	//创建一个微博
	public WeiboImpl createWeibo(String id, String date, String text,
			String source, String repostsnum, String commentsnum,
			String attitudesnum, String uid, String topic) throws Exception {

		Transaction tx = this.graphDb.beginTx();
		try {

			Node newWeiboNode = this.graphDb.createNode();
			userRefNode.createRelationshipTo(newWeiboNode, RelTypes.KNOWS);
			Node alreadyExist = index.get(UserImpl.ID, id).getSingle();
			if (alreadyExist != null) {
				tx.failure();
				throw new Exception("Person whit this name already exists");
			}
			newWeiboNode.setProperty(WeiboImpl.ID, id);
			newWeiboNode.setProperty(WeiboImpl.DATE, date);
			newWeiboNode.setProperty(WeiboImpl.TEXT, text);
			newWeiboNode.setProperty(WeiboImpl.SOURCE, source);
			newWeiboNode.setProperty(WeiboImpl.REPOSTSNUM, repostsnum);
			newWeiboNode.setProperty(WeiboImpl.COMMENTSNUM, commentsnum);
			newWeiboNode.setProperty(WeiboImpl.ATTITUDESNUM, attitudesnum);
			newWeiboNode.setProperty(WeiboImpl.UID, uid);
			newWeiboNode.setProperty(WeiboImpl.TOPIC, topic);

			index.add(newWeiboNode, WeiboImpl.ID, id);
			tx.success();
			return new WeiboImpl(newWeiboNode);
		} finally {
			tx.finish();
		}
	}

	public UserImpl getUserById(String id) {
		Node userNode = index.get(UserImpl.ID, id).getSingle();
		if (userNode == null) {
			return null;
		}
		return new UserImpl(userNode);
	}

	public WeiboImpl getWeiboById(String id) {
		Node weiboNode = index.get(UserImpl.ID, id).getSingle();
		if (weiboNode == null) {
			return null;
		}
		return new WeiboImpl(weiboNode);
	}
}
