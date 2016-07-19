package com.twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;
import javax.jws.WebService;
import org.json. simple.JSONObject;


public class dbConnection {

	public static Connection config(){

		String JDBC_DRIVER = "com.mysql.jdbc.Driver";  

		String DB_URL = "jdbc:mysql://localhost/twitter";

		 

		  //  Database credentials

		  String USER = "root";

		  String PASS = "priyankat";

		Connection conn = null;

		  Statement stmt = null;

		  try{

		      //STEP 2: Register JDBC driver

		      Class.forName("com.mysql.jdbc.Driver");



		      //STEP 3: Open a connection

		      System.out.println("Connecting to database...");

		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      return conn;

		  }catch(Exception e){

		      //Handle errors for Class.forName

		      e.printStackTrace();

		  }

		Connection obj = null;

		return obj;

		}
	
}
