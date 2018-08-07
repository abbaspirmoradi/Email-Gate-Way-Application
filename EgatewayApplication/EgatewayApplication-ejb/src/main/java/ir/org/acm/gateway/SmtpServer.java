package ir.org.acm.gateway;

import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import ir.org.acm.util.GatewayUtil;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

@Stateless
public class SmtpServer implements SmtpServerInterface {

    private final static String SERVER_NAME = "127.0.0.1";
    private final static Logger LOGGER = Logger.getLogger(SmtpServer.class);

    @Override
    public boolean sendmail(Email email, User user, File file) {       
        String destmailid = email.getReciever();
        String sendrmailid = user.getEmail();

        final String uname = user.getEmail();
        final String pwd = user.getPassword();

        Properties propvls = new Properties();
        propvls.put("mail.smtp.auth", "true");
        propvls.put("mail.smtp.starttls.enable", "true");
        propvls.put("mail.smtp.host", SERVER_NAME);
        propvls.put("mail.smtp.port", "25");
        propvls.put("mail.smtp.debug", "true");

        Session sessionobj = Session.getInstance(propvls,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, pwd);
            }
        });

        try {

            Message messageobj = new MimeMessage(sessionobj);
            messageobj.setFrom(new InternetAddress(sendrmailid));
            messageobj.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destmailid));

            messageobj.setSentDate(new Date());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            messageobj.setHeader("sendDate", df.format(today));

            if (null != email.getType()) {
                switch (email.getType()) {
                    case DRAFT:
                        messageobj.setFlag(Flags.Flag.DRAFT, true);
                        break;
                    case DELETED:
                        messageobj.setFlag(Flags.Flag.DELETED, true);
                        break;
                    default:
                        messageobj.setFlag(Flags.Flag.RECENT, true);
                        break;
                }
            }

            if (file != null) {
                Multipart multipart = new MimeMultipart();
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                multipart.addBodyPart(mimeBodyPart);
                mimeBodyPart.attachFile(file);
                multipart.addBodyPart(mimeBodyPart);
                messageobj.setContent(multipart);
                messageobj.setHeader("fileName", email.getAttachmentFileName());
                mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"; name=\""+email.getAttachmentFileName()+"\"");
            } else {
                messageobj.setText(email.getBody());
            }

            messageobj.setHeader("messageBody", email.getBody());
            messageobj.setSubject(email.getSubject());
            messageobj.setHeader("messageId", String.valueOf(GatewayUtil.getInboxMessages(user).length + 1));

            Transport.send(messageobj);
            System.out.println("Your email sent successfully....");
            return true;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

}
