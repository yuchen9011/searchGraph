package com.commom;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class UserFactoryImpl implements UserFactory{
	private final GraphDatabaseService graphDb;
	private final Node userFactoryNode;
	
	public UserFactoryImpl(GraphDatabaseService graphDb){
		this.graphDb = graphDb;
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(RelTypes.KNOWS, Direction.BOTH);
		if(rel == null)
		{
			userFactoryNode = graphDb.createNode();
			graphDb.getReferenceNode().createRelationshipTo(userFactoryNode, RelTypes.KNOWS);	
		}
		else
		{
			userFactoryNode = rel.getEndNode();
		}
	}
	
	@Override
	public User createUser() {
		Node node = graphDb.createNode();
		userFactoryNode.createRelationshipTo(node, RelTypes.KNOWS);
		return new UserImpl(node);
	}	
}
