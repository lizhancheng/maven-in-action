package com.app;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


public class JsonExplain {
	public static void main(String[] args) {
		serialize(resolveJson());
	}
	
	public static User resolveJson() {
		InputStream inputStream = JsonExplain.class.getResourceAsStream("/user.json");
		ObjectMapper mapper = new ObjectMapper();
		
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			User user = mapper.readValue(inputStream, User.class);
			System.out.println(user);
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void serialize(User user) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String userJson = mapper.writeValueAsString(user);
			// User的getter setter没有public修饰，导致Jackson解释出现问题，下面的打印就为空{}
			System.out.println(userJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class IsbnDeserializer extends JsonDeserializer<BigInteger> {
	public BigInteger deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
		String string = p.getValueAsString();
		if (string != null) {
			try {
				return new BigInteger(string.replace("-", ""));
			} catch (NumberFormatException exp) {
				throw new JsonParseException(p, string, exp);
			}
		}
		return null;
	}
}

class User {
	long id;
	String name;
	int age;
	List<String> hobbies;
	Address address;
	
	// 反序列化isbn时使用自定义的 IsbnDeserializer
	@JsonDeserialize(using = IsbnDeserializer.class)
	BigInteger isbn;
	
	// 反序列化时需要提供无参数构造方法，否则无法实例化
	// 所以如果存在带参数构造方法，就要显式声明无参数构造方法
	public User() {}
	
	public User(long id, String name, int age, List<String> hobbies, Address address, BigInteger isbn) {
		setId(id);
		setName(name);
		setAge(age);
		setHobbies(hobbies);
		setAddress(address);
		setIsbn(isbn);
	}
	
	public long getId() {
		return id;
	}
	
	void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	public int getAge() {
		return age;
	}
	
	void setAge(int age) {
		this.age = age;
	}
	
	public List<String> getHobbies() {
		return hobbies;
	}
	
	void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}
	
	public Address getAddress() {
		return address;
	}
	
	void setAddress(Address address) {
		this.address = address;
	}
	
	public BigInteger getIsbn() {
		return isbn;
	}
	
	void setIsbn(BigInteger isbn) {
		this.isbn = isbn;
	}
	
	@Override
	public String toString() {
		return name + ":" + age + ":" + isbn;
	}
}

class Address {
	String city;
	String street;
	
	public String getCity() {
		return city;
	}
	
	void setCity(String city) {
		this.city = city;
	}
	
	public String getStreet() {
		return street;
	}
	
	void setStreet(String street) {
		this.street = street;
	}
}
