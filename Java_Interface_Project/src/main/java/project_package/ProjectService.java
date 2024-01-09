package project_package;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="projectService")
@ApplicationScoped
public class ProjectService {

    private List<Project> projects;

    @PostConstruct
    public void init() {
    	projects = new ArrayList<>();
        // Exemple d'ajout de projets
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public List<Project> getClonedProjects(int size) {
        List<Project> results = new ArrayList<>();
        List<Project> originals = getProjects();
        for (Project original : originals) {
            results.add(original.clone());
        }

        // make sure to have unique codes
        for (Project project : results) {
            project.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }

        return results;
    }
}