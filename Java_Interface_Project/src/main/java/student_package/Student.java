package student_package;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Student implements Serializable {

    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String password;
    private AccountCreation accountCreation;

    public Student() {
    }

    public Student(String id, String code, String firstName, String lastName,  String password, AccountCreation accountCreation) {
        this.id = id;
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.accountCreation = accountCreation;
    }

    @Override
    public Student clone() {
        return new Student(getId(), getCode(), getFirstName(), getLastName(), getPassword(), getAccountCreation());
    }

    // Getters and Setters
 
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public AccountCreation getAccountCreation() {
        return accountCreation;
    }

    public void setAccountCreation(AccountCreation accountCreation) {
        this.accountCreation = accountCreation;
    }

    // hashCode and equals based on 'id

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }
}