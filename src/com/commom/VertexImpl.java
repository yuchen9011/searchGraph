package com.commom;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

public class VertexImpl implements Vertex {

	private final Node underlyingNode = null;

	@Override
	public User getUser() {
		Node userNode = underlyingNode.getSingleRelationship(RelTypes.KNOWS, Direction.BOTH).getStartNode();
		return new UserImpl(userNode);
	}

	@Override
	public Weibo getWeibo() {
		Node weiboNode = underlyingNode.getSingleRelationship(RelTypes.KNOWS, Direction.BOTH).getStartNode();
		return new WeiboImpl(weiboNode);
	}

}
