package com.twitter;

import java.sql.*;
import javax.jws.WebService;
import org.json. simple.JSONObject;

import com.twitter.dbConnection;


@WebService
public class login {

	public String mylogin(String username, String password) throws SQLException{

		//dbConnection connection = new dbConnection();

		//Connection conn = connection.config();
		
		ConnectionPoolManager connection = new ConnectionPoolManager();
		Connection conn = connection.getConnectionFromPool();

		JSONObject resultSet = new JSONObject();

		Statement stmt = null;

		stmt = conn.createStatement();

		      String query;

		      query = "SELECT * FROM users WHERE password = SHA1('"+password+"') AND username='" +username+ "'";

		      try {

		ResultSet rs = stmt.executeQuery(query);


		while (rs.next()) {

		                
						resultSet.put("username", rs.getString(1));

		                resultSet.put("firstname", rs.getString(2));

		                resultSet.put("lastname", rs.getString(3));

		              

		            }


		} catch (SQLException e) {

		// TODO Auto-generated catch block

		e.printStackTrace();

		//return null;

		}

		  System.out.println(resultSet.toJSONString());

		      return resultSet.toJSONString();

		//return 1;

		}

}
