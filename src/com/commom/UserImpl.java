package com.commom;

import org.neo4j.graphdb.Node;

public class UserImpl implements User{
	
	static final String NODEID = "nodeid";
	static final String ID = "id";
	static final String USERNAME = "name";
	static final String CITY = "city";
	static final String URL = "url";
	static final String GENDER = "gender";
	static final String FOLLOWERSNUM = "followernum";
	static final String FRIENDSSUM = "friendsnum";
	static final String STATUSESNUM = "statusesnum";
	static final String CREATED_AT = "created_at";
	
    private final Node userNode;
	
	public UserImpl(Node node) {
		this.userNode = node;
	}
	
	public Node getUserNode(){
		return this.userNode;
	}
	
	public String getID(){
		return (String) this.userNode.getProperty(ID);
	}
	public String getUserName(){
		return (String) this.userNode.getProperty(USERNAME);
	}
	public String getCity(){
		return (String) this.userNode.getProperty(CITY);
	}
	public String getUrl(){
		return (String) this.userNode.getProperty(URL);
	}
	public String getGender(){
		return (String) this.userNode.getProperty(GENDER);
	}
	public int getFollowersnum(){
		return (int) this.userNode.getProperty(FOLLOWERSNUM);
	}
	public int getFriendsnum(){
		return (int) this.userNode.getProperty(FRIENDSSUM);
	}
	public int getStatusesnum(){
		return (int) this.userNode.getProperty(STATUSESNUM);
	}
//	public int getFavouritesnum(){
//		return (int) this.userNode.getProperty(FAVOURITESNUM);
//	}
	public String getCreated_at(){
		return (String) this.userNode.getProperty(CREATED_AT);
	}
	public String getNodeid()
	{
		return (String) this.userNode.getProperty(NODEID);
	}
	
	public String getContent() {
		return   
//				  this.getNodeID() + "," +
				  this.getID() + "," +
				  this.getUserName() + "," + 
				  this.getCity() + "," +
				  this.getUrl() + "," +
				  this.getGender() + "," +
				  this.getFollowersnum() + "," +
				  this.getFriendsnum() + "," +
				  this.getStatusesnum() + "," +
				  this.getCreated_at()
				;
	}
	
	@Override
	public String toString() {
		return   
								
//				  this.getID() +
//				  this.getID() +
				  this.getUserName() + " "+
				  this.getCity() 
//				   this.getUrl() +
//				  this.getGender() +
//				  this.getFollowersnum() +
//				  this.getFriendsnum() +
//				  this.getStatusesnum() +
//				  this.getFavouritesnum() +
//				  + this.getCreated_at()
				;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof UserImpl && this.userNode.equals(((UserImpl) obj).getUserNode());
	}
	@Override
	public int hashCode() {
		return this.userNode.hashCode();
	}
}
