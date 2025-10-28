package com.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
	
	public static void insertUser() throws SQLException {
		try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSql)) {
			preparedStatement.setObject(1, 15);
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
	
	public static void execute() {
		try {
			openDatabase();
			
			insertUser();
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
