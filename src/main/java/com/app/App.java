package com.app;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class App {
  public static void main(String[] args) {
    System.out.println(Arrays.toString(args));
    new App();
  }

  public App() {
    inputAccount();
    setMailConfig();
    setAuth();
    sendMail();
  }

  private String host = "smtp.139.com";
  private String port = "465";
  private String username;
  private String password;

  private Properties props = new Properties();
  private Authenticator auth;

  private String receiver;
  private String subject = "测试邮件";
  private String content = "这是一封使用Java发送的测试邮件";

  public void inputAccount() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("请输入中国移动账号：");
    username = scanner.nextLine();
    
    System.out.println("请输入授权码码：");
    password = scanner.nextLine();
    
    System.out.println("请填写收件人地址：");
    receiver = scanner.nextLine();
    
    System.out.println("请填写发送主题：");
    subject = scanner.nextLine();
    
    System.out.println("请填写发送内容");
    content = scanner.nextLine();
    
    scanner.close();
  }

  // 配置信息
  public void setMailConfig() {
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.ssl.enable", "true");
    props.put("mail.smtl.ssl.trust", host);
  }

  // 认证器
  public void setAuth() {
    auth = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    };
  }

  public void sendMail() {
    Session session = Session.getInstance(props, auth);
    session.setDebug(true);
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
      message.setSubject(subject);
      message.setText(content);

      Transport.send(message);

      System.out.println("邮件发送成功");
    } catch (MessagingException e) {
      System.out.println("邮件发送失败：" + e.getMessage());
      e.printStackTrace();
    }
  }
}
