package project_package;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;


import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="teamsService")
@ApplicationScoped
public class TeamsService {

    private List<ProjectTeam> projectTeams;

    @PostConstruct
    public void init() {
    	projectTeams = new ArrayList<>();
    }

    public List<ProjectTeam> getProjectTeams() {
        return projectTeams;
    }
}