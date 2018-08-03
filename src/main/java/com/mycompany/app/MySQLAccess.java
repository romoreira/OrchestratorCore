package com.mycompany.app;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class MySQLAccess {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/kamailio";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "pass";
	private Statement stmt = null;	
	
	public Statement getStmt() {
		return stmt;
	}
	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}
	public static String getJdbcDriver() {
		return JDBC_DRIVER;
	}
	public static String getDbUrl() {
		return DB_URL;
	}
	public static String getUser() {
		return USER;
	}
	public static String getPass() {
		return PASS;
	}
	public Connection createConnection() {
		Connection conn = null;
		
		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open a connection
			System.out.println("Connecting to Orchestrator DataBase...");
			conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected to Orchestrator DataBase...");
			return conn;
		} catch (SQLException e) {
			System.out.println("Erro de Conexão com o Banco");
		}catch(ClassNotFoundException e){
			System.out.println("Classe não encontrada: JDBC");
		}
		return conn;
	}
	public boolean updateServerPriority(String description, int priority){
		try{
			Connection conn = this.createConnection();
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "UPDATE dispatcher SET priority = "+priority+" WHERE description=\""+description+"\";";
			stmt.executeUpdate(sql);
		    stmt.close();
		    conn.close();
		    return true;
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch(Exception e){
			System.out.println("Algum erro no updateServerPriority");
		}
		return false;
	}
	
	public int selectServerPriority(String serverName){
		int priority = -1;
		try{
			Connection conn = this.createConnection();
			//System.out.println("Creating statement...");
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "SELECT priority FROM dispatcher WHERE description=\""+serverName+"\";";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				priority = rs.getInt("priority");
			}
		    rs.close();
		    stmt.close();
		    conn.close();
		    return priority;
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch(Exception e){
			System.out.println("Algum erro no selectServerPriority");
		}
		return priority;
	}
	
	public boolean insertServerToDispatcher(String destination, int priority, String description){
		try{
			Connection conn = this.createConnection();
			stmt = (Statement) conn.createStatement();
			String sql;
			
			sql = "INSERT INTO dispatcher(setid, destination, flags, priority, description) VALUES(?, ?, ?, ?, ?)";
			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(sql);
			
			preparedStmt.setInt(1, 1);
			preparedStmt.setString(2, "sip:"+destination+":5060");
			preparedStmt.setInt(3, 0);
			preparedStmt.setInt(4, priority);
			preparedStmt.setString(5, description);
			
			if(!preparedStmt.execute()){
				conn.close();
				return true;
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch(Exception e){
			System.out.println("Algum erro no updateServerPriority");
		}
		return false;
	}
	public boolean deleteServerToDispatcher(String serverName){
		//System.out.println("Deleting: " + serverName + " to Dispatcher Data Base");
		try{
			Connection conn = this.createConnection();
			System.out.println("Creating statement...");
			stmt = (Statement) conn.createStatement();
			String sql;
			
			sql = "DELETE FROM dispatcher WHERE description=?";
			PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(sql);
			
			preparedStmt.setString(1, serverName);
			
		
			if(!preparedStmt.execute()){
				conn.close();
				System.out.println("Deleted "+ serverName + " to Dispatcher Data Base");
				return true;
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch(Exception e){
			System.out.println("Algum erro no deleteServerToDispatcher");
		}
		return false;
	}
}
