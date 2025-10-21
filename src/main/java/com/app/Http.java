package com.app;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Http {
	public static void main(String[] args) {
		new ClientRequest();
	}
}

class ClientRequest {
	 static OkHttpClient httpClient = new OkHttpClient();
	 static Request request;
	 
	 public ClientRequest() {
		 setRequest();
		 getResponse();
	 }
	 
	 public void setRequest() {
		 request = new Request.Builder()
				 .url("https://www.sina.com.cn/")
				 .header("User-Agent", "OkHttpClient")
				 .header("Accept", "*/*")
				 .build();
	 }
	 
	 public void getResponse() {
		 try (Response response = httpClient.newCall(request).execute()) {
			 System.out.println(response.code());
			 System.out.println(response.body().string());
			 System.out.println(response.header("Content-Type"));
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
}
