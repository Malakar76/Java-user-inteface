package project_package;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.primefaces.PrimeFaces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ApplicationScoped;

@SuppressWarnings("serial")
@ManagedBean(name="projectView")
@ApplicationScoped
public class ProjectView implements Serializable {

	private List<Project> projects;

    private Project selectedProject;
    private Project currentProject;

    private List<Project> selectedProjects;

    @ManagedProperty(value = "#{projectService}")
    private ProjectService projectService;
    

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
        this.projects = this.projectService.getClonedProjects();
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
        System.out.println("openNew method called");
        this.selectedProject = new Project();
    }

    public void saveProject() {
        if (this.selectedProject.getId() == null) {
            this.selectedProject.setId("ST" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
            this.projects.add(this.selectedProject);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Added"));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Project Updated"));
        }

        PrimeFaces.current().executeScript("PF('manageProjectDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
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
    

    public void deleteSelectedProjects() {
        this.projects.removeAll(this.selectedProjects);
        this.selectedProjects = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Projects Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
        PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
    }

}