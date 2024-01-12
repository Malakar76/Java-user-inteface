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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;

import project_package.ProjectTeam;
import student_package.Student;

@ManagedBean(name = "teamView")
@ApplicationScoped
public class TeamView implements Serializable {

    private List<Team> teams;
    private Team selectedTeam;
    private DualListModel<ProjectTeam> teamMembers;
    
    @Resource(lookup = "java:comp/env/jdbc/h2db")
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        loadTeamsFromDatabase();
    }

    private void loadTeamsFromDatabase() {
        // Logique pour charger les équipes à partir de la base de données
    }

    public void openNew() {
        selectedTeam = new Team();
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

    // Autres méthodes (suppression, mise à jour, etc.)

}