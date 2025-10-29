package com.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TransactionSql {
	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/spring_test_db";
	private static final String DATABASE_USER = "root";
	private static final String DATABASE_PASSWORD = "123456";

	
	private static String insertUserSql = "INSERT INTO user (id, name, first_name, last_name, age, occupation, address, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static String updateUserSql = "UPDATE user SET age=? WHERE id=?";
	private static Connection connection;
	
	public static void main(String[] args) {
		execute();
	}
	
	public static void openDatabase() {
		try {
			connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
			connection.setAutoCommit(false);
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
	
	// HikariCP 为连接池（Hikari Connection Pool），避免总是打开连接、操作、关闭连接
	// 创建和销毁JDBC连接的开销就太大了，为了避免频繁创建销毁，可以通过连接池复用创建好的连接
	public static HikariDataSource openByHikari() throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(DATABASE_URL);
		config.setUsername(DATABASE_USER);
		config.setPassword(DATABASE_PASSWORD);
		
		config.setAutoCommit(false);
		
		config.addDataSourceProperty("connectionTimeout", "1000");
		config.addDataSourceProperty("idleTimeout", "60000");
		config.addDataSourceProperty("maximumPoolSize", "10");
		
		HikariDataSource dataSource = new HikariDataSource(config);
		connection = dataSource.getConnection();
		return dataSource;
	}
	
	public static void insertUser() throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql)) {
			preparedStatement.setObject(1, 16);
			User userInfo = Jdbc.getUserData();
			preparedStatement.setObject(2, userInfo.name);
			preparedStatement.setObject(3, userInfo.first_name);
			preparedStatement.setObject(4, userInfo.last_name);
			preparedStatement.setObject(5, userInfo.age);
			preparedStatement.setObject(6, userInfo.occupation);
			preparedStatement.setObject(7, userInfo.address);
			preparedStatement.setObject(8, userInfo.password);
			
			int n = preparedStatement.executeUpdate();
			if (n == 1) {
				System.out.println("Insert succeed!");
			} else {
				System.out.println("Failed insert.");
			}
		}
	}
	
	public static void updateUser() throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSql)) {
			preparedStatement.setObject(1, 0);
			preparedStatement.setObject(2, 12);
			
			int n = preparedStatement.executeUpdate();
			if (n == 1) {
				System.out.println("Update succeed!");
			} else {
				System.out.println("Failed update.");
			}
		}
	}
	
	public static void batchUpdate() throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSql)) {
			int[] ages = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
			
			for (int age : ages) {
				preparedStatement.setObject(1, age * 10);
				preparedStatement.setObject(2, age);
				preparedStatement.addBatch();
			}
			// executeBatch 执行批处理
			preparedStatement.executeBatch();
		}
	}
	
	public static void execute() {
		HikariDataSource dataSource = null;
		try {
			// openDatabase();
			dataSource = openByHikari();
			// updateUser();
			// insertUser();
			batchUpdate();
			connection.commit();
		} catch (SQLException exp) {
			exp.printStackTrace();
			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException childExp) {
				childExp.printStackTrace();
			}
		} finally {
			closeDatabase();
			if (dataSource != null) {
				dataSource.close();
			}
		}
	}
	
	public static void closeDatabase() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
}
