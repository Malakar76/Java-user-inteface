package team_package;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import project_package.ProjectTeam;

@SuppressWarnings("serial")
public class Team implements Serializable {

    private String id;
    private String description;
    private String name;
    private List<ProjectTeam> students;

    public Team() {
    	this.students = new ArrayList<ProjectTeam>();
    }

    public Team(String id, String description, String name, List<ProjectTeam> students) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.students = students;
    }

    @Override
    public Team clone() {
        return new Team(getId(), getDescription(), getName(), getTeams());
    }

    // Getters and Setters

    public List<ProjectTeam> getTeams(){
    	return students;
    }
    
    public void setProjectTeams(List<ProjectTeam> projectTeams) {
    	this.students = new ArrayList<ProjectTeam>();
    	this.students.addAll(projectTeams);
    			
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
        Team other = (Team) obj;
        if (id == null) {
            return other.id == null;
        } else {
            return id.equals(other.id);
        }
    }
    
    public void addTeam(ProjectTeam students) {
    	if (students != null) {
    		this.students.add(students);
    	}
    	
    }
    
    public String getTeamsList() {
    	String result = "";
    	for (ProjectTeam team : this.getTeams()) {
    		result += (team.displayName()+"\n");
    		}
    		return result;
    }
}