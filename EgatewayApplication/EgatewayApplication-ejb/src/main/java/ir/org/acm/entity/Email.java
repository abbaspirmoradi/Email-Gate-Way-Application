
package ir.org.acm.entity;

import ir.org.acm.util.Enums;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public class Email implements Serializable {

    private long id;
    private String sender;
    private String subject;
    private String body;
    private String reciever;
    private Enums.EmailType type;
    private int inboxCount;
    private int sentCount;
    private int importantCount;
    private int spamCount;
    private String attachmentFileName;
    private InputStream attachmentFile;

    private Date createDate;

    private Date recieveDate;

    private boolean viewed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public Enums.EmailType getType() {
        return type;
    }

    public void setType(Enums.EmailType type) {
        this.type = type;
    }

    public int getInboxCount() {
        return inboxCount;
    }

    public void setInboxCount(int inboxCount) {
        this.inboxCount = inboxCount;
    }

    public int getSentCount() {
        return sentCount;
    }

    public void setSentCount(int sentCount) {
        this.sentCount = sentCount;
    }

    public int getImportantCount() {
        return importantCount;
    }

    public void setImportantCount(int importantCount) {
        this.importantCount = importantCount;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public void setSpamCount(int spamCount) {
        this.spamCount = spamCount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getRecieveDate() {
        return recieveDate;
    }

    public void setRecieveDate(Date recieveDate) {
        this.recieveDate = recieveDate;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public InputStream getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(InputStream attachmentFile) {
        this.attachmentFile = attachmentFile;
    }
}
