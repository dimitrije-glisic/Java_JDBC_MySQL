package student.db;


import java.sql.*;

public class DB {
	
	/*
	 sql package
	 load and register driver
	 configure
	 session
	 
	 *
	 *
	 */
	
	public static final String serverName = "localhost";
	public static final String database = "DOMACI";
	public static final String username = "root";
	public static final String password = "+++dimi96gm";
	public static final int port = 1433;
	
	public static  final String connectionString = 
			"jdbc:sqlserver://" + serverName + ":" + port + ";" + "database=" +
			database + ";user="+username+";password="+password;
	//------------------------------
	/**
	 make a connection
	 */
	
	private Connection connection;
	
	private static DB db;
	
	
	
	private DB(){
		try{
			connection = DriverManager.getConnection(connectionString);
		} catch(Exception e){
			System.out.println(e);
		}
	}
	
	
	public static DB getInstance(){
		if(db == null)
			db = new DB();
		return db;
	}
	
	
	public Connection getConnection(){
		return connection;
	}
	
}







