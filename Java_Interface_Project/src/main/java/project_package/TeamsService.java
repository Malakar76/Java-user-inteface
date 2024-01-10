package project_package;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;

import student_package.AccountCreation;
import student_package.Student;

import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="teamsService")
@ApplicationScoped
public class TeamsService {

    private List<ProjectTeams> projectTeams;

    @PostConstruct
    public void init() {
    	projectTeams = new ArrayList<>();
    	projectTeams.add(new Student("dejzjd", "fffze", "Fred", "Mercury",  "zkfefj", AccountCreation.Created));
    }

    public List<ProjectTeams> getProjectTeams() {
        return projectTeams;
    }
}