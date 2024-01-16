package submission_package;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import project_package.Project;
import project_package.ProjectTeam;
import student_package.AccountCreation;
import student_package.Student;
import team_package.Team;

/**
 * The SubmissionView class is responsible for managing project submissions. It
 * interacts with the database to retrieve, initialize tables, and update
 * project information.
 *
 * This class uses JavaServer Faces (JSF) annotations and PrimeFaces for the
 * user interface. It includes methods to initialize the database tables,
 * retrieve projects, and update the project list.
 *
 * @author Robin Deplanques
 * @version 1.0
 */
@SuppressWarnings("serial")
@ManagedBean(name = "submissionView")
@ApplicationScoped
public class SubmissionView implements Serializable {

	@Resource(lookup = "java:comp/env/jdbc/h2db")
	private DataSource dataSource;

	private List<Project> projects;
	private Project selectedProject;

	/**
	 * Sets the data source for the database connection.
	 *
	 * @param dataSource The DataSource object for the database connection.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Retrieves the data source for the database connection.
	 *
	 * @return The DataSource object for the database connection.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Retrieves the list of projects from the database.
	 *
	 * @return A list of Project objects representing projects.
	 */
	public List<Project> getProjects() {
		return projects;
	}

	/**
	 * Sets the list of projects.
	 *
	 * @param projects The list of Project objects to set as projects.
	 */
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	/**
	 * Gets the currently selected project.
	 *
	 * @return The currently selected Project object.
	 */
	public Project getSelectedProject() {
		return selectedProject;
	}

	/**
	 * Sets the currently selected project.
	 *
	 * @param selectedProject The Project object to set as the currently selected
	 *                        project.
	 */
	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	/**
	 * Initializes the SubmissionView instance. It checks and creates necessary
	 * database tables during the post-construction phase of the JSF lifecycle.
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
		this.projects = getProjectsTable();

	}

	/**
	 * Checks and initializes the required database tables if they do not exist.
	 */
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Project (id VARCHAR(255), description VARCHAR(255), name VARCHAR(255), type VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Team (id VARCHAR(255), teammate TEXT, submit TEXT)")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the list of projects from the database, including team and
	 * submission information.
	 *
	 * @return A list of Project objects with associated team details and submission
	 *         information.
	 */
	public List<Project> getProjectsTable() {
		List<Project> projects = new ArrayList<Project>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Project")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Project project = new Project(resultSet.getString("id"), resultSet.getString("description"),
						resultSet.getString("name"), resultSet.getString("type"), new ArrayList<ProjectTeam>(),
						new ArrayList<String>());
				try (PreparedStatement preparedStatement2 = connection
						.prepareStatement("SELECT * FROM Team WHERE id =?")) {
					preparedStatement2.setString(1, resultSet.getString("id"));
					ResultSet resultSet2 = preparedStatement2.executeQuery();
					if (resultSet2.next()) {
						String[] parts = resultSet2.getString("teammate").split(":");
						String[] partsSubmit = resultSet2.getString("submit").split(":");

						if (project.getType().equals("Individual")) {
							for (int i = 0; i < parts.length; i++) {
								try (PreparedStatement preparedStatement3 = connection
										.prepareStatement("SELECT * FROM Student WHERE id = ?")) {
									preparedStatement3.setString(1, parts[i]);
									ResultSet resultSet3 = preparedStatement3.executeQuery();
									if (resultSet3.next()) {
										Student student = new Student(resultSet3.getString("id"),
												resultSet3.getString("code"), resultSet3.getString("firstName"),
												resultSet3.getString("lastName"), resultSet3.getString("password"),
												AccountCreation.NotCreated);
										if (resultSet3.getString("accountCreation") == "Created") {
											student.setAccountCreation(AccountCreation.Created);
										} else {
											student.setAccountCreation(AccountCreation.NotCreated);
										}
										project.addTeam(student);
										project.addSubmit(partsSubmit[i]);
									}

								}

							}
						} else {
							for (int i = 0; i < parts.length; i++) {
								try (PreparedStatement preparedStatement3 = connection
										.prepareStatement("SELECT * FROM Groupe WHERE id = ?")) {
									preparedStatement3.setString(1, parts[i]);
									ResultSet resultSet3 = preparedStatement3.executeQuery();
									if (resultSet3.next()) {
										Team team = new Team(resultSet3.getString("id"), resultSet3.getString("name"),
												new ArrayList<Student>());
										project.addTeam(team);
										project.addSubmit(partsSubmit[i]);
									}

								}

							}
						}
					}

				}
				projects.add(project);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return projects;
	}

	/**
	 * Updates the list of projects by refreshing the data from the database.
	 */
	public void updateProjectList() {
		this.projects = getProjectsTable();

	}
}
