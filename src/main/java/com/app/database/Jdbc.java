package com.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {
	public static void main(String[] args) {
		connectDatabase();
	}
	
	static Connection connection;
	static String JDBC_URL = "jdbc:mysql://localhost:3306/spring_test_db";
	static String JDBC_USER = "root";
	static String jDBC_PASSWORD = "123456";
	static String getAllUsersSql = "SELECT id, address, age, name FROM user;";
	static String getConghuaUser = "SELECT * FROM user WHERE address = ?";
	
	public static void connectDatabase() {
		try (Connection innerConnection = DriverManager.getConnection(JDBC_URL, JDBC_USER, jDBC_PASSWORD)) {
			connection = innerConnection;
			getAllUsers();
			getSpecificUser();
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
	
	public static void getAllUsers() {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet resultSet = statement.executeQuery(getAllUsersSql)) {
				while (resultSet.next()) {
					// 索引从 1 开始，而不是 0
					long id = resultSet.getLong(1);
					String name = resultSet.getString("name");
					String address = resultSet.getString("address");
					System.out.println(id + " " + name + " " + address);
				}
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
	
	public static void getSpecificUser() {
		try (PreparedStatement preparedStatement = connection.prepareStatement(getConghuaUser)) {
			preparedStatement.setObject(1, "conghua");
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long id = resultSet.getLong("id");
					String name = resultSet.getString("name");
					String address = resultSet.getString("address");
					String occupation = resultSet.getString("occupation");
					System.out.println(id + " " + name + " " + address + " " + occupation);
				}
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
}
