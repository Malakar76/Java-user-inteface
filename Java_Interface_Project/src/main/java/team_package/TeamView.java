package team_package;

import javax.faces.bean.ManagedBean;
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

@ManagedBean(name = "teamView")
@ApplicationScoped
public class TeamView implements Serializable {

    private List<Team> teams;
    private Team selectedTeam;
    private DualListModel<Student> teamMembers;
	private List<Team> selectedTeams;
	
	
    public List<Team> getSelectedTeams() {
		return selectedTeams;
	}

	public void setSelectedTeams(List<Team> selectedTeams) {
		this.selectedTeams = selectedTeams;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

    
    @Resource(lookup = "java:comp/env/jdbc/h2db")
    private DataSource dataSource;

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

		teamMembers = new DualListModel<Student>(studentSource, studentTarget);

    }
 
	public void editTeams() {
			this.teamMembers.setSource(availableStudents(this.selectedTeams.get(0))); 
			this.teamMembers.setTarget(this.selectedTeams.get(0).getStudents());
	}

	public List<Team> getTeamsTable() {
		List<Team> teams = new ArrayList<Team>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Group")) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Team team = new Team(resultSet.getString("id"), resultSet.getString("name"),new ArrayList<Student>());
				String[] parts = resultSet.getString("student").split(":");
						for (int i = 0; i < parts.length; i++) {
								try (PreparedStatement preparedStatement3 = connection
										.prepareStatement("SELECT * FROM Student WHERE id = ?")) {
									preparedStatement3.setString(1, parts[i]);
									ResultSet resultSet3 = preparedStatement3.executeQuery();
									if (resultSet3.next()) {
										Student student = new Student(resultSet3.getString("id"),
												resultSet3.getString("code"), resultSet3.getString("firstName"),
												resultSet3.getString("lastName"), resultSet3.getString("password"),
												AccountCreation.NotCreated);
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

	public List<Student> availableStudents(Team team) {
		List<Student> available = new ArrayList<Student>(getStudents());
		available.removeAll(team.getStudents());
		return available;
	}

    public void saveTeam() {
        // Logique pour sauvegarder l'équipe dans la base de données
        if (selectedTeam.getId() == null) {
            selectedTeam.setId(UUID.randomUUID().toString().substring(0, 6));
            teams.add(selectedTeam);
            // Ajouter l'équipe dans la base de données
        } else {
            // Mettre à jour l'équipe dans la base de données
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-teams");
        PrimeFaces.current().executeScript("PF('manageTeamDialog').hide()");
    }

    // Getters et Setters

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public DualListModel<Student> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(DualListModel<Student> teamMembers) {
        this.teamMembers = teamMembers;
    }
   
	public void checkInitTable() {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
						"CREATE TABLE IF NOT EXISTS Group (id VARCHAR(255), name VARCHAR(255), student TEXT")) {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}