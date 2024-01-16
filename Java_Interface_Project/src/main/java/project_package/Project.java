package project_package;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * The Project class represents a project entity with its attributes and
 * associated project teams. It implements Serializable for object
 * serialization.
 * 
 * @author Robin Deplanques
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Project implements Serializable {

	private String id;
	private String description;
	private String name;
	private String type;
	private List<ProjectTeam> projectTeams;
	private List<String> submited;

	/**
	 * Default constructor initializes projectTeams and submited lists.
	 */
	public Project() {
		this.projectTeams = new ArrayList<ProjectTeam>();
		this.submited = new ArrayList<String>();
	}

	/**
	 * Parameterized constructor to create a Project with specified attributes.
	 *
	 * @param id           Unique identifier for the project.
	 * @param description  Description of the project.
	 * @param name         Name of the project.
	 * @param type         Type of the project.
	 * @param projectTeams List of ProjectTeam associated with the project.
	 * @param submited     List of submission status associated with the project.
	 */
	public Project(String id, String description, String name, String type, List<ProjectTeam> projectTeams,
			List<String> submited) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.type = type;
		this.projectTeams = projectTeams;
		this.submited = submited;
	}

	/**
	 * Clone method to create a deep copy of the project.
	 *
	 * @return A new Project object with the same attributes as the original.
	 */
	@Override
	public Project clone() {
		return new Project(getId(), getDescription(), getName(), getType(), getProjectTeams(), getSubmited());
	}

	// Getters and Setters

	/**
	 * Getter for the projectTeams list.
	 *
	 * @return List of ProjectTeam associated with the project.
	 */
	public List<ProjectTeam> getProjectTeams() {
		return projectTeams;
	}

	/**
	 * Setter for the projectTeams list.
	 *
	 * @param projectTeams List of ProjectTeam to be set for the project.
	 */
	public void setProjectTeams(List<ProjectTeam> projectTeams) {
		this.projectTeams = new ArrayList<ProjectTeam>();
		this.projectTeams.addAll(projectTeams);

	}

	/**
	 * Getter for the project ID.
	 *
	 * @return The unique identifier for the project.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter for the submission status list.
	 *
	 * @return List of submission status associated with the project.
	 */
	public List<String> getSubmited() {
		return submited;
	}

	/**
	 * Setter for the submission status list.
	 *
	 * @param submited List of submission status to be set for the project.
	 */
	public void setSubmited(List<String> submited) {
		this.submited = submited;
	}

	/**
	 * Setter for the project ID.
	 *
	 * @param id The unique identifier to be set for the project.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter for the project description.
	 *
	 * @return The description of the project.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for the project description.
	 *
	 * @param description The description to be set for the project.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for the project name.
	 *
	 * @return The name of the project.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the project name.
	 *
	 * @param Name The name to be set for the project.
	 */
	public void setName(String Name) {
		this.name = Name;
	}

	/**
	 * Getter for the project type.
	 *
	 * @return The type of the project.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter for the project type.
	 *
	 * @param Type The type to be set for the project.
	 */
	public void setType(String Type) {
		this.type = Type;
	}

	// hashCode and equals based on 'id

	/**
	 * Generates a hash code for the project based on its unique identifier ('id').
	 *
	 * @return The hash code for the project.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Checks whether two projects are equal based on their unique identifier
	 * ('id').
	 *
	 * @param obj The object to be compared with the current project.
	 * @return True if the projects are equal, false otherwise.
	 */
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

	/**
	 * Adds a ProjectTeam to the project's list of teams.
	 *
	 * @param team The ProjectTeam to be added to the project.
	 */
	public void addTeam(ProjectTeam team) {
		if (team != null) {
			this.projectTeams.add(team);
		}

	}

	/**
	 * Adds a submission status to the project's list of submissions.
	 *
	 * @param value The submission status to be added to the project.
	 */
	public void addSubmit(String value) {
		this.submited.add(value);
	}
}