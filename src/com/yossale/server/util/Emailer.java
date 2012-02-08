package com.yossale.server.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer {
  
  private static final String[] ADMINS = 
    new String[]{"yossale+obudget@gmail.com","rmerom+obudget@gmail.com",
                                          "oded+obudget@poncz.com"};
  private static final String APP_ADDRESS = "open.budget.proffesionals@gmail.com";
  
  private Logger logger = Logger.getLogger(Emailer.class
      .getName());

  public void sendEmail(String aToEmailAddr,
      String aSubject, String aBody) {
     sendEmail(new String[]{aToEmailAddr}, aSubject, aBody); 
  } 
  
  public void sendHappyMailToAdmins(String subject, String info) {
    sendEmail(ADMINS, "Happy mail! - " + subject, info);
  }
  
  public void sendSadMailToAdmins(String subject, String info) {
    sendEmail(ADMINS, "Sad mail :( - " + subject, info);
  }
  
  public void sendBoringMailToAdmins(String subject, String info) {
    sendEmail(ADMINS, "Boring mail - " + subject, info);
  }  
  
  public void sendMailToAdmins(String subject, String info) {
    sendEmail(ADMINS, subject, info);
  }
  
  public void sendEmail(String[] aToEmailAddr,
      String aSubject, String aBody) {
    
    System.out.println("Sending mail");
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);    

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(APP_ADDRESS, "OBudget Overlord"));
      for (String to : aToEmailAddr) {
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      }     
      
      msg.setSubject(aSubject);
      msg.setText(aBody);
      Transport.send(msg);      

    } catch (AddressException e) {
      logger.warning("Failed to send email due to " + e.getCause());
      e.printStackTrace();
    } catch (MessagingException e) {
      logger.warning("Failed to send email due to " + e.getCause());
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      logger.warning("Failed to send email due to " + e.getCause());
      e.printStackTrace();
    }
  }

}
