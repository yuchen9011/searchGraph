package com.commom;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class WeiboFactoryImpl implements WeiboFactory {

	private final GraphDatabaseService graphDb;
	private final Node weiboFactoryNode;
	
	public WeiboFactoryImpl(GraphDatabaseService graphDb){
		this.graphDb = graphDb;
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(RelTypes.KNOWS, Direction.BOTH);
		if(rel == null)
		{
			weiboFactoryNode = graphDb.createNode();
			graphDb.getReferenceNode().createRelationshipTo(weiboFactoryNode, RelTypes.KNOWS);	
		}
		else
		{
			weiboFactoryNode = rel.getEndNode();
		}
	}

	
	@Override
	public Weibo createWeibo() {
		Node node = graphDb.createNode();
		weiboFactoryNode.createRelationshipTo(node, RelTypes.KNOWS);
		return new WeiboImpl(node);
	}
	
}
