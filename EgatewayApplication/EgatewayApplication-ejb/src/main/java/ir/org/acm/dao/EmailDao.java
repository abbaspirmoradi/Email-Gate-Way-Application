
package ir.org.acm.dao;

import ir.org.acm.entity.Email;
import ir.org.acm.entity.EmailView;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class EmailDao implements EmailDaoInterface {

    @PersistenceContext
    EntityManager em;

    @Override
    public Email save(Email email) {

        Email e = em.merge(email);
        return e;
    }

    @Override
    public boolean getisViewed(long id, String userName) {
       List< EmailView> emails = em.createQuery(
                "SELECT e from EmailView e WHERE e.emailNo=:num AND e.userName=:userN", EmailView.class).
                setParameter("num", id).setParameter("userN", userName).getResultList();
        if (emails.size() != 0) {
            return emails.get(0).isViewed();
        } else {
            return false;
        }
    }

    @Override
    public void viewed(long id,String userName) {
        EmailView emailView = new EmailView();
        emailView.setUserName(userName);
        emailView.setEmailNo(id);
        emailView.setViewed(true);
        em.merge(emailView);
    }
}
