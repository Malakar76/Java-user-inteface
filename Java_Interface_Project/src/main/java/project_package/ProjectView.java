package project_package;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

import student_package.AccountCreation;
import student_package.Student;
import team_package.Team;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ApplicationScoped;
import java.sql.ResultSet;

/**
 * The ProjectView class is responsible for managing projects, including their
 * creation, modification, and archiving. It interacts with the database to
 * retrieve, add, update, and archive project information.
 *
 * This class uses JavaServer Faces (JSF) annotations and PrimeFaces for the
 * user interface. It includes methods to initialize the database tables,
 * retrieve projects, add, update, and archive projects, and manage project
 * teams.
 *
 * @author Robin Deplanques
 * @version 1.0
 */
@SuppressWarnings("serial")
@ManagedBean(name = "projectView")
@ApplicationScoped
public class ProjectView implements Serializable {

	private List<Project> projects;
	private DualListModel<ProjectTeam> projectTeams;
	private Project selectedProject;
	private Project currentProject;

	private List<Project> selectedProjects;

	@Resource(lookup = "java:comp/env/jdbc/h2db")
	private DataSource dataSource;

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
	 * Gets the currently selected project.
	 *
	 * @return The currently selected Project object.
	 */
	public Project getCurrentProject() {
		return currentProject;
	}

	/**
	 * Sets the currently selected project.
	 *
	 * @param currentProject The Project object to set as the currently selected
	 *                       project.
	 */
	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	/**
	 * Initializes the ProjectView instance. It checks and creates necessary
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
		List<ProjectTeam> teamsSource = new ArrayList<>();
		List<ProjectTeam> teamsTarget = new ArrayList<>();

		projectTeams = new DualListModel<ProjectTeam>(teamsSource, teamsTarget);

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
	 * Gets the list of selected projects.
	 *
	 * @return A list of selected Project objects.
	 */
	public List<Project> getSelectedProjects() {
		return selectedProjects;
	}

	/**
	 * Sets the list of selected projects.
	 *
	 * @param selectedProjects The list of Project objects to set as selected
	 *                         projects.
	 */
	public void setSelectedProjects(List<Project> selectedProjects) {
		this.selectedProjects = selectedProjects;
	}

	/**
	 * Opens a new project for editing.
	 */
	public void openNew() {
		this.selectedProject = new Project();
	}

	/**
	 * Saves the currently selected project. If the project is new, it is added to
	 * the list of projects.
	 */
	public void saveProject() {
		if (this.selectedProject.getId() == null) {
			this.selectedProject.setId("ST" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
			this.projects.add(this.selectedProject);
			AddProject(selectedProject);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Added"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Updated"));
		}

		PrimeFaces.current().executeScript("PF('manageProjectDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");

	}

	/**
	 * Saves the selected teams for a project.
	 */
	public void saveTeams() {
		UpdateProject(selectedProjects.get(0));
		this.selectedProjects.get(0).setProjectTeams(this.projectTeams.getTarget());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Teams Saved"));
		PrimeFaces.current().ajax().update("form:messages");
		PrimeFaces.current().executeScript("PF('dtProjects').clearFilters()");
		PrimeFaces.current().executeScript("PF('manageProjectTeams').hide()");

	}

	/**
	 * Gets the button message for deleting projects based on the number of selected
	 * projects.
	 *
	 * @return The button message indicating the number of selected projects.
	 */
	public String getDeleteButtonMessage() {
		if (hasSelectedProjects()) {
			int size = this.selectedProjects.size();
			return size > 1 ? size + " projects selected" : "1 project selected";
		}

		return "Delete";
	}

	/**
	 * Checks if there are any selected projects.
	 *
	 * @return True if there are selected projects, otherwise false.
	 */
	public boolean hasSelectedProjects() {
		return this.selectedProjects != null && !this.selectedProjects.isEmpty();
	}

	/**
	 * Checks if there is only one selected project.
	 *
	 * @return True if there is only one selected project, otherwise false.
	 */
	public boolean hasOneSelectedProject() {
		return this.selectedProjects != null && (this.selectedProjects.size() == 1);
	}

	/**
	 * Deletes the selected projects from the database and removes them from the
	 * list of projects.
	 */
	public void deleteSelectedProjects() {
		DeleteProjects(this.selectedProjects);
		this.projects.removeAll(this.selectedProjects);
		this.selectedProjects = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}

	/**
	 * Archives the selected projects by moving them to the Archive table in the
	 * database.
	 */
	public void archiveSelectedProjects() {
		AddArchivesProjects(this.selectedProjects);
		this.projects.removeAll(this.selectedProjects);
		this.selectedProjects = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Archived"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");

	}

	/**
	 * Gets the dual list model for managing project teams.
	 *
	 * @return The DualListModel containing source and target project teams.
	 */
	public DualListModel<ProjectTeam> getProjectTeams() {
		return projectTeams;
	}

	/**
	 * Sets the dual list model for managing project teams.
	 *
	 * @param projectTeams The DualListModel to set.
	 */
	public void setProjectTeams(DualListModel<ProjectTeam> projectTeams) {
		this.projectTeams = projectTeams;
	}

	/**
	 * Edits the teams for the selected project. It populates the source and target
	 * lists based on the project type (Individual or Group).
	 */
	public void editTeams() {
		if (this.selectedProjects.get(0).getType().equals("Individual")) {
			this.projectTeams.setSource(availableStudents(this.selectedProjects.get(0)));
			this.projectTeams.setTarget(this.selectedProjects.get(0).getProjectTeams());
		} else {
			this.projectTeams.setSource(availableTeams(this.selectedProjects.get(0)));
			this.projectTeams.setTarget(this.selectedProjects.get(0).getProjectTeams());

		}
	}

	/**
	 * Retrieves the list of students from the database.
	 *
	 * @return A list of Student objects representing students.
	 */
	public List<ProjectTeam> getStudents() {
		List<ProjectTeam> students = new ArrayList<ProjectTeam>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Student")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Student student = new Student(resultSet.getString("id"), resultSet.getString("code"),
						resultSet.getString("firstName"), resultSet.getString("lastName"),
						resultSet.getString("password"), AccountCreation.NotCreated);
				if (resultSet.getString("accountCreation") == "Created") {
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

	/**
	 * Retrieves the list of teams from the database.
	 *
	 * @return A list of Team objects representing teams.
	 */
	public List<ProjectTeam> getTeams() {
		List<ProjectTeam> teams = new ArrayList<ProjectTeam>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Groupe")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Team team = new Team(resultSet.getString("id"), resultSet.getString("name"), new ArrayList<Student>());

				teams.add(team);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teams;
	}

	/**
	 * Retrieves the list of available students for a project (not already in the
	 * project team).
	 *
	 * @param project The project for which available students are needed.
	 * @return A list of ProjectTeam objects representing available students.
	 */
	public List<ProjectTeam> availableStudents(Project project) {
		List<ProjectTeam> available = new ArrayList<ProjectTeam>(getStudents());
		available.removeAll(project.getProjectTeams());
		return available;
	}

	/**
	 * Retrieves the list of available teams for a project (not already in the
	 * project team).
	 *
	 * @param project The project for which available teams are needed.
	 * @return A list of ProjectTeam objects representing available teams.
	 */
	public List<ProjectTeam> availableTeams(Project project) {
		List<ProjectTeam> available = new ArrayList<ProjectTeam>(getTeams());
		available.removeAll(project.getProjectTeams());
		return available;
	}

	/**
	 * Checks and initializes the necessary database tables during application
	 * startup.
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
	 * Adds a new project to the database.
	 *
	 * @param selectedProject The Project object to add to the database.
	 */
	public void AddProject(Project selectedProject) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO Project (id, description, name, type) VALUES (?, ?, ?, ?)")) {
			preparedStatement.setString(1, selectedProject.getId());
			preparedStatement.setString(2, selectedProject.getDescription());
			preparedStatement.setString(3, selectedProject.getName());
			preparedStatement.setString(4, selectedProject.getType());
			preparedStatement.executeUpdate();
			try (PreparedStatement preparedStatement2 = connection
					.prepareStatement("INSERT INTO Team (id, teammate, submit) VALUES (?, '', '')")) {
				preparedStatement2.setString(1, selectedProject.getId());
				preparedStatement2.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes selected projects from the database.
	 *
	 * @param selectedProjects The list of Project objects to delete from the
	 *                         database.
	 */
	public void DeleteProjects(List<Project> selectedProjects) {
		for (Project project : selectedProjects) {
			try (Connection connection = dataSource.getConnection();
					PreparedStatement preparedStatement = connection
							.prepareStatement("DELETE FROM Project WHERE id=?")) {
				preparedStatement.setString(1, project.getId());
				preparedStatement.executeUpdate();
				PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM Team WHERE id = ?");
				preparedStatement2.setString(1, project.getId());
				preparedStatement2.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates a project in the database.
	 *
	 * @param selectedProject The Project object to update in the database.
	 */
	public void UpdateProject(Project selectedProject) {
		List<ProjectTeam> toAdd = new ArrayList<ProjectTeam>(this.projectTeams.getTarget());
		toAdd.removeAll(selectedProject.getProjectTeams());
		List<ProjectTeam> toRemove = new ArrayList<ProjectTeam>(selectedProject.getProjectTeams());
		toRemove.retainAll(this.projectTeams.getSource());
		List<ProjectTeam> toAddRemove = new ArrayList<ProjectTeam>(selectedProject.getProjectTeams());
		toAddRemove.removeAll(toRemove);
		toAddRemove.addAll(toAdd);
		addRemoveTeammates(toAddRemove, selectedProject);
	}

	/**
	 * Retrieves a list of projects from the database along with their associated
	 * teams.
	 *
	 * @return List of Project objects containing project details and associated
	 *         teams.
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
	 * Archives selected projects by moving them to the Archive table and removing
	 * them from the Project table.
	 *
	 * @param selectedProjects List of Project objects to be archived.
	 */
	public void AddArchivesProjects(List<Project> selectedProjects) {
		for (Project project : selectedProjects) {
			try (Connection connection = dataSource.getConnection();
					PreparedStatement preparedStatement = connection
							.prepareStatement("INSERT INTO Archive (id, description, name, type) VALUES (?,?,?,?)")) {
				preparedStatement.setString(1, project.getId());
				preparedStatement.setString(2, project.getDescription());
				preparedStatement.setString(3, project.getName());
				preparedStatement.setString(4, project.getType());
				preparedStatement.executeUpdate();
				PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM Project WHERE id=?");
				preparedStatement2.setString(1, project.getId());
				preparedStatement2.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds or removes teammates from the Team table based on the provided list.
	 *
	 * @param toAddRemove     List of ProjectTeam objects to be added or removed.
	 * @param selectedProject Project for which teammates are added or removed.
	 */
	public void addRemoveTeammates(List<ProjectTeam> toAddRemove, Project selectedProject) {
		String idList = "";
		String submitList = "";
		String[] parts;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Team WHERE id= ?")) {
			preparedStatement.setString(1, selectedProject.getId());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				parts = resultSet.getString("submit").split(":");
				for (ProjectTeam teammate : toAddRemove) {
					idList += (teammate.getId() + ":");
					int find = 0;
					for (int i = 0; i < selectedProject.getProjectTeams().size(); i++) {
						if (teammate.getId().equals(selectedProject.getProjectTeams().get(i).getId())) {
							submitList += (parts[i] + ":");
							find = 1;
						}
					}
					if (find == 0) {
						submitList += "Pending:";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE Team SET teammate = ?, submit = ? WHERE id = ?")) {
			preparedStatement.setString(1, idList);
			preparedStatement.setString(2, submitList);
			preparedStatement.setString(3, selectedProject.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
