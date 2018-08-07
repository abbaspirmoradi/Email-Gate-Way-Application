package ir.org.acm.controller;

import ir.org.acm.dao.UserDaoInterface;
import ir.org.acm.entity.User;
import ir.org.acm.gateway.Pop3Server;
import ir.org.acm.util.GatewayUtil;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean(name = "userController")
@SessionScoped
public class UserController implements Serializable{

    @EJB
    private UserDaoInterface userDaoInterface;
    private String firstName;
    private String lastName;
    private String password;
    private String loginedUser;

    public String getLoginedUser() {
        return loginedUser;
    }

    public void setLoginedUser(String loginedUser) {
        this.loginedUser = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    private String email;

    public String addNewUser() {
        try {
            User user = getUser();
            User register = userDaoInterface.register(user);
            return "success";
        } catch (Exception e) {
            System.out.println(e);
            return "unsuccess";
        }
    }

    private User getUser() {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        return user;
    }

    public String login() {
        User user = getUser();
        User loginedUser = userDaoInterface.find(user);
        if (loginedUser != null) {
            // get Http Session and store username
            HttpSession session = GatewayUtil.getSession();
            session.setAttribute("username", loginedUser.getEmail());
            return "inbox";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Invalid Login!",
                            "Please Try Again!"));
            return "login";
        }
    }

    public String logout() {
        HttpSession session = GatewayUtil.getSession();
        session.invalidate();
        return "login";
    }
}
