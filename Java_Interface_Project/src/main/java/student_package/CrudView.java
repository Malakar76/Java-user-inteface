package student_package;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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

	@ManagedProperty(value = "#{studentService}")
	private StudentService studentService;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Student getCurrentStudent() {
		return currentStudent;
	}

	public void setCurrentStudent(Student currentStudent) {
		this.currentStudent = currentStudent;
	}

	public boolean isCreateAccountCheck() {
		return createAccountCheck;
	}

	public void setCreateAccountCheck(boolean createAccountCheck) {
		this.createAccountCheck = createAccountCheck;
	}

	public StudentService getStudentService() {
		return studentService;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}

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

	public List<Student> getStudents() {
		return students;
	}

	public Student getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(Student selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public List<Student> getSelectedStudents() {
		return selectedStudents;
	}

	public void setSelectedStudents(List<Student> selectedStudents) {
		this.selectedStudents = selectedStudents;
	}

	public void openNew() {
		this.selectedStudent = new Student();
		this.selectedStudent.setAccountCreation(AccountCreation.NotCreated);
	}

	public void createStudentAccount() {
		if (currentStudent != null && currentStudent.getAccountCreation() == AccountCreation.NotCreated) {
			createAccountForStudent(currentStudent);
			PrimeFaces.current().ajax().update("form:dt-students");
			UpdateStudent(currentStudent);
		}
	}

	private void createAccountForStudent(Student student) {
		String randomPassword = generateRandomPassword();
		student.setPassword(randomPassword);
		student.setAccountCreation(AccountCreation.Created);
	}

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

	public void deleteStudent() {
		this.students.remove(this.selectedStudent);
		this.selectedStudents.remove(this.selectedStudent);
		this.selectedStudent = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
	}

	public String getDeleteButtonMessage() {
		if (hasSelectedStudents()) {
			int size = this.selectedStudents.size();
			return size > 1 ? size + " students selected" : "1 student selected";
		}

		return "Delete";
	}

	public boolean hasSelectedStudents() {
		return this.selectedStudents != null && !this.selectedStudents.isEmpty();
	}

	private String generateRandomPassword() {
		// TODO : Generate Random Password
		return UUID.randomUUID().toString().substring(0, 8); // Exemple simple
	}

	public void deleteSelectedStudents() {
		DeleteStudents(this.selectedStudents);
		this.students.removeAll(this.selectedStudents);
		this.selectedStudents = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Students Removed"));
		
		PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}

	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Student (id VARCHAR(255), code VARCHAR(255), firstName VARCHAR(255), lastName VARCHAR(255), password VARCHAR(255), accountCreation VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
	
	public void DeleteStudents(List<Student> selectedStudents) {
		for (Student student : selectedStudents) {
			try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Student WHERE id=?")) {
				preparedStatement.setString(1, student.getId());
				preparedStatement.executeUpdate();
			} 	catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void UpdateStudent(Student currentStudent) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"UPDATE Student SET password = ?, accountCreation = ? WHERE id = ?  ")) {
			preparedStatement.setString(1, currentStudent.getPassword());
			preparedStatement.setString(2, currentStudent.getAccountCreation().getText());
			preparedStatement.setString(3, currentStudent.getId());
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Student> getStudentsTable() {
		List<Student> students = new ArrayList<Student>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Student")) {
				ResultSet resultSet = preparedStatement.executeQuery();
				 while (resultSet.next()) {
	                  Student student = new Student(resultSet.getString("id"),resultSet.getString("code"),resultSet.getString("firstName"),resultSet.getString("lastName"),resultSet.getString("password"),AccountCreation.NotCreated);
	                  if (resultSet.getString("accountCreation").equals("Created")) {
	                	 student.setAccountCreation(AccountCreation.Created);
	                   }else {
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
