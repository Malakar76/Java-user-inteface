package student_package;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

@ManagedBean(name="studentService")
@ApplicationScoped
public class StudentService {

    private List<Student> students;

    @PostConstruct
    public void init() {
        students = new ArrayList<>();
        // Exemple d'ajout d'étudiants
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public List<Student> getClonedStudents(int size) {
        List<Student> results = new ArrayList<>();
        List<Student> originals = getStudents();
        for (Student original : originals) {
            results.add(original.clone());
        }

        // make sure to have unique codes
        for (Student student : results) {
            student.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        }

        return results;
    }
}