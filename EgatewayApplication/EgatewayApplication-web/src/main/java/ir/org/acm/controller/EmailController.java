package ir.org.acm.controller;

import ir.org.acm.dao.EmailDaoInterface;
import ir.org.acm.dao.UserDaoInterface;
import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import ir.org.acm.gateway.Pop3ServerInterface;
import ir.org.acm.gateway.SmtpServerInterface;
import ir.org.acm.util.Enums;
import ir.org.acm.util.Enums.EmailType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

@ManagedBean(name = "emailController")
@SessionScoped
public class EmailController implements Serializable {

    @EJB
    public EmailDaoInterface emailDaoInterface;

    @EJB
    private SmtpServerInterface smtpServerInterface;

    @EJB
    private UserDaoInterface userDaoInterface;

    @EJB
    private Pop3ServerInterface pop3ServerInterface;

    private String subject;
    private String body;
    private String reciever;
    private String sender;
    private String recieveDate;
    private boolean viewed;
    private String attachmentFileName;
    private Part file;
    private long id;
    private File attachmentFile;
    private String messageType;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public File getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(File attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    public String sendEmail() {
        String value = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("form:emailbody");

        Email sentEmail = getEamil(Enums.EmailType.Sent, value);
        User user = getUser();
        File file = null;
        if (sentEmail.getAttachmentFile() != null) {
            try {
                file = stream2file(sentEmail);
            } catch (IOException ex) {
                Logger.getLogger(EmailController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        smtpServerInterface.sendmail(sentEmail, user, file);
        return "inbox";
    }

    public String viewEmail(Email email) throws IOException {
        User user0 = getUser();
        Email emailInbox = new Email();
        List<User> users = userDaoInterface.getAllUsers();
        outer:
        for (User user : users) {
            if (user0.getEmail().equals(user.getEmail())) {
                List<Email> pop3Emails = pop3ServerInterface.getInboxData(user);
                for (Email emaili : pop3Emails) {
                    if (emaili.getId() == email.getId()) {
                        emailInbox = emaili;
                        break outer;
                    }
                }
            }
        }
        subject = emailInbox.getSubject();
        body = emailInbox.getBody();
        reciever = emailInbox.getReciever();
        sender = emailInbox.getSender();
        recieveDate = emailInbox.getRecieveDate() != null ? email.getRecieveDate().toString() : null;
        attachmentFile = stream2file(emailInbox);
        attachmentFileName = emailInbox.getAttachmentFileName();
        emailDaoInterface.viewed(email.getId(), user0.getEmail());
        messageType = "Inbox";
        return "messageview?faces-redirect=true&includeViewParams=true";
    }

    public String viewSentEmail(Email email) throws IOException {
        String password = userDaoInterface.getUserPassword(email.getReciever());
        User user = new User();
        user.setEmail(email.getReciever());
        user.setPassword(password);
        Email emailInbox = new Email();
        List<Email> pop3Emails = pop3ServerInterface.getInboxData(user);
        for (Email emaili : pop3Emails) {
            if (emaili.getId() == email.getId()) {
                emailInbox = emaili;
            }
        }

        subject = emailInbox.getSubject();
        body = emailInbox.getBody();
        reciever = emailInbox.getReciever();
        sender = emailInbox.getSender();
        recieveDate = emailInbox.getRecieveDate() != null ? email.getRecieveDate().toString() : null;
        attachmentFile = stream2file(emailInbox);
        attachmentFileName = emailInbox.getAttachmentFileName();
        messageType = "Sent";
        return "messageview?faces-redirect=true&includeViewParams=true";
    }

    public void saveToDraftEmail() {
        Email sentEmail = getEamil(Enums.EmailType.DRAFT, null);
        User user = getUser();
        File file = null;
        if (sentEmail.getAttachmentFile() != null) {
            try {
                file = stream2file(sentEmail);
            } catch (IOException ex) {
                Logger.getLogger(EmailController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        smtpServerInterface.sendmail(sentEmail, user, file);
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getRecieveDate() {
        return recieveDate;
    }

    public void setRecieveDate(String recieveDate) {
        this.recieveDate = recieveDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private Email getEamil(EmailType type, String bodyFromHtml) {
        Email email = new Email();
        email.setBody(bodyFromHtml);
        email.setCreateDate(new Date());
        email.setReciever(getReciever());
        email.setSubject(subject);
        email.setType(type);
        if (file != null) {
            try {
                setAttachmentFileName(file.getSubmittedFileName());
                email.setAttachmentFile(file.getInputStream());
                email.setAttachmentFileName(getAttachmentFileName());
            } catch (IOException ex) {
                Logger.getLogger(EmailController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return email;
    }

    public User getUser() {
        User user = new User();
        user.setEmail(reciever);
        String userName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        user.setEmail(userName);
        String password = userDaoInterface.getUserPassword(userName);
        user.setPassword(password);
        return user;
    }

    public File stream2file(Email email) throws IOException {
        if (email.getAttachmentFileName() != null) {
            String[] fileName = email.getAttachmentFileName().split("\\.", -1);
            final File tempFile = File.createTempFile(fileName[0], "." + fileName[1]);
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(email.getAttachmentFile(), out);
            }
            return tempFile;
        }
        return null;
    }

    public void download() {

        File file = getAttachmentFile();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=" + getAttachmentFileName());
        response.setContentLength((int) file.length());
        ServletOutputStream out = null;
        try {
            FileInputStream input = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            int i = 0;
            while ((i = input.read(buffer)) != -1) {
                out.write(buffer);
                out.flush();
            }
            FacesContext.getCurrentInstance().getResponseComplete();
        } catch (IOException err) {
            err.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}
