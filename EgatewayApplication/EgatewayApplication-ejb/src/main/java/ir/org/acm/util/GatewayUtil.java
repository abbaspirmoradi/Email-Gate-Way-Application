package ir.org.acm.util;

import ir.org.acm.entity.User;
import java.util.Properties;
import javax.faces.context.FacesContext;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class GatewayUtil {

    private final static String SERVER_NAME = "127.0.0.1";
    private final static Logger LOGGER = Logger.getLogger(GatewayUtil.class);
    
    public static HttpSession getSession() {
        return (HttpSession) FacesContext.
                getCurrentInstance().
                getExternalContext().
                getSession(false);
    }

    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.
                getCurrentInstance().
                getExternalContext().getRequest();
    }

    public static String getUserName() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session.getAttribute("username").toString();
    }

    public static String getUserId() {
        HttpSession session = getSession();
        if (session != null) {
            return (String) session.getAttribute("userid");
        } else {
            return null;
        }
    }

    public static Message[] getInboxMessages(User user) {
        Message[] messages;
        Properties propvals = new Properties();
        propvals.put("mail.pop3.host", SERVER_NAME);
        propvals.put("mail.pop3.port", "110");
        propvals.put("mail.pop3.starttls.enable", "true");
        Session emailSessionObj = Session.getDefaultInstance(propvals);

        try (Store storeObj = emailSessionObj.getStore("pop3")) {

            storeObj.connect(user.getEmail(), user.getPassword());

            Folder emailFolderObj = storeObj.getFolder("INBOX");
            emailFolderObj.open(Folder.READ_ONLY);

            messages = emailFolderObj.getMessages();
            emailFolderObj.close(false);
            return messages;
        } catch (NoSuchProviderException exp) {
            LOGGER.error(exp);
        } catch (MessagingException exp) {
            LOGGER.error(exp);
        }
        return new Message[0];
    }

}
