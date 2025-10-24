package com.app;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonExplain {
	public static void main(String[] args) {
		resolveJson();
	}
	
	public static void resolveJson() {
		InputStream inputStream = JsonExplain.class.getResourceAsStream("/user.json");
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			User user = mapper.readValue(inputStream, User.class);
			System.out.println(user);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

class User {
	long id;
	String name;
	int age;
	List<String> hobbies;
	Address address;
	
	long getId() {
		return id;
	}
	
	void setId(long id) {
		this.id = id;
	}
	
	String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	int getAge() {
		return age;
	}
	
	void setAge(int age) {
		this.age = age;
	}
	
	List<String> getHobbies() {
		return hobbies;
	}
	
	void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}
	
	Address getAddress() {
		return address;
	}
	
	void setAddress(Address address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		return name + ":" + age;
	}
}

class Address {
	String city;
	String street;
	
	String getCity() {
		return city;
	}
	
	void setCity(String city) {
		this.city = city;
	}
	
	String getStreet() {
		return street;
	}
	
	void setStreet(String street) {
		this.street = street;
	}
}
