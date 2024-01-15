package student_package;

import java.io.Serializable;

import project_package.ProjectTeam;

/**
 * The `Student` class represents a student entity with various attributes such
 * as a unique ID, code, first name, last name, password, and account creation
 * status. This class implements the Serializable interface and is part of the
 * ProjectTeam interface. It provides methods for creating, accessing, and
 * manipulating student information, as well as defining methods for equality
 * comparison, hash code generation, and string representation.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Student implements Serializable, ProjectTeam {

	private String id;
	private String code;
	private String firstName;
	private String lastName;
	private String password;
	private AccountCreation accountCreation;

	/**
	 * Default constructor for creating an instance of Student.
	 */
	public Student() {
	}

	/**
	 * Constructs a Student with specified details.
	 *
	 * @param id              The unique identifier for the student.
	 * @param code            The code associated with the student.
	 * @param firstName       The student's first name.
	 * @param lastName        The student's last name.
	 * @param password        The student's password.
	 * @param accountCreation The account creation status.
	 */
	public Student(String id, String code, String firstName, String lastName, String password,
			AccountCreation accountCreation) {
		this.id = id;
		this.code = code;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.accountCreation = accountCreation;
	}

	/**
	 * Creates and returns a copy of this student object.
	 *
	 * @return A clone of this instance.
	 */
	@Override
	public Student clone() {
		return new Student(getId(), getCode(), getFirstName(), getLastName(), getPassword(), getAccountCreation());
	}

	/**
	 * Retrieves the password of the student.
	 *
	 * @return The password as a String.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password for the student.
	 *
	 * @param password The new password String to be set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Retrieves the unique ID of the student.
	 *
	 * @return The unique ID as a String.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique ID for the student.
	 *
	 * @param id The new unique ID String to be set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves the code associated with the student.
	 *
	 * @return The code as a String.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code for the student.
	 *
	 * @param code The new code String to be set.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Retrieves the first name of the student.
	 *
	 * @return The first name as a String.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name for the student.
	 *
	 * @param firstName The new first name String to be set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Retrieves the last name of the student.
	 *
	 * @return The last name as a String.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name for the student.
	 *
	 * @param lastName The new last name String to be set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Retrieves the account creation status of the student.
	 *
	 * @return The account creation status as an instance of AccountCreation enum.
	 */
	public AccountCreation getAccountCreation() {
		return accountCreation;
	}

	/**
	 * Sets the account creation status for the student.
	 *
	 * @param accountCreation The new AccountCreation enum to be set.
	 */
	public void setAccountCreation(AccountCreation accountCreation) {
		this.accountCreation = accountCreation;
	}

	/**
	 * Generates a hash code for the student. The hash code is generated based on
	 * the student's unique ID.
	 *
	 * @return A hash code value for the object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Compares this Student object with another object to determine if they are
	 * equal.
	 *
	 * @param obj The object to compare with this Student object.
	 * @return true if the objects are equal, false otherwise.
	 */
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

	/**
	 * Returns the full name of the student as a string.
	 *
	 * @return The full name of the student.
	 */
	public String displayName() {
		return (this.firstName + " " + this.lastName);
	}

	/**
	 * Returns a string representation of the Student object.
	 *
	 * @return A string containing the student's information.
	 */
	public String toString() {
		return (id + ":" + code + ":" + firstName + ":" + lastName + ":" + password + ":" + accountCreation);
	}

	/**
	 * Creates a new Student object by parsing a string in the specified format.
	 *
	 * @param value The string to parse.
	 * @return A new Student object created from the string.
	 */
	public static Student fromString(String value) {
		String[] parts = value.split(":");
		Student team = new Student();
		((Student) team).setId(parts[0]);
		((Student) team).setCode(parts[1]);
		((Student) team).setFirstName(parts[2]);
		((Student) team).setLastName(parts[3]);
		if (parts[5].equals("Created")) {
			((Student) team).setPassword(parts[4]);
			((Student) team).setAccountCreation(AccountCreation.Created);
		} else {
			((Student) team).setAccountCreation(AccountCreation.NotCreated);
		}

		return team;
	}
}