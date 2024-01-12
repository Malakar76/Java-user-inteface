package project_package;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Project implements Serializable {

    private String id;
    private String description;
    private String name;
    private String type;
    private List<ProjectTeam> projectTeams;

    public Project() {
    	this.projectTeams = new ArrayList<ProjectTeam>();
    }

    public Project(String id, String description, String name,  String type, List<ProjectTeam> projectTeams) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.type = type;
        this.projectTeams = projectTeams;
    }

    @Override
    public Project clone() {
        return new Project(getId(), getDescription(), getName(), getType(), getProjectTeams());
    }

    // Getters and Setters

    public List<ProjectTeam> getProjectTeams(){
    	return projectTeams;
    }
    
    public void setProjectTeams(List<ProjectTeam> projectTeams) {
    	this.projectTeams = new ArrayList<ProjectTeam>();
    	this.projectTeams.addAll(projectTeams);
    			
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }
    
    public String getType() {
    	return type;
    }
    
    public void setType(String Type) {
    	this.type = Type;
    }

    // hashCode and equals based on 'id

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }
    
    public void addTeam(ProjectTeam team) {
    	if (team != null) {
    		this.projectTeams.add(team);
    	}
    	
    }
    
    public String getTeamsList() {
    	String result = "";
    	if (this.getType().equals("Individual")) {
    		for (ProjectTeam team : this.getProjectTeams()) {
    			result += (team.displayName()+"\n");
    		}
    		return result;
    	}else {
    		return result; //TODO : Case of Group Teams
    	}
		
		
	}
}