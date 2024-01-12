package project_package;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ApplicationScoped;
import java.sql.ResultSet;

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

	@ManagedProperty(value = "#{projectService}")
	private ProjectService projectService;

	@ManagedProperty(value = "#{teamsService}")
	private TeamsService teamsService;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
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
		this.projects = getProjectsTable();
		List<ProjectTeam> teamsSource = this.teamsService.getProjectTeams();
		List<ProjectTeam> teamsTarget = new ArrayList<>();

		projectTeams = new DualListModel<ProjectTeam>(teamsSource, teamsTarget);

	}

	public List<Project> getProjects() {
		return projects;
	}

	public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	public List<Project> getSelectedProjects() {
		return selectedProjects;
	}

	public void setSelectedProjects(List<Project> selectedProjects) {
		this.selectedProjects = selectedProjects;
	}

	public void openNew() {
		this.selectedProject = new Project();
	}

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

	public void saveTeams() {
		UpdateProject(selectedProjects.get(0));
		this.selectedProjects.get(0).setTeams(this.projectTeams.getTarget());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Teams Saved"));
		PrimeFaces.current().ajax().update("form:messages");
		PrimeFaces.current().executeScript("PF('manageProjectTeams').hide()");
	}

	public String getDeleteButtonMessage() {
		if (hasSelectedProjects()) {
			int size = this.selectedProjects.size();
			return size > 1 ? size + " projects selected" : "1 project selected";
		}

		return "Delete";
	}

	public boolean hasSelectedProjects() {
		return this.selectedProjects != null && !this.selectedProjects.isEmpty();
	}

	public boolean hasOneSelectedProject() {
		return this.selectedProjects != null && (this.selectedProjects.size() == 1);
	}

	public void deleteSelectedProjects() {
		DeleteProjects(this.selectedProjects);
		this.projects.removeAll(this.selectedProjects);
		this.selectedProjects = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}

	public void archiveSelectedProjects() {
		AddArchivesProjects(this.selectedProjects);
		this.projects.removeAll(this.selectedProjects);
		this.selectedProjects = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Archived"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");

	}

	public TeamsService getTeamsService() {
		return teamsService;
	}

	public void setTeamsService(TeamsService teamsService) {
		this.teamsService = teamsService;
	}

	public DualListModel<ProjectTeam> getProjectTeams() {
		return projectTeams;
	}

	public void setProjectTeams(DualListModel<ProjectTeam> projectTeams) {
		this.projectTeams = projectTeams;
	}

	public void editTeams() {
		if (this.selectedProjects.get(0).getType().equals("Individual")) {
			this.projectTeams.setSource(availableStudents(this.selectedProjects.get(0))); // mettre liste des élèves
																							// moins ceux déjà
																							// sélectionnés
			this.projectTeams.setTarget(this.selectedProjects.get(0).getTeams());
		} else {
			this.projectTeams.setSource(new ArrayList<ProjectTeam>()); // mettre liste des groups moins ceux déjà
																		// sélectionnés
			this.projectTeams.setTarget(this.selectedProjects.get(0).getTeams());

		}
	}

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

	public List<ProjectTeam> availableStudents(Project project) {
		List<ProjectTeam> available = new ArrayList<ProjectTeam>(getStudents());
		available.removeAll(project.getTeams());
		return available;
	}

	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Project (id VARCHAR(255), description VARCHAR(255), name VARCHAR(255), type VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Team (id VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void AddProject(Project selectedProject) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO Project (id, description, name, type) VALUES (?, ?, ?, ?)")) {
			preparedStatement.setString(1, selectedProject.getId());
			preparedStatement.setString(2, selectedProject.getDescription());
			preparedStatement.setString(3, selectedProject.getName());
			preparedStatement.setString(4, selectedProject.getType());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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

	public void UpdateProject(Project selectedProject) {
		List<ProjectTeam> toAdd = new ArrayList<ProjectTeam>(this.projectTeams.getTarget());
		toAdd.removeAll(selectedProject.getTeams());
		List<ProjectTeam> toRemove = new ArrayList<ProjectTeam>(selectedProject.getTeams());
		toRemove.retainAll(this.projectTeams.getSource());
		//TODO REMOVE AND ADD NEW TEAMMATES
	}

	public List<Project> getProjectsTable() {
		List<Project> projects = new ArrayList<Project>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Project")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Project project = new Project(resultSet.getString("id"), resultSet.getString("description"),
						resultSet.getString("name"), resultSet.getString("type"), new ArrayList<ProjectTeam>());
				try (PreparedStatement preparedStatement2 = connection.prepareStatement("SELECT * FROM Team WHERE id =?")) {
					preparedStatement2.setString(1, resultSet.getString("id"));
					ResultSet resultSet2 = preparedStatement2.executeQuery();
					if(resultSet2.next()) {
					int columnCount = resultSet2.getMetaData().getColumnCount();
					if (project.getType().equals("Individual")) {
						for (int i = 1; i <= columnCount; i++) {
							try (PreparedStatement preparedStatement3 = connection
									.prepareStatement("SELECT * FROM Student WHERE id = ?")) {
								preparedStatement3.setString(1, resultSet2.getString(i));
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
						// TODO MANAGE GROUP
					}}

				}
				projects.add(project);
			}
	}catch(SQLException e){
		e.printStackTrace();
	}return projects;
	}

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

}
