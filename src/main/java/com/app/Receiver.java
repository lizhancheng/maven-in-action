package com.app;

import java.util.Properties;
import java.util.Scanner;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;

public class Receiver {
	public static void main(String[] args) {
		new Receiver();
	}
	
	private final String POP_HOST = "pop.139.com";
	private final int POP_PORT = 995;
	private final int MAX_MAILS = 10;
	private String username;
	private String password;
	private Store store;
	
	
	public Receiver() {
		connectServer();
	}
	
	public Properties configureMail() {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "pop3"); // 使用POP3协议
		props.setProperty("mail.pop3.host", POP_HOST);
		props.setProperty("mail.pop3.port", String.valueOf(POP_PORT)); // 加密端口
		props.setProperty("mail.pop3.ssl.enable", "true"); // 启用SSL
		
		return props;
	}
	
	public void connectServer() {
		Session session = Session.getDefaultInstance(configureMail());
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.println("请输入139邮箱用户名：");
			username = scanner.nextLine();
			
			System.out.println("请输入139邮箱授权码：");
			password = scanner.nextLine();
			
			store = session.getStore();
			store.connect(username, password);
			
			openInbox();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openInbox() {
		try {
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			
			Message[] messages = inbox.getMessages();
			int start = Math.max(0, messages.length - MAX_MAILS); // 从最新邮件开始
			System.out.println("共收到 " + messages.length + " 封邮件，下面展示最新 " + MAX_MAILS + " 封：\n");
			
			for (int i = messages.length - 1; i >= start; i --) {
				Message msg = messages[i];
				System.out.println("------------------- 邮件 " + (i + 1) + " -------------------");
				System.out.println("发件人：" + InternetAddress.toString(msg.getFrom()));
				System.out.println("主题：" + msg.getSubject());
				System.out.println("发送时间：" + msg.getSentDate());
				System.out.println("正文：" + getTextContent(msg)); // 提取正文
				System.out.println("-----------------------------------------------------------\n");
			}
			
			// 关闭资源
			inbox.close();
			store.close();
			System.out.println("邮件接收完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getTextContent(Part part) {
		try {
			if (part.isMimeType("text/plain")) {
				return part.getContent().toString();
			} else if (part.isMimeType("text/html")) {
				return "【HTML内容】" + part.getContent().toString();
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				// 简单处理：取第一部分作为正文
				for (int i = 0; i < multipart.getCount(); i ++) {
					String content = getTextContent(multipart.getBodyPart(i));
					
					if (content != null && !content.isEmpty()) {
						return content;
					}
				}
			}
			
			return "【无法解析的内容类型】";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
