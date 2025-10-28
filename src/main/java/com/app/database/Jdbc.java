package com.app.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Jdbc {
	public static void main(String[] args) {
		connectDatabase();
	}
	
	static Connection connection;
	static String JDBC_URL = "jdbc:mysql://localhost:3306/spring_test_db";
	static String JDBC_USER = "root";
	static String jDBC_PASSWORD = "123456";
	static String getAllUsersSql = "SELECT id, address, age, name FROM user;";
	static String getConghuaUserSql = "SELECT * FROM user WHERE address = ?";
	static String createUserSql = "INSERT INTO user (name, first_name, last_name, age, occupation, address, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
	static String updateUserSql = "UPDATE user SET age=?, address=?, occupation=? WHERE id=?";
	static String deleteUserSql = "DELETE FROM user WHERE id=?";
	
	public static void connectDatabase() {
		try (Connection innerConnection = DriverManager.getConnection(JDBC_URL, JDBC_USER, jDBC_PASSWORD)) {
			connection = innerConnection;
			// getAllUsers();
			// getSpecificUser();
			// insertUser();
			// updateUser();
			deleteUser();
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
		try (PreparedStatement preparedStatement = connection.prepareStatement(getConghuaUserSql)) {
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
	
	public static String generateRandomString(int length, int middle) {
		String name = "";
		for (int i = 0; i < length; i ++) {
			name += i != 0 ? i == middle ? "." : (char) (97 + 25 * Math.random()) : (char) (97 + 25 * Math.random()) ;
		}
		return name;
	}
	
	public static String getRandomCity() {
		String[] cities = new String[]{ "conghua", "baiyun", "tianhe", "haizhu", "yuexiu", "liwan" };
		Random rd = new Random();
		
		int randomIndex = rd.nextInt(5);
		return cities[randomIndex];
	}
	
	public static String getRandomOccupation() {
		String[] occupations = new String[] { "teacher", "player", "policy" };
		Random rd = new Random();
		
		return occupations[rd.nextInt(2)];
	}
	
	public static int getRandomAge() {
		return (int) Math.round(Math.random() * 100);
	}
	
	public static User getUserData() {
		String name = generateRandomString(7, 3);
		return new User(
				name,
				name.substring(name.indexOf(".") + 1, 7),
				name.substring(0, name.indexOf(".")),
				getRandomAge(),
				getRandomOccupation(),
				getRandomCity(),
				generateRandomString(10, 0)
				);
	}
	
	public static void insertUser() {
		String name = generateRandomString(7, 3);
		String firstName = name.substring(name.indexOf(".") + 1, 7);
		String lastName = name.substring(0, name.indexOf("."));
		int age = getRandomAge();
		String occupation = getRandomOccupation();
		String address = getRandomCity();
		String password = generateRandomString(10, 0);
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(createUserSql, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setObject(1, name);
			preparedStatement.setObject(2, firstName);
			preparedStatement.setObject(3, lastName);
			preparedStatement.setObject(4, age);
			preparedStatement.setObject(5, occupation);
			preparedStatement.setObject(6, address);
			preparedStatement.setObject(7, password);
			
			int n = preparedStatement.executeUpdate();
			System.out.println(n == 1 ? "Insert succeed! " : "Failed insertion.");
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				if (resultSet.next()) {
					long id = resultSet.getLong(1);
					System.out.println("Primary key: " + id);
				}
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
	
	public static void updateUser() {
		int age = getRandomAge();
		String address = getRandomCity();
		String occupation = getRandomOccupation();
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserSql, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setObject(1, age);
			preparedStatement.setObject(2, address);
			preparedStatement.setObject(3, occupation);
			preparedStatement.setObject(4, 12);
			
			int n = preparedStatement.executeUpdate();
			if (n == 1) {
				System.out.println("Update succeed!");
				try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
					if (resultSet.next()) {
						System.out.println(resultSet.getString("name"));
					}
				}
			} else {
				System.out.println("Failed update.");
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
	
	public static void deleteUser() {
		try (PreparedStatement preparedStatement = connection.prepareStatement(deleteUserSql)) {
			preparedStatement.setObject(1, 12);
			int n = preparedStatement.executeUpdate();
			if (n == 1) {
				System.out.println("Delete succeed!");
			} else {
				System.out.println("Failed delete. Affected rows: " + n);
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}
	}
}

class User {
	long id;
	String name;
	String first_name;
	String last_name;
	String occupation;
	int age;
	String address;
	String password;
	
	public User(String name, String first_name, String last_name, int age, String occupation, String address, String password) {
		this.name = name;
		this.first_name = first_name;
		this.last_name = last_name;
		this.age = age;
		this.occupation = occupation;
		this.address = address;
		this.password = password;
	}
	
	public User(long id, String name, String first_name, String last_name, int age, String occupation, String address, String password) {
		this.id = id;
		this.name = name;
		this.first_name = first_name;
		this.last_name = last_name;
		this.age = age;
		this.occupation = occupation;
		this.address = address;
		this.password = password;
	}
}
