package student_package;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.primefaces.PrimeFaces;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.faces.bean.ApplicationScoped;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The `CrudView` class is a managed bean in a JavaServer Faces (JSF)
 * application responsible for managing student data and interactions with the
 * user interface. It integrates with a database using a DataSource to perform
 * CRUD (Create, Read, Update, Delete) operations on student records. This class
 * also handles account creation for students and provides methods to manage
 * student data.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
@SuppressWarnings("serial")
@ManagedBean(name = "crudView")
@ApplicationScoped
public class CrudView implements Serializable {

	@Resource(lookup = "java:comp/env/jdbc/h2db")
	private DataSource dataSource;

	private boolean createAccountCheck;

	private List<Student> students;

	private Student selectedStudent;
	private Student currentStudent;

	private List<Student> selectedStudents;

	/**
	 * Sets the DataSource for the CRUD operations to be performed on the student
	 * records.
	 *
	 * @param dataSource The DataSource to be set.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Retrieves the current DataSource used for CRUD operations on student records.
	 *
	 * @return The current DataSource.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Retrieves the currently selected student.
	 *
	 * @return The currently selected student.
	 */
	public Student getCurrentStudent() {
		return currentStudent;
	}

	/**
	 * Sets the currently selected student.
	 *
	 * @param currentStudent The student to be set as currently selected.
	 */
	public void setCurrentStudent(Student currentStudent) {
		this.currentStudent = currentStudent;
	}

	/**
	 * Checks whether the "Create Account" checkbox is checked or not.
	 *
	 * @return true if the checkbox is checked, false otherwise.
	 */
	public boolean isCreateAccountCheck() {
		return createAccountCheck;
	}

	/**
	 * Sets the state of the "Create Account" checkbox.
	 *
	 * @param createAccountCheck The new state of the checkbox.
	 */
	public void setCreateAccountCheck(boolean createAccountCheck) {
		this.createAccountCheck = createAccountCheck;
	}

	/**
	 * Initializes the managed bean after construction. It performs the necessary
	 * setup, such as looking up the DataSource and initializing the student table.
	 */
	@PostConstruct
	public void init() {
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/h2db");
			checkInitTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.students = getStudentsTable();
	}

	/**
	 * Retrieves the list of students from the database.
	 *
	 * @return A list of Student objects representing the student records.
	 */
	public List<Student> getStudents() {
		return students;
	}

	/**
	 * Retrieves the list of selected student.
	 *
	 * @return A list of Student objects representing the selected students.
	 */
	public Student getSelectedStudent() {
		return selectedStudent;
	}

	/**
	 * Sets the list of selected students.
	 *
	 * @param selectedStudent A list of Student objects to be set as selected
	 *                         students.
	 */
	public void setSelectedStudent(Student selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	/**
	 * Retrieves the list of selected students.
	 *
	 * @return A list of Student objects representing the selected students.
	 */
	public List<Student> getSelectedStudents() {
		return selectedStudents;
	}

	/**
	 * Sets the list of selected students.
	 *
	 * @param selectedStudents A list of Student objects to be set as selected
	 *                         students.
	 */
	public void setSelectedStudents(List<Student> selectedStudents) {
		this.selectedStudents = selectedStudents;
	}

	/**
	 * Initializes a new student instance and sets its account creation status to
	 * "NotCreated." This function is used when opening a new student creation
	 * dialog.
	 */
	public void openNew() {
		this.selectedStudent = new Student();
		this.selectedStudent.setAccountCreation(AccountCreation.NotCreated);
	}

	/**
	 * Creates a student account for the current student if the account doesn't
	 * already exist. It also updates the student's information and triggers an AJAX
	 * update of the user interface.
	 */
	public void createStudentAccount() {
		if (currentStudent != null && currentStudent.getAccountCreation() == AccountCreation.NotCreated) {
			createAccountForStudent(currentStudent);
			PrimeFaces.current().ajax().update("form:dt-students");
			UpdateStudent(currentStudent);
		}
	}

	/**
	 * Creates an account for the given student if it doesn't already exist.
	 * Generates a random password for the student and sets the account creation
	 * status to "Created."
	 *
	 * @param student The student for whom the account is to be created.
	 */
	private void createAccountForStudent(Student student) {
		String randomPassword = generateRandomPassword();
		student.setPassword(randomPassword);
		student.setAccountCreation(AccountCreation.Created);
	}

	/**
	 * Saves or updates a student's information. If the student has no ID, a new
	 * student is added to the list. If the "Create Account" checkbox is checked, an
	 * account is created for the student. This function also triggers an AJAX
	 * update of the user interface and displays appropriate messages.
	 */
	public void saveStudent() {
		if (this.selectedStudent.getId() == null) {
			this.selectedStudent.setId("1ST" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
			this.students.add(this.selectedStudent);
			if (createAccountCheck) {
				createAccountForStudent(this.selectedStudent);
			}
			AddStudent(this.selectedStudent);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Added"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Updated"));
		}

		PrimeFaces.current().executeScript("PF('manageStudentDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
	}

	/**
	 * Deletes the selected student from the list of students. This function also
	 * triggers an AJAX update of the user interface and displays a message.
	 */
	public void deleteStudent() {
		this.students.remove(this.selectedStudent);
		this.selectedStudents.remove(this.selectedStudent);
		this.selectedStudent = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
	}

	/**
	 * Retrieves the message to be displayed on the "Delete" button. It varies based
	 * on the number of selected students.
	 *
	 * @return The appropriate message for the button.
	 */
	public String getDeleteButtonMessage() {
		if (hasSelectedStudents()) {
			int size = this.selectedStudents.size();
			return size > 1 ? size + " students selected" : "1 student selected";
		}

		return "Delete";
	}

	/**
	 * Checks if there are any selected students.
	 *
	 * @return true if there are selected students, false otherwise.
	 */
	public boolean hasSelectedStudents() {
		return this.selectedStudents != null && !this.selectedStudents.isEmpty();
	}

	/**
	 * Generates a random password for a student.
	 * 
	 * @return A random password string.
	 */
	private String generateRandomPassword() {
		// TODO : Generate Random Password
		return UUID.randomUUID().toString().substring(0, 8); // Exemple simple
	}

	/**
	 * Deletes the selected students from both the database and the list of
	 * students. This function also triggers an AJAX update of the user interface
	 * and displays a message.
	 */
	public void deleteSelectedStudents() {
		DeleteStudents(this.selectedStudents);
		this.students.removeAll(this.selectedStudents);
		this.selectedStudents = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Students Removed"));

		PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}

	/**
	 * Checks if the "Student" table exists in the database, and creates it if it
	 * doesn't exist.
	 */
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Student (id VARCHAR(255), code VARCHAR(255), firstName VARCHAR(255), lastName VARCHAR(255), password VARCHAR(255), accountCreation VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts a new student record into the database.
	 *
	 * @param selectedStudent The student to be added to the database.
	 */
	public void AddStudent(Student selectedStudent) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"INSERT INTO Student (id, code, firstName, lastName, password, accountCreation) VALUES (?, ?, ?, ?, ?, ?)")) {
			preparedStatement.setString(1, selectedStudent.getId());
			preparedStatement.setString(2, selectedStudent.getCode());
			preparedStatement.setString(3, selectedStudent.getFirstName());
			preparedStatement.setString(4, selectedStudent.getLastName());
			preparedStatement.setString(5, selectedStudent.getPassword());
			preparedStatement.setString(6, selectedStudent.getAccountCreation().getText());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes a list of students from the database.
	 *
	 * @param selectedStudents The list of students to be deleted from the database.
	 */
	public void DeleteStudents(List<Student> selectedStudents) {
		for (Student student : selectedStudents) {
			try (Connection connection = dataSource.getConnection();
					PreparedStatement preparedStatement = connection
							.prepareStatement("DELETE FROM Student WHERE id=?")) {
				preparedStatement.setString(1, student.getId());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates a student's information in the database.
	 *
	 * @param currentStudent The student with updated information to be stored in
	 *                       the database.
	 */
	public void UpdateStudent(Student currentStudent) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE Student SET password = ?, accountCreation = ? WHERE id = ?  ")) {
			preparedStatement.setString(1, currentStudent.getPassword());
			preparedStatement.setString(2, currentStudent.getAccountCreation().getText());
			preparedStatement.setString(3, currentStudent.getId());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a list of students from the database.
	 *
	 * @return A list of Student objects representing the student records from the
	 *         database.
	 */
	public List<Student> getStudentsTable() {
		List<Student> students = new ArrayList<Student>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Student")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Student student = new Student(resultSet.getString("id"), resultSet.getString("code"),
						resultSet.getString("firstName"), resultSet.getString("lastName"),
						resultSet.getString("password"), AccountCreation.NotCreated);
				if (resultSet.getString("accountCreation").equals("Created")) {
					student.setAccountCreation(AccountCreation.Created);
				} else {
					student.setAccountCreation(AccountCreation.NotCreated);
				}
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}
}
