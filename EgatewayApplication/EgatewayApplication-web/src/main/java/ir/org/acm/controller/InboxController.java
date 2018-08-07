package ir.org.acm.controller;

import ir.org.acm.dao.EmailDaoInterface;
import ir.org.acm.dao.UserDaoInterface;
import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import ir.org.acm.gateway.Pop3ServerInterface;
import ir.org.acm.util.EmailComparer;
import ir.org.acm.util.Enums;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class InboxController {

    @PostConstruct
    public void init() {
        loadInboxData();
        loadSentData();
    }

    private List<Email> inboxEmails;
    private List<Email> draftEmails;
    private List<Email> sentEmails;
    private int inboxCount;
    private int draftCount;
    private int sentCount;
    private int importantCount;
    private int spamCount;
    private int trashCount;

    public List<Email> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<Email> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public List<Email> getDraftEmails() {
        return draftEmails;
    }

    public void setDraftEmails(List<Email> draftEmails) {
        this.draftEmails = draftEmails;
    }

    public int getTrashCount() {
        return trashCount;
    }

    public void setTrashCount(int trashCount) {
        this.trashCount = trashCount;
    }

    public int getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(int draftCount) {
        this.draftCount = draftCount;
    }

    public List<Email> getInboxEmails() {
        return inboxEmails;
    }

    public void setInboxEmails(List<Email> inboxEmails) {
        this.inboxEmails = inboxEmails;
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

    @EJB
    private Pop3ServerInterface pop3ServerInterface;
    @EJB
    private UserDaoInterface userDaoInterface;

    @EJB
    public EmailDaoInterface emailDaoInterface;

    private void loadInboxData() {
        User user = new User();
        String userName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        user.setEmail(userName);
        String password = userDaoInterface.getUserPassword(userName);
        user.setPassword(password);
        List<Email> emails = pop3ServerInterface.getInboxData(user);
        for (Email email : emails) {
            if (emailDaoInterface.getisViewed(email.getId(),userName)) {
                email.setViewed(true);
            } else {
                email.setViewed(false);
            }
        }

        Collections.sort(emails, new EmailComparer());
        setInboxEmails(emails);

        for (Email email : inboxEmails) {
            if (email.getType() == Enums.EmailType.DRAFT) {
                draftCount++;
            }
        }
        for (Email email : inboxEmails) {
            if (email.getType() == Enums.EmailType.DELETED) {
                trashCount++;
            }
        }
        setInboxCount(inboxEmails.size() - draftCount - trashCount);
    }

    public void loadSentData() {
        List<User> users = userDaoInterface.getAllUsers();
        List<Email> pop3Emails = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        String userName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        for (User user : users) {
            pop3Emails = pop3ServerInterface.getInboxData(user);
            for (Email email : pop3Emails) {
                if (email.getSender().equals(userName)) {
                    email.setType(Enums.EmailType.Sent);
                    emails.add(email);
                }
            }
        }
        Collections.sort(emails, new EmailComparer());
        for (Email email : emails) {
            email.setViewed(true);
        }

        setSentEmails(emails);
        setSentCount(sentEmails.size());
    }

}
