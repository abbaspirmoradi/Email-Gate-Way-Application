package ir.org.acm.dao;

import ir.org.acm.entity.User;
import java.util.List;
import javax.ejb.Local;

@Local
public interface UserDaoInterface {
    
     User register(User user);

     User find(User user);
    
     String getUserPassword(String username); 

    public List<User> getAllUsers();
}
