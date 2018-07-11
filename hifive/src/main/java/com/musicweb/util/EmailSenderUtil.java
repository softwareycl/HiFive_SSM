package com.musicweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSenderUtil {
	public void sendMail(String to, String code) throws RuntimeException, IOException, MessagingException{
        Properties props = new Properties();

        //从配置文件读取内容
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("email.properties");
        props.load(in);

        Session mailSession = Session.getDefaultInstance(props);

        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(props.getProperty("username")));
        msg.addRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        msg.setSubject(props.getProperty("subject"));
        msg.setContent(
                "<h5>" + props.getProperty("content1") + "</h5>"
                        + "<h5><a href='" + props.getProperty("url") + "/" + code + "'>【激活账号】</a></h5>"
                        + "<h5>" + props.getProperty("content2") + "</h5>"
                        + "<h5>" + props.getProperty("url") + "/" + code + "</h5>",
                "text/html;charset=UTF-8");
        msg.saveChanges();

        Transport transport = mailSession.getTransport(props.getProperty("mail.transport.protocol"));
        transport.connect(props.getProperty("mail.smtp.host"),
                props.getProperty("username"),
                props.getProperty("password"));
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
}
