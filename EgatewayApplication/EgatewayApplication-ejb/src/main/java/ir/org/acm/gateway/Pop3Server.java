package ir.org.acm.gateway;

import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import ir.org.acm.util.Enums;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

@Stateless
public class Pop3Server implements Pop3ServerInterface {

    private final static Logger LOGGER = Logger.getLogger(SmtpServer.class);
    private final static String SERVER_NAME = "127.0.0.1";

    @Override
    public List<Email> getInboxData(User user) {

        List<Email> emails = new ArrayList<>();

        Properties propvals = new Properties();
        propvals.put("mail.pop3.host", SERVER_NAME);
        propvals.put("mail.pop3.port", "110");
        propvals.put("mail.pop3.starttls.enable", "true");
        Session emailSessionObj = Session.getDefaultInstance(propvals);

        try (Store storeObj = emailSessionObj.getStore("pop3")) {

            storeObj.connect(user.getEmail(), user.getPassword());

            Folder emailFolderObj = storeObj.getFolder("INBOX");
            emailFolderObj.open(Folder.READ_ONLY);

            Message[] messages = emailFolderObj.getMessages();

            for (int i = 0, n = messages.length; i < n; i++) {
                Email email = messageToEmail(messages[i], user, i);
                emails.add(email);
            }
            emailFolderObj.close(false);
        } catch (NoSuchProviderException exp) {
            LOGGER.error(exp);
        } catch (MessagingException exp) {
            LOGGER.error(exp);
        }
        return emails;
    }

    private File getAttachment(Message message, Email email) throws MessagingException, IOException {

        if (message.getContent() instanceof Multipart) {
            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (!("".equals(bodyPart.getFileName()) || bodyPart.getFileName() == null)
                        && !Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    return null;
                }
                InputStream is = bodyPart.getInputStream();
                String fileName = message.getHeader("fileName")[0];
                String[] fileNames = fileName.split("\\.", -1);
                final File tempFile = File.createTempFile(fileNames[0], "." + fileNames[1]);
                tempFile.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    IOUtils.copy(is, out);
                }
                return tempFile;
            }
        }

        return null;
    }

    @Override
    public Email getEmailWithId(long id, User user) {
        Email email = new Email();
        Properties propvals = new Properties();
        propvals.put("mail.pop3.host", SERVER_NAME);
        propvals.put("mail.pop3.port", "110");
        propvals.put("mail.pop3.starttls.enable", "true");
        Session emailSessionObj = Session.getDefaultInstance(propvals);

        try (Store storeObj = emailSessionObj.getStore("pop3")) {

            storeObj.connect(user.getEmail(), user.getPassword());

            Folder emailFolderObj = storeObj.getFolder("INBOX");
            emailFolderObj.open(Folder.READ_ONLY);

            Message[] messages = emailFolderObj.getMessages();

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                try {
                    if (message.getHeader("messageId")[0].equals(String.valueOf(id))) {
                        email = messageToEmail(message, user, i);
                    }
                } catch (MessagingException ex) {
                    java.util.logging.Logger.getLogger(Pop3Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            emailFolderObj.close(false);
            return email;
        } catch (NoSuchProviderException exp) {
            LOGGER.error(exp);
        } catch (MessagingException exp) {
            LOGGER.error(exp);
        }
        return email;
    }

    private Email messageToEmail(Message messageobj, User user, int i) {
        Email email = new Email();
        try {
            Message indvidualmsg = messageobj;
            email.setBody(indvidualmsg.getHeader("messageBody")[0]);
            email.setSender(indvidualmsg.getFrom()[0].toString());
            email.setSubject(indvidualmsg.getSubject());
            email.setReciever(user.getEmail());
            File f = getAttachment(indvidualmsg, email);
            email.setId(Long.parseLong(indvidualmsg.getHeader("messageId")[0]));
            email.setInboxCount(i + 1);
            if (f != null) {
                email.setAttachmentFileName(f.getName());
                email.setAttachmentFile(new FileInputStream(f));
            }
            if (indvidualmsg.getHeader("sendDate") != null && indvidualmsg.getHeader("sendDate")[0] != null) {
                try {
                    email.setRecieveDate(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(indvidualmsg.getHeader("sendDate")[0]));
                } catch (MessagingException | ParseException ex) {
                    LOGGER.error(ex);
                }
               
            }
            
            email.setId(indvidualmsg.getMessageNumber());
            if (indvidualmsg.getFlags().contains(Flags.Flag.DRAFT)) {
                email.setType(Enums.EmailType.DRAFT);
            } else if (indvidualmsg.getFlags().contains(Flags.Flag.SEEN)) {
                email.setType(Enums.EmailType.SEEN);
            } else if (indvidualmsg.getFlags().contains(Flags.Flag.DELETED)) {
                email.setType(Enums.EmailType.DELETED);
            } else if (indvidualmsg.getFlags().contains(Flags.Flag.RECENT)) {
                email.setType(Enums.EmailType.RECENT);
            }

        } catch (NoSuchProviderException exp) {
            LOGGER.error(exp);
        } catch (MessagingException | IOException exp) {
            LOGGER.error(exp);
        }
        return email;
    }

}
