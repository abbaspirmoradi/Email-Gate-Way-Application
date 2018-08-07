package ir.org.acm.dao;

import ir.org.acm.entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UserDao implements UserDaoInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User register(User user) {
        return entityManager.merge(user);
    }

    @Override
    public User find(User user) {
        User userFind = entityManager.createQuery(
                "SELECT u from User u WHERE u.email =:username", User.class).
                setParameter("username", user.getEmail()).getSingleResult();
        if (userFind.getPassword().equals(user.getPassword())) {
            return userFind;
        } else {
            return null;
        }
    }

    @Override
    public String getUserPassword(String username) {
        User userFind = entityManager.createQuery(
                "SELECT u from User u WHERE u.email =:username", User.class).
                setParameter("username", username).getSingleResult();
        return userFind.getPassword();
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createNamedQuery("User.getAllUsers", User.class).getResultList();
    }

}
