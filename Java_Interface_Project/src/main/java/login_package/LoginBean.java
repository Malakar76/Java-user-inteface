package login_package;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

@ManagedBean
@ViewScoped
/**
 * The LoginBean class is a JSF ManagedBean that is responsible for managing user authentication.
 * The bean is ViewScoped, meaning it is bound to the view and will be recreated with each new
 * navigation to the page. It holds the username and password entered by the user and contains
 * the logic for validating those credentials.
 *
 * The class provides a method called 'login' that is invoked when the user attempts to log in.
 * This method checks if the entered username and password match predefined values ('admin').
 * If the credentials are correct, it returns a string that redirects the user to the home page.
 * If the credentials are incorrect, it adds an error message to the FacesContext and returns null,
 * which results in the user staying on the login page.
 * @author Kilyan Bentchakal
 * @version 1.0
 */
public class LoginBean {
    private String username;
    private String password;


    /**
     * Gets the username.
     *
     * @return A string representing the username.
     */
    
    public String getUsername() {
		return username;
	}
    
    /**
     * Sets the username.
     *
     * @param username A string containing the username.
     */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return A string representing the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password A string containing the password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Method to handle the login process.
	 *
	 * @return The navigation outcome (page to redirect to) based on login success or failure.
	 */	
	public String login() {
        if ("admin".equals(username) && "admin".equals(password)) {
            return "home?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Identifiants invalides", null));
            return null;
        }
    }
}