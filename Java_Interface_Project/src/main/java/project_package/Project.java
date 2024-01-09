package project_package;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Project implements Serializable {

    private String id;
    private String description;
    private String name;
    private String type;

    public Project() {
    }

    public Project(String id, String description, String name,  String type) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.type = type;
    }

    @Override
    public Project clone() {
        return new Project(getId(), getDescription(), getName(), getType());
    }

    // Getters and Setters

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
}