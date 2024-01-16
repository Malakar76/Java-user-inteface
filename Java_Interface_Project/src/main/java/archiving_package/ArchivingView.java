package archiving_package;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.PrimeFaces;

import project_package.Project;
import project_package.ProjectTeam;
import student_package.AccountCreation;
import student_package.Student;
import team_package.Team;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * The ArchivingView class is responsible for managing archived projects. It
 * interacts with the database to retrieve, delete, and update archived
 * projects.
 *
 * This class uses JavaServer Faces (JSF) annotations and PrimeFaces for the
 * user interface. It includes methods to initialize the database tables, delete
 * selected archives, and update the list of archived projects.
 *
 * @author Robin Deplanques
 * @version 1.0
 */
@SuppressWarnings("serial")
@ManagedBean(name = "archivingView")
@ApplicationScoped
public class ArchivingView implements Serializable {

	private List<Project> archives;
	private Project selectedArchive;
	private Project currentArchive;

	@Resource(lookup = "java:comp/env/jdbc/h2db")
	private DataSource dataSource;

	private List<Project> selectedArchives;

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
	 * Gets the currently selected archive.
	 *
	 * @return The currently selected Project object.
	 */
	public Project getCurrentArchive() {
		return currentArchive;
	}

	/**
	 * Sets the currently selected archive.
	 *
	 * @param currentArchive The Project object to set as the currently selected
	 *                       archive.
	 */
	public void setCurrentArchive(Project currentArchive) {
		this.currentArchive = currentArchive;
	}

	/**
	 * Initializes the ArchivingView instance. It checks and creates necessary
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
		this.archives = getArchivesTable();
	}

	/**
	 * Retrieves the list of archived projects from the database.
	 *
	 * @return A list of Project objects representing archived projects.
	 */
	public List<Project> getArchives() {
		return archives;
	}

	/**
	 * Sets the list of archived projects.
	 *
	 * @param archives The list of Project objects to set as archived projects.
	 */
	public void setArchives(List<Project> archives) {
		this.archives = archives;
	}

	/**
	 * Gets the currently selected archive.
	 *
	 * @return The currently selected Project object.
	 */
	public Project getSelectedArchive() {
		return selectedArchive;
	}

	/**
	 * Sets the currently selected archive.
	 *
	 * @param selectedArchive The Project object to set as the currently selected
	 *                        archive.
	 */
	public void setSelectedArchive(Project selectedArchive) {
		this.selectedArchive = selectedArchive;
	}

	/**
	 * Gets the list of selected archives.
	 *
	 * @return The list of selected Project objects.
	 */
	public List<Project> getSelectedArchives() {
		return selectedArchives;
	}

	/**
	 * Sets the list of selected archives.
	 *
	 * @param selectedArchives The list of Project objects to set as selected
	 *                         archives.
	 */
	public void setSelectedArchives(List<Project> selectedArchives) {
		this.selectedArchives = selectedArchives;
	}

	/**
	 * Gets the message for the archive button based on the number of selected
	 * archives.
	 *
	 * @return The message for the archive button.
	 */
	public String getArchiveButtonMessage() {
		if (hasSelectedArchives()) {
			int size = this.selectedArchives.size();
			return size > 1 ? size + " archives selected" : "1 archive selected";
		}

		return "Delete";
	}

	/**
	 * Checks if there are selected archives.
	 *
	 * @return True if there are selected archives, false otherwise.
	 */
	public boolean hasSelectedArchives() {
		return this.selectedArchives != null && !this.selectedArchives.isEmpty();
	}

	/**
	 * Deletes the selected archive from the database and updates the archived
	 * projects list.
	 */
	public void deleteSelectedArchive() {
		DeleteArchive(this.selectedArchive);
		this.archives.remove(this.selectedArchive);
		this.selectedArchive = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archive Removed"));
		PrimeFaces.current().executeScript("PF('archiveDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-archive");
	}

	/**
	 * Checks and initializes the required database tables if they do not exist.
	 */
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Archive (id VARCHAR(255), description VARCHAR(255), name VARCHAR(255), type VARCHAR(255))")) {
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
	 * Deletes the specified project and its associated team information from the
	 * database.
	 *
	 * @param selectedArchive The Project object to be deleted.
	 */
	public void DeleteArchive(Project selectedArchive) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Archive WHERE id=?")) {
			preparedStatement.setString(1, selectedArchive.getId());
			preparedStatement.executeUpdate();
			PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM Team WHERE id = ?");
			preparedStatement2.setString(1, selectedArchive.getId());
			preparedStatement2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the list of archived projects from the database, including team
	 * information.
	 *
	 * @return A list of Project objects with associated team details.
	 */
	public List<Project> getArchivesTable() {
		List<Project> archives = new ArrayList<Project>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Archive")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Project archive = new Project(resultSet.getString("id"), resultSet.getString("description"),
						resultSet.getString("name"), resultSet.getString("type"), new ArrayList<ProjectTeam>(),
						new ArrayList<String>());
				try (PreparedStatement preparedStatement2 = connection
						.prepareStatement("SELECT * FROM Team WHERE id =?")) {
					preparedStatement2.setString(1, resultSet.getString("id"));
					ResultSet resultSet2 = preparedStatement2.executeQuery();
					if (resultSet2.next()) {
						String[] parts = resultSet2.getString("teammate").split(":");
						if (archive.getType().equals("Individual")) {
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
										archive.addTeam(student);
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
										archive.addTeam(team);
									}

								}
							}

						}
					}

				}
				archives.add(archive);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return archives;
	}

	/**
	 * Updates the list of archived projects by refreshing the data from the
	 * database.
	 */
	public void updateArchiveList() {
		this.archives = getArchivesTable();

	}

}
