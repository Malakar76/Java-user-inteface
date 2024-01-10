package archiving_package;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;


import project_package.Project;
import project_package.ProjectService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ApplicationScoped;

@SuppressWarnings("serial")
@ManagedBean(name = "archivingView")
@ApplicationScoped
public class ArchivingView implements Serializable {

	private List<Project> archives;
	private Project selectedArchive;
	private Project currentArchive;

	private List<Project> selectedArchives;

	@ManagedProperty(value = "#{projectService}")
	private ProjectService archivingService;


	public Project getCurrentArchive() {
		return currentArchive;
	}

	public void setCurrentArchive(Project currenArchive) {
		this.currentArchive = currenArchive;
	}

	public ProjectService getArchivingService() {
		return archivingService;
	}

	public void setArchivingService(ProjectService archivingService) {
		this.archivingService = archivingService;
	}

	@PostConstruct
	public void init() {
		this.archives = this.archivingService.getClonedProjects();
	}

	public List<Project> getArchives() {
		return archives;
	}

	public Project getSelectedArchive() {
		return selectedArchive;
	}

	public void setSelectedArchive(Project selectedArchive) {
		this.selectedArchive = selectedArchive;
	}

	public List<Project> getSelectedArchives() {
		return selectedArchives;
	}

	public void setSelectedArchives(List<Project> selectedArchives) {
		this.selectedArchives = selectedArchives;
	}

	public void deleteArchive() {
		this.archives.remove(this.selectedArchive);
		this.selectedArchives.remove(this.selectedArchive);
		this.selectedArchive = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archive Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
	}

	public String getArchiveButtonMessage() {
		if (hasSelectedArchives()) {
			int size = this.selectedArchives.size();
			return size > 1 ? size + " archives selected" : "1 archive selected";
		}

		return "Delete";
	}

	public boolean hasSelectedArchives() {
		return this.selectedArchives != null && !this.selectedArchives.isEmpty();
	}

	public void deleteSelectedArchives() {
		this.archives.removeAll(this.selectedArchives);
		this.selectedArchives = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archives Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dt-projects");
		PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
	}
}
