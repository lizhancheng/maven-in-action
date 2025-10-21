package com.app;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;

public class Sender {
  public static void main(String[] args) {
    System.out.println(Arrays.toString(args));
    new Sender();
  }

  public Sender() {
    inputAccount();
    inputMailInfo();
    setMailConfig();
    setAuth();
    sendMail();
  }
  private Scanner scanner = new Scanner(System.in);

  private String host = "smtp.139.com";
  private String port = "465";
  private String username;
  private String password;
  
  private String[] attachmentPaths;
  // 图片路径（支持多个）
  private String[] imagePaths;
  // 是否嵌入正文（默认作为附件）
  private boolean isEmbedImage = false;

  private Properties props = new Properties();
  private Authenticator auth;

  private String receiver;
  private String subject = "测试邮件";
  private String content = "这是一封使用Java发送的测试邮件";

  public void inputAccount() {
    System.out.println("请输入中国移动账号：");
    username = scanner.nextLine();
    
    System.out.println("请输入授权码码：");
    password = scanner.nextLine();
  }
  
  public void inputMailInfo() {
	  System.out.println("请填写收件人地址：");
	  receiver = scanner.nextLine();

	  System.out.println("请填写发送主题：");
	  subject = scanner.nextLine();

	  System.out.println("是否需要写发送内容？Y/n");
	  
	  if (scanner.nextLine().toLowerCase().equals("y")) {
		  System.out.println("请填写发送内容");
		  content = scanner.nextLine();
	  }
	  
	  System.out.println("是否需要添加图片？Y/n");
	  if (scanner.nextLine().toLowerCase().equals("y")) {
		  System.out.println("图片添加方式：1-嵌入正文 2-作为附件");
		  String choice = scanner.nextLine();
		  isEmbedImage = choice.equals("1");
		  
		  addImages();
	  }
	  
	  System.out.println("是否需要添加附件？Y/n");
	  if (scanner.nextLine().toLowerCase().equals("y")) {
		  addAttachments();
	  }
	  scanner.close();
  }
  
  public void printWorkingDirectory() {
	  System.out.println("当前工作目录：" + System.getProperty("user.dir"));
  }
  
  public void addImages() {
	  System.out.println("请输入要发送的图片的数量");
	  int imagesCount = scanner.nextInt();
	  // 清除换行符，修复循环时会出现两次输出才让输入内容
	  scanner.nextLine();
	  
	  imagePaths = new String[imagesCount];
	  
	  printWorkingDirectory();
	  
	  for (int i = 0; i < imagesCount; i ++) {
		  System.out.println("请输入第" + (i + 1) + "张图片的完整路径：");
		  imagePaths[i] = scanner.nextLine(); 
	  }
  }
  
  public void addAttachments() {
	  System.out.println("请输入需要发送的附件的数量：");
	  int attachmentsCount = scanner.nextInt();
	  scanner.nextLine();
	  
	  attachmentPaths = new String[attachmentsCount];
	  
	  printWorkingDirectory();
	  
	  for (int i = 0; i < attachmentsCount; i ++) {
		  System.out.println("请输入第" + (i + 1) + "个附件的完整路径：");
		  attachmentPaths[i] = scanner.nextLine(); 
	  }
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
  
  public void resolveImages(Multipart multipart) {
	  if (imagePaths != null) {
		  for (int i = 0; i < imagePaths.length; i ++) {
			  File imageFile = new File(imagePaths[i]);
			  if (!imageFile.exists() || !imageFile.isFile()) {
				  System.out.println("图片不存在或不是文件：" + imagePaths[i]);
				  continue;
			  }
			  
			  BodyPart imagePart = new MimeBodyPart();
			  FileDataSource fileDataSource = new FileDataSource(imageFile);
			  try {
				  imagePart.setDataHandler(new DataHandler(fileDataSource));
				  
				  if (isEmbedImage) {
					  // 嵌入正文：设置 Content-ID（与HTML中的 cid 对应）
					  imagePart.setHeader("Content-ID", "<image" + i + ">");
					  // 隐藏嵌入的图片作为附件显示（部分邮件客户端需要）
					  imagePart.setDisposition(MimeBodyPart.INLINE);
				  } else {
					  // 作为附件：设置文件名（避免中文乱码）
					  imagePart.setFileName(MimeUtility.encodeWord(imageFile.getName()));
				  }
				  multipart.addBodyPart(imagePart);
				  System.out.println("已添加图片：" + imageFile.getName());
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
		  }
	  }
  }
  
  public void resolveAttachments(Multipart multipart) {
	  if (attachmentPaths != null && attachmentPaths.length > 0) {
    	  // 添加附件
    	  for (String path : attachmentPaths) {
    		  File file = new File(path);
    		  if (!file.exists() || !file.isFile()) {
    			  System.out.println("附件不存在或不是文件：" + path);
    			  continue; // 跳过无效文件
    		  }
    		  
    		  BodyPart attachmentPart = new MimeBodyPart();
    		  // 读取文件并设置数据源
    		  FileDataSource dataSource = new FileDataSource(file);
    		  try {
    			  attachmentPart.setDataHandler(new DataHandler(dataSource));
    			  // 设置附件文件名（避免中文乱码）
    			  attachmentPart.setFileName(MimeUtility.encodeWord(file.getName()));
    			  multipart.addBodyPart(attachmentPart);
            	  System.out.println("已添加附件：" + file.getName());
    		  } catch (Exception e) {
    			  e.printStackTrace();
    		  }
    	  }
      }
  }

  public void sendMail() {
    Session session = Session.getInstance(props, auth);
    session.setDebug(true);
    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
      message.setSubject(subject);
      
      // 创建多部分内容（正文+图片+附件）
      Multipart multipart = new MimeMultipart();
      
      // 处理邮件正文（支持嵌入图片）
      BodyPart textPart = new MimeBodyPart();
      if (isEmbedImage && imagePaths != null && imagePaths.length > 0) {
    	  StringBuilder htmlContent = new StringBuilder();
    	  htmlContent.append("<html><body>");
    	  
    	  if (content != null) {
    		  htmlContent.append("<p>")
    		  	.append(content)
    		  	.append("</p>");
    	  }
    	  
    	  for (int i = 0; i < imagePaths.length; i ++) {
    		  htmlContent.append("<img src='cid:image")
    		  	.append(i)
    		  	.append("'>");
    	  }
    	  
    	  htmlContent.append("</body></html>");
    	  
    	  textPart.setContent(htmlContent.toString(), "text/html;charset=UTF-8");
      } else if (content != null) {
    	  // 添加邮件正文
    	  textPart.setText(content);
      }
      multipart.addBodyPart(textPart);
      // 解析图片
      resolveImages(multipart);
      // 解析文件
      resolveAttachments(multipart);
      
      // 将多部分内容设置为邮件内容
      message.setContent(multipart);

      Transport.send(message);

      System.out.println("邮件发送成功");
    } catch (Exception e) {
      System.out.println("邮件发送失败：" + e.getMessage());
      e.printStackTrace();
    }
  }
}
