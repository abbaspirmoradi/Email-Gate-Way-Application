
package ir.org.acm.dao;

import ir.org.acm.entity.Email;
import javax.ejb.Local;

@Local
public interface EmailDaoInterface {
    
     Email save(Email email);    

    public boolean getisViewed(long id, String userName);

    public void viewed(long id, String userName);
}
