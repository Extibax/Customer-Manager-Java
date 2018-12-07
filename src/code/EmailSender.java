/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 * Clase que se encarga del envio del correo al usuario indicado en el panel emergente
 * Extiende de Thread ya que habia choques con la clase Reminders
 * @author SrExtibax
 */

public class EmailSender extends Thread{
    
    String subject;
    String messageText;
    Connection myConnection;
    
    /**
     * Constructor de la clase Reminders que pide los datos necesario para el envio
     * @param subject
     * @param messageText
     * @param myConnection 
     */
    
    public void EmailSender(String subject, String messageText, Connection myConnection){
        
        this.subject = subject;
        this.messageText = messageText;
        this.myConnection = myConnection;
        
    }
    
    /**
     * Metodo Run de la clase heredada Thread
     */
    
    @Override
    public void run(){
        
        try{
            
            String host ="smtp.gmail.com" ;
            String user = "extibaxmanager@gmail.com";
            String pass = "extibax20";
            FileReader reader = new FileReader("config.properties");
            Properties properties = new Properties();
            properties.load(reader);
            
            String to = properties.getProperty("correo");
            String from = "extibaxmanager@gmail.com";
            boolean sessionDebug = false;

            Properties props = System.getProperties();

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(subject); msg.setSentDate(new Date());
            msg.setText(messageText);

           Transport transport=mailSession.getTransport("smtp");
           transport.connect(host, user, pass);
           transport.sendMessage(msg, msg.getAllRecipients());
           transport.close();
           System.out.println("message send successfully");
           
        }catch(MessagingException ex)
        {
            
            JOptionPane.showMessageDialog(null, "No se ha enviado al correo la obligacion, Por favor verifique "
                    + "que esta seleccionado el correo en la seccion de Obligaciones/Establecer Gmail.");
            System.out.println(ex);
            
        } catch (IOException ex) {
            
            Logger.getLogger(EmailSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
