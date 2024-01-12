package team_package;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="TeamService")
@ApplicationScoped
public class TeamService {

    private List<Team> teams;

    @PostConstruct
    public void init() {
    	teams = new ArrayList<>();
        // Exemple d'ajout de projets
    }

    public List<Team> getTeam() {
        return new ArrayList<>(teams);
    }

    public List<Team> getClonedProjects() {
        List<Team> results = new ArrayList<>();
        List<Team> originals = getTeam();
        for (Team original : originals) {
            results.add(original.clone());
        }

        // make sure to have unique codes
        for (Team project : results) {
            project.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }

        return results;
    }
}