package ir.org.acm.gateway;

import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import java.io.File;
import javax.ejb.Local;

@Local
public interface SmtpServerInterface {

    boolean sendmail(Email email, User user, File file);
}
