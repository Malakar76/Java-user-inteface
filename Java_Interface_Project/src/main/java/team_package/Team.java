package team_package;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import student_package.AccountCreation;
import student_package.Student;
import project_package.ProjectTeam;

/**
 * Represents a team in a project. A team has an ID, a name, and a list of
 * students who are members of the team.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Team implements Serializable, ProjectTeam {

	private String id;
	private String name;
	private List<Student> students;

	/**
	 * Default constructor for creating an instance of Team. Initializes the
	 * students list as an empty ArrayList.
	 */
	public Team() {
		this.students = new ArrayList<Student>();
	}

	/**
	 * Constructs a Team with specified details.
	 *
	 * @param id       The unique identifier for the team.
	 * @param name     The name of the team.
	 * @param students The list of students who are members of the team.
	 */
	public Team(String id, String name, List<Student> students) {
		this.id = id;
		this.name = name;
		this.students = students;
	}

	/**
	 * Creates and returns a copy of this team object.
	 *
	 * @return A clone of this instance.
	 */
	@Override
	public Team clone() {
		return new Team(getId(), getName(), getStudents());
	}

	/**
	 * Retrieves the list of students who are members of the team.
	 *
	 * @return The list of students in the team.
	 */
	public List<Student> getStudents() {
		return students;
	}

	/**
	 * Sets the list of students who are members of the team.
	 *
	 * @param students The new list of students to be set.
	 */
	public void setStudents(List<Student> students) {
		this.students = students;
	}

	/**
	 * Sets the project teams by replacing the current list of students with a new
	 * list.
	 *
	 * @param students The new list of students to be assigned to the project team.
	 */
	public void setProjectTeams(List<Student> students) {
		this.students = new ArrayList<Student>();
		this.students.addAll(students);

	}

	/**
	 * Retrieves the unique ID of the team.
	 *
	 * @return The unique ID as a String.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the unique ID for the team.
	 *
	 * @param id The new unique ID String to be set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves the name of the team.
	 *
	 * @return The name of the team as a String.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name for the team.
	 *
	 * @param Name The new name String to be set.
	 */
	public void setName(String Name) {
		this.name = Name;
	}

	/**
	 * Generates a hash code for the team. The hash code is generated based on the
	 * team's unique ID.
	 *
	 * @return A hash code value for the object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Compares this Team object with another object to determine if they are equal.
	 *
	 * @param obj The object to compare with this Team object.
	 * @return true if the objects are equal, false otherwise.
	 */
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

	/**
	 * Adds a student to the team.
	 *
	 * @param student The student to be added to the team.
	 */
	public void addStudent(Student student) {
		if (student != null) {
			this.students.add(student);
		}

	}

	/**
	 * Returns a string containing the IDs of the students in the team.
	 *
	 * @return A string representing the list of student IDs in the team.
	 */
	public String getTeamsList() {
		String result = "";
		for (ProjectTeam team : this.getStudents()) {
			result += team.getId() + ":";
		}
		return result;
	}

	/**
	 * Returns the display name of the team, which is the team's name.
	 *
	 * @return The name of the team as a String.
	 */
	public String displayName() {
		return this.name;
	}

	/**
	 * Returns a string representation of the Team object, including its ID, name,
	 * and the details of its students.
	 *
	 * @return A string containing the team's information.
	 */
	public String toString() {
		String result = (id + ":" + name + ":");
		for (Student student : students) {
			result += (student.toString() + ":");
		}
		return result;
	}

	/**
	 * Creates a new Team object by parsing a string in the specified format.
	 *
	 * @param value The string to parse.
	 * @return A new Team object created from the string.
	 */
	public static Team fromString(String value) {
		String[] parts = value.split(":");
		Team team = new Team();
		team.setId(parts[0]);
		team.setName(parts[1]);
		int size = (parts.length - 2) / 6;
		for (int i = 0; i < size; i++) {
			Student student = new Student();
			student.setId(parts[(i * 6) + 2]);
			student.setCode(parts[(i * 6) + 3]);
			student.setFirstName(parts[(i * 6) + 4]);
			student.setLastName(parts[(i * 6) + 5]);
			if (parts[6].equals("Created")) {
				student.setPassword(parts[(i * 6) + 6]);
				student.setAccountCreation(AccountCreation.Created);
			} else {
				student.setAccountCreation(AccountCreation.NotCreated);
			}
			team.addStudent(student);
		}
		return team;
	}
}