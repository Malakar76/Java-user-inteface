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
	private DualListModel<ProjectTeams> projectTeams;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
		this.projects = this.projectService.getClonedProjects();
	    List<ProjectTeams> teamsSource = this.teamsService.getProjectTeams();
	    List<ProjectTeams> teamsTarget = new ArrayList<ProjectTeams>();
	     
	    projectTeams = new DualListModel<ProjectTeams>(teamsSource, teamsTarget);
	    
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Added"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Updated"));
		}

		PrimeFaces.current().executeScript("PF('manageProjectDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
	}
	
	public void saveTeams() {
		this.selectedProject.setTeams(this.projectTeams.getTarget());
		PrimeFaces.current().executeScript("PF('manageProjectTeams').hide()");
	}

	public void deleteProject() {
		this.projects.remove(this.selectedProject);
		this.selectedProjects.remove(this.selectedProject);
		this.selectedProject = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
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
		this.projects.removeAll(this.selectedProjects);
		this.selectedProjects = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}

	public void archiveSelectedProjects() {
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

	public DualListModel<ProjectTeams> getProjectTeams() {
		return projectTeams;
	}

	public void setProjectTeams(DualListModel<ProjectTeams> projectTeams) {
		this.projectTeams = projectTeams;
	}
	
	public void editTeams() {
		if (this.selectedProject.getType().equals("Individual")) {
			this.projectTeams.setSource(availableStudents(this.selectedProject)); // mettre liste des élèves moins ceux déjà sélectionnés
			this.projectTeams.setTarget(this.selectedProject.getTeams());
		}else {
			this.projectTeams.setSource(new ArrayList<ProjectTeams>()); // mettre liste des groups moins ceux déjà sélectionnés
			this.projectTeams.setTarget(this.selectedProject.getTeams());
			
		}
	}
	
	public List<ProjectTeams> getStudents() {
		List<ProjectTeams> students = new ArrayList<ProjectTeams>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Student")) {
				ResultSet resultSet = preparedStatement.executeQuery();
				 while (resultSet.next()) {
	                  Student student = new Student(resultSet.getString("id"),resultSet.getString("code"),resultSet.getString("firstName"),resultSet.getString("lastName"),resultSet.getString("password"),AccountCreation.NotCreated);
	                   if (resultSet.getString("accountCreation")=="Created") {
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
	
	public List<ProjectTeams> availableStudents(Project project){
		List<ProjectTeams> available = new ArrayList<ProjectTeams>(getStudents());
		available.removeAll(project.getTeams());
		return available;
	}
	
	
}
