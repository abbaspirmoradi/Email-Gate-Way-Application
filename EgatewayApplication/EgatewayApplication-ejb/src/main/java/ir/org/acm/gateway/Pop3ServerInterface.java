
package ir.org.acm.gateway;

import ir.org.acm.entity.Email;
import ir.org.acm.entity.User;
import java.util.List;
import javax.ejb.Local;

@Local
public interface Pop3ServerInterface {
    
    List<Email> getInboxData(User user);
  
    public Email getEmailWithId(long id,User user);
}
