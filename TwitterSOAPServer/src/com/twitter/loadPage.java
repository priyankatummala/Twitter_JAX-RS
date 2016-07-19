package com.twitter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import org.json. simple.JSONObject;

import com.google.gson.Gson;
import com.twitter.dbConnection;

import infoObj.*;

@WebService

public class loadPage {
	
	//dbConnection connection = new dbConnection();

	//Connection conn = connection.config();
	
	ConnectionPoolManager connection = new ConnectionPoolManager();
	Connection conn = connection.getConnectionFromPool();
	
	public String loadpageProfile (String username){
		System.out.println("in loadpageProfile : "+username);
		
		JSONObject resultSet = new JSONObject();

		Statement stmt = null;
		String result = "";
		JSONObject res = new JSONObject();
		List<homeprofile> profile = new ArrayList<homeprofile>();

		try{
			stmt = conn.createStatement();
			String query = "	SELECT " +
							"concat(firstname,' ', lastname) uname, u.thandle, F.FOLLOWERS,T.FOLLOWING, TW.TWEETS, birthday, location,phone" +
							"		FROM" +
							"		(select COUNT(*) FOLLOWERS " +
							"		from followers	where uname='"+username+"')F" +
							",(select count(*) FOLLOWING" +
							"		from followers " +
							"		where follower='"+username+"')T," +
							"		(select count(*) TWEETS" +
							"		from tweets" +
							"		where uname='"+username+"') TW," +
							"		users  u" +
							"		where u.username='"+username+"'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				
				res.put("fullname", rs.getString(1));

                res.put("thandle", rs.getString(2));

                res.put("numFollowers", rs.getString(3));
                res.put("numFollowing", rs.getString(4));

                res.put("numTweets", rs.getString(5));

                res.put("birthday", rs.getString(6));
                
                res.put("location", rs.getString(7));

                res.put("phone", rs.getString(8));
	
				}
			
		}
		catch (SQLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

			//return null;

			}
		
	      //return result;
		
		 System.out.println(res.toJSONString());

	      return res.toJSONString();
		
	}
	
	public String  signup(String username, String password, String fname, String lname, String handle){
		JSONObject resultSet = new JSONObject();

		Statement stmt = null;
		String result = "";
		
		try{
			stmt = conn.createStatement();
			String insertUser="INSERT INTO users (`username`, `firstname`, `lastname`, `password`, `thandle`) VALUES " +
					"('" +username+"','" +fname+"','" +lname+"',SHA1('" +password+"'),'" +handle+"');";
			int rowcount = stmt.executeUpdate(insertUser);
			System.out.println(rowcount);
			if (rowcount > 0)
				result="Success";
			}
			catch(SQLException e)   {
				e.printStackTrace();
				return "Username or Twitter Handle is already in use";
			}
			return result;
		
	}
	
	//fetch tweets
	public String  fetchTweets(String username){
		System.out.println("in loadpageProfile : "+username);
		
		JSONObject resultSet = new JSONObject();

		Statement stmt = null;
		String result = "";
		List<homeTweets> tweetinfo = new ArrayList<homeTweets>();

		try{
			stmt = conn.createStatement();
			String getTweets="select distinct t.tweet, concat(u.firstname,' ',u.lastname)usrname, thandle" +
					"			from tweets t	inner join followers f" +
					"			on t.uname='"+username+"' " +
					"   or(t.uname=f.uname and f.follower='"+username+"' )" +
					"		inner join users u" +
					"			on u.username=t.uname" +
					"				order by t.tweettime desc";
			ResultSet rs = stmt.executeQuery(getTweets);
			while (rs.next()){
				String name = rs.getString("usrname");
				String handle = rs.getString("thandle");
				String tweet = rs.getString("tweet");
				homeTweets newTweet = new homeTweets(name, handle, tweet);
				tweetinfo.add(newTweet);
				}
			Gson gson = new Gson();
			String json = gson.toJson(tweetinfo);
			System.out.println("Json string:"+json);
			result=json;
			}
			catch(SQLException e)   {
				e.printStackTrace();
			}
			return result;
		
	}
	
	//fetch my tweets
		public String  fetchMyTweets(String username){
			System.out.println("in loadpageProfile : "+username);
			
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			List<homeTweets> tweetinfo = new ArrayList<homeTweets>();

			try{
				stmt = conn.createStatement();
				String getTweets="select t.tweet, concat(u.firstname,' ',u.lastname)usrname, thandle "
						+ "from twitter.tweets t, twitter.users u "
						+ "where u.username = '"+username+"' and u.username = t.uname order by t.tweettime desc;";
				ResultSet rs = stmt.executeQuery(getTweets);
				while (rs.next()){
					String name = rs.getString("usrname");
					String handle = rs.getString("thandle");
					String tweet = rs.getString("tweet");
					homeTweets newTweet = new homeTweets(name, handle, tweet);
					tweetinfo.add(newTweet);
					}
				Gson gson = new Gson();
				String json = gson.toJson(tweetinfo);
				System.out.println("Json string:"+json);
				result=json;
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
	
	//fetch suggestions
	public String  fetchsuggestions(String username){
		System.out.println("in fetchsuggestions : "+username);
		
		JSONObject resultSet = new JSONObject();

		Statement stmt = null;
		String result = "";
		List<homesuggestion> suggestions = new ArrayList<homesuggestion>();

		try{
			stmt = conn.createStatement();
			String getSuggestions="select concat(firstname,' ', lastname) usrname, thandle from users" +
					"	where " +
					"	username not in (select uname from followers where follower='"+username+"') " +
					"	and username !='"+username+"'	order by usrname limit 2";
			ResultSet rs = stmt.executeQuery(getSuggestions);
			while (rs.next()){
				String name = rs.getString("usrname");
				String handle = rs.getString("thandle");
				homesuggestion newSuggest = new homesuggestion(name, handle);
				suggestions.add(newSuggest);
				}
			Gson gson = new Gson();
			String json = gson.toJson(suggestions);
			System.out.println("Json string:"+json);
			result=json;
			}
			catch(SQLException e)   {
				e.printStackTrace();
			}
			return result;
		
	}
	
	// register a tweet
	
		public String  registerTweet(String username, String tweet){
			System.out.println("in registerTweet : "+username);
		
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			
			try{
				stmt = conn.createStatement();
				String insertTweet="INSERT INTO `twitter`.`tweets` (`uname`, `tweet`, `retweet` ) "
						+ "VALUES "
						+ "('"+username+"', '"+tweet+"', 'N')";
				int rowcount = stmt.executeUpdate(insertTweet);
				System.out.println(rowcount);
				if (rowcount > 0)
					result="Success";
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
		public String  reTweet(String username, String tweet){
			System.out.println("in registerTweet : "+username);
		
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			
			try{
				stmt = conn.createStatement();
				String insertTweet="INSERT INTO `twitter`.`tweets` (`uname`, `tweet`, `retweet` ) "
						+ "VALUES "
						+ "('"+username+"', '"+tweet+"', 'Y')";
				int rowcount = stmt.executeUpdate(insertTweet);
				System.out.println(rowcount);
				if (rowcount > 0)
					result="Success";
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
		public String  updateInfo(String username, String bday, String loc, String phone){
			System.out.println("in registerTweet : "+username);
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			
			try{
				stmt = conn.createStatement();
				String insertUpd="UPDATE `twitter`.`users` "
						+ "SET `birthday`='"+bday+"', `location`='"+loc+"', `phone`='"+phone+"' WHERE `username`='"+username+"'";
				int rowcount = stmt.executeUpdate(insertUpd);
				System.out.println(rowcount);
				if (rowcount > 0)
					result="Success";
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
		public String  followuser(String curUser, String thandle){
			System.out.println("in followuser : ");
			
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			
			try{
				stmt = conn.createStatement();
				String uname="select username from `twitter`.`users` where thandle='"+thandle+"'";
				ResultSet rs = stmt.executeQuery(uname);
				String username="";
				while (rs.next()){
					username = rs.getString("username");
					}
				System.out.println("username to follow"+username);
				String followQuery="INSERT INTO `twitter`.`followers` (`uname`, `follower`) " +
						"VALUES ('"+username+"', '"+curUser+"')";
				int rowcount = stmt.executeUpdate(followQuery);
				System.out.println(rowcount);
				if (rowcount > 0)
					result="Success";
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
		public String  fetchFollowing(String username){
			System.out.println("in fetchtweetfollowing : "+username);
			
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			List<user> users = new ArrayList<user>();

			try{
				stmt = conn.createStatement();
				String fetchfollowing="select concat(firstname,' ',lastname)usrname, thandle " +
						"from users where username in" +
						" (select uname from followers where follower='"+username+"')";
				ResultSet rs = stmt.executeQuery(fetchfollowing);
				while (rs.next()){
					String name = rs.getString("usrname");
					String handle = rs.getString("thandle");
					user newUser = new user(name, handle);
					users.add(newUser);
					}
				Gson gson = new Gson();
				String json = gson.toJson(users);
				System.out.println("Json string:"+json);
				result=json;
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
			
		}
		
		public String  fetchFollowers(String username){
		
			System.out.println("in fetchsuggestions : "+username);
			
			JSONObject resultSet = new JSONObject();

			Statement stmt = null;
			String result = "";
			List<homesuggestion> suggestions = new ArrayList<homesuggestion>();

			try{
				stmt = conn.createStatement();
				String getSuggestions="select concat(firstname,' ', lastname) usrname, thandle from users" +
						"	where " +
						"	username not in (select uname from followers where follower='"+username+"') " +
						"	and username !='"+username+"'";
				ResultSet rs = stmt.executeQuery(getSuggestions);
				while (rs.next()){
					String name = rs.getString("usrname");
					String handle = rs.getString("thandle");
					homesuggestion newSuggest = new homesuggestion(name, handle);
					suggestions.add(newSuggest);
					}
				Gson gson = new Gson();
				String json = gson.toJson(suggestions);
				System.out.println("Json string:"+json);
				result=json;
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;
		}
		
				
		public String  searchResults(String searchTerm){
			
			JSONObject resultSet = new JSONObject();
			System.out.println(searchTerm);

			Statement stmt = null;
			String result = "";
			List<homeTweets> tweetinfo = new ArrayList<homeTweets>();

			try{
				stmt = conn.createStatement();
				String searchTweet="select distinct concat(firstname,' ', lastname) username, thandle, tweet,  thandle" +
						" from tweets t , users u" +
						" where t.tweet like '%"+searchTerm+"%' and u.username=t.uname "; 
				ResultSet rs = stmt.executeQuery(searchTweet);
				while (rs.next()){
					String name = rs.getString("username");
					String handle = rs.getString("thandle");
					String tweet = rs.getString("tweet");
					homeTweets newTweet = new homeTweets(name, handle, tweet);
					tweetinfo.add(newTweet);
					}
				Gson gson = new Gson();
				String json = gson.toJson(tweetinfo);
				System.out.println("Json string:"+json);
				result=json;
				}
				catch(SQLException e)   {
					e.printStackTrace();
				}
				return result;

		}
		
		
		
		
		
		

}
