package team_package;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;
import student_package.AccountCreation;
import student_package.Student;

/**
 * The TeamView class represents the managed bean responsible for managing teams
 * in a web application. It handles CRUD (Create, Read, Update, Delete)
 * operations for teams and their members. This class interacts with a database
 * to store and retrieve team information. It also manages the user interface
 * for team management.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
@SuppressWarnings("serial")
@ManagedBean(name = "teamView")
@ApplicationScoped
public class TeamView implements Serializable {

	private List<Team> teams;
	private Team selectedTeam;
	private DualListModel<Student> teamMembers;
	private List<Team> selectedTeams;

	/**
	 * Retrieves the list of selected teams.
	 *
	 * @return A list of selected teams.
	 */
	public List<Team> getSelectedTeams() {
		return selectedTeams;
	}

	/**
	 * Sets the list of selected teams.
	 *
	 * @param selectedTeams The list of selected teams.
	 */
	public void setSelectedTeams(List<Team> selectedTeams) {
		this.selectedTeams = selectedTeams;
	}

	/**
	 * Retrieves the data source used for database operations.
	 *
	 * @return The data source.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the data source for database operations.
	 *
	 * @param dataSource The data source to set.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Retrieves the list of teams.
	 *
	 * @return A list of teams.
	 */
	public List<Team> getTeams() {
		return teams;
	}

	/**
	 * Sets the list of teams.
	 *
	 * @param teams The list of teams to set.
	 */
	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	/**
	 * Retrieves the currently selected team.
	 *
	 * @return The currently selected team.
	 */
	public Team getSelectedTeam() {
		return selectedTeam;
	}

	/**
	 * Sets the currently selected team.
	 *
	 * @param selectedTeam The currently selected team.
	 */
	public void setSelectedTeam(Team selectedTeam) {
		this.selectedTeam = selectedTeam;
	}

	/**
	 * Retrieves the DualListModel containing team members.
	 *
	 * @return The DualListModel containing team members.
	 */
	public DualListModel<Student> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * Sets the DualListModel containing team members.
	 *
	 * @param teamMembers The DualListModel containing team members.
	 */
	public void setTeamMembers(DualListModel<Student> teamMembers) {
		this.teamMembers = teamMembers;
	}

	@Resource(lookup = "java:comp/env/jdbc/h2db")
	private DataSource dataSource;

	/**
	 * Initializes the TeamView bean after construction. This method is annotated
	 * with @PostConstruct and is executed after the bean is created.
	 */
	@PostConstruct
	public void init() {
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/h2db");
			checkInitTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.teams = getTeamsTable();
		List<Student> studentSource = new ArrayList<>();
		List<Student> studentTarget = new ArrayList<>();
		this.selectedTeam = new Team();

		teamMembers = new DualListModel<Student>(studentSource, studentTarget);

	}

	/**
	 * Checks if there are selected teams.
	 *
	 * @return true if there are selected teams, false otherwise.
	 */
	public boolean hasSelectedTeams() {
		return this.selectedTeams != null && !this.selectedTeams.isEmpty();
	}

	/**
	 * Checks if there is exactly one selected team.
	 *
	 * @return true if there is one selected team, false otherwise.
	 */
	public boolean hasOneSelectedTeam() {
		return this.selectedTeams != null && (this.selectedTeams.size() == 1);
	}

	/**
	 * Gets the appropriate delete button message based on the number of selected
	 * teams.
	 *
	 * @return The delete button message.
	 */
	public String getDeleteButtonMessage() {
		if (hasSelectedTeams()) {
			int size = this.selectedTeams.size();
			return size > 1 ? size + " Teams selected" : "1 team selected";
		}

		return "Delete";
	}

	/**
	 * Edits the selected teams. This method is called when the "Edit" button is
	 * clicked. It sets the source and target members for the DualListModel based on
	 * the selected team.
	 */
	public void editTeams() {
		this.teamMembers.setSource(availableStudents(this.selectedTeams.get(0)));
		this.teamMembers.setTarget(this.selectedTeams.get(0).getStudents());
		this.selectedTeam = this.selectedTeams.get(0);
	}

	/**
	 * Retrieves the list of teams from the database.
	 *
	 * @return The list of teams.
	 */
	public List<Team> getTeamsTable() {
		List<Team> teams = new ArrayList<Team>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Groupe")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Team team = new Team(resultSet.getString("id"), resultSet.getString("name"), new ArrayList<Student>());
				String[] parts = resultSet.getString("student").split(":");
				for (int i = 0; i < parts.length; i++) {
					try (PreparedStatement preparedStatement3 = connection
							.prepareStatement("SELECT * FROM Student WHERE id = ?")) {
						preparedStatement3.setString(1, parts[i]);
						ResultSet resultSet3 = preparedStatement3.executeQuery();
						if (resultSet3.next()) {
							Student student = new Student(resultSet3.getString("id"), resultSet3.getString("code"),
									resultSet3.getString("firstName"), resultSet3.getString("lastName"),
									resultSet3.getString("password"), AccountCreation.NotCreated);
							if (resultSet3.getString("accountCreation") == "Created") {
								student.setAccountCreation(AccountCreation.Created);
							} else {
								student.setAccountCreation(AccountCreation.NotCreated);
							}
							team.addStudent(student);
						}

					}
				}
				teams.add(team);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teams;
	}

	/**
	 * Retrieves the list of students from the database.
	 *
	 * @return The list of students.
	 */
	public List<Student> getStudents() {
		List<Student> students = new ArrayList<Student>();
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

	/**
	 * Returns a list of available students to join a team.
	 *
	 * @param team The team for which to obtain the list of available students.
	 * @return A list of available students to join the team.
	 */
	public List<Student> availableStudents(Team team) {
		List<Student> available = new ArrayList<Student>(getStudents());
		available.removeAll(team.getStudents());
		return available;
	}

	/**
	 * Initializes a new team with empty members. This method is called when a user
	 * wants to create a new team.
	 */
	public void openNew() {
		this.selectedTeam = new Team();
		this.teamMembers.setSource(getStudents());
		this.teamMembers.setTarget(new ArrayList<Student>());
	}

	/**
	 * Saves a team (new or existing) after making modifications. This method is
	 * called when the user wants to save a team. If the team's ID is null, a new
	 * unique ID is generated. Team members are updated with the target list
	 * (selected students).
	 *
	 */
	public void saveTeam() {
		if (this.selectedTeam.getId() == null) {
			this.selectedTeam.setId("0GR" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
			this.selectedTeam.setStudents(this.teamMembers.getTarget());
			this.teams.add(this.selectedTeam);
			AddTeam(selectedTeam);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Team Added"));
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Team Updated"));
		}

		PrimeFaces.current().executeScript("PF('manageTeamDialog').hide()");
		PrimeFaces.current().ajax().update("form:messages", "form:dtTeams");

	}

	/**
	 * Deletes a list of teams from the database.
	 *
	 * @param selectedTeam The list of teams to be deleted.
	 */
	public void DeleteTeams(List<Team> selectedTeam) {
		for (Team team : selectedTeam) {
			try (Connection connection = dataSource.getConnection();
					PreparedStatement preparedStatement = connection
							.prepareStatement("DELETE FROM Groupe WHERE id=?")) {
				preparedStatement.setString(1, team.getId());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Deletes the selected teams from the list of teams. This method is called when
	 * the user wants to delete selected teams. It also updates the user interface
	 * to reflect the changes.
	 */

	public void deleteSelectedTeam() {
		DeleteTeams(this.selectedTeams);
		this.teams.removeAll(this.selectedTeams);
		this.selectedTeams = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Teams Removed"));
		PrimeFaces.current().ajax().update("form:messages", "form:dtTeams");
		PrimeFaces.current().executeScript("PF('dtTeams').clearFilters()");
	}

	/**
	 * Checks and initializes the database table for teams if it doesn't exist. This
	 * method is called during application startup to ensure the table exists. If
	 * the table doesn't exist, it creates it.
	 */
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Groupe (id VARCHAR(255), name VARCHAR(255), student TEXT)")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new team to the database.
	 *
	 * @param selectedTeam The team to be added to the database.
	 */
	public void AddTeam(Team selectedTeam) {
		String idList = "";
		for (Student student : selectedTeam.getStudents()) {
			idList += student.getId() + ":";
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("INSERT INTO Groupe (id, name, student) VALUES (?, ?, ?)")) {
			preparedStatement.setString(1, selectedTeam.getId());
			preparedStatement.setString(2, selectedTeam.getName());
			preparedStatement.setString(3, idList);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the selected team with the modified list of students. This method is
	 * called when the user edits and saves the team. It updates the team's list of
	 * students in the database and user interface.
	 */
	public void updateTeam() {
		updateStudents(selectedTeams.get(0));
		this.selectedTeams.get(0).setProjectTeams(this.teamMembers.getTarget());
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Students Saved"));
		PrimeFaces.current().ajax().update("form:messages");
		PrimeFaces.current().executeScript("PF('dtTeams').clearFilters()");
		PrimeFaces.current().executeScript("PF('editTeamDialog').hide()");

	}

	/**
	 * Updates the list of students for a selected team. This method is called when
	 * the user edits and saves the team. It determines which students need to be
	 * added or removed from the team and updates the database accordingly.
	 *
	 * @param selectedTeam The team whose students are being updated.
	 */
	public void updateStudents(Team selectedTeam) {
		List<Student> toAdd = new ArrayList<Student>(this.teamMembers.getTarget());
		toAdd.removeAll(selectedTeam.getStudents());
		List<Student> toRemove = new ArrayList<Student>(selectedTeam.getStudents());
		toRemove.retainAll(this.teamMembers.getSource());
		List<Student> toAddRemove = new ArrayList<Student>(selectedTeam.getStudents());
		toAddRemove.removeAll(toRemove);
		toAddRemove.addAll(toAdd);
		addRemoveStudents(toAddRemove, selectedTeam);
	}

	/**
	 * Adds or removes students to/from a selected team. This method is called when
	 * updating the list of students for a team. It adds or removes students in the
	 * database and updates the team's list.
	 *
	 * @param toaddRemove		  The list of students to be added or removed.
	 * @param selectedTeam        The team for which students are being added or
	 *                            removed.
	 */
	public void addRemoveStudents(List<Student> toaddRemove, Team selectedTeam) {
		String idList = "";
		for (Student students : toaddRemove) {
			idList += students.getId() + ":";
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("UPDATE Groupe SET student = ? WHERE id= ?")) {
			preparedStatement.setString(1, idList);
			preparedStatement.setString(2, selectedTeam.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}