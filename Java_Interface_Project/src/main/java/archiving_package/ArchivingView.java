package archiving_package;


import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
import project_package.ProjectService;
import project_package.ProjectTeam;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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

	@ManagedProperty(value = "#{projectService}")
	private ProjectService archivingService;

	 public void setDataSource(DataSource dataSource) {
	        this.dataSource = dataSource;
	    }
	    
		public DataSource getDataSource() {
	        return dataSource;
	    }
	
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
		try {
            Context context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/h2db");
            checkInitTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
		this.archives = getArchivesTable();
	}

	public List<Project> getArchives() {
		return archives;
	}
	
	public void setArchives(List<Project> archives) {
		this.archives = archives;
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

	public void deleteSelectedArchive() {
		DeleteArchive(this.selectedArchive);
		this.archives.remove(this.selectedArchive);
		this.selectedArchive = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archive Removed"));
		PrimeFaces.current().executeScript("PF('archiveDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dt-archive");
	}
	
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Archive (id VARCHAR(255), description VARCHAR(255), name VARCHAR(255), type VARCHAR(255))")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void DeleteArchive(Project selectedArchive) {
			try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Archive WHERE id=?")) {
				preparedStatement.setString(1, selectedArchive.getId());
				preparedStatement.executeUpdate();
				//TO DO : ADD DELETION OF TEAMS TABLE IF ANY
			} 	catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public List<Project> getArchivesTable() {
		List<Project> archives = new ArrayList<Project>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Archive")) {
				ResultSet resultSet = preparedStatement.executeQuery();
				 while (resultSet.next()) {
	                  Project archive = new Project(resultSet.getString("id"),resultSet.getString("description"),resultSet.getString("name"),resultSet.getString("type"),new ArrayList<ProjectTeam>());
	                  //TO DO : ADD TEAM IF ANY  
	                  archives.add(archive);
				 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return archives;
	}
	
	public void updateArchiveList() {
		this.archives = getArchivesTable();
		
	}
	
	
}
