package table_package;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.primefaces.PrimeFaces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ApplicationScoped;

@SuppressWarnings("serial")
@ManagedBean(name="crudView")
@ApplicationScoped
public class CrudView implements Serializable {

	private boolean createAccountCheck;

    private List<Student> students;

    private Student selectedStudent;
    private Student currentStudent;

    private List<Student> selectedStudents;

    @ManagedProperty(value = "#{studentService}")
    private StudentService studentService;

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }
    
    public boolean isCreateAccountCheck() {
        return createAccountCheck;
    }

    public void setCreateAccountCheck(boolean createAccountCheck) {
        this.createAccountCheck = createAccountCheck;
    }
    public StudentService getStudentService() {
        return studentService;
    }

    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }
  
    @PostConstruct
    public void init() {
        this.students = this.studentService.getClonedStudents(100);
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(Student selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public List<Student> getSelectedStudents() {
        return selectedStudents;
    }

    public void setSelectedStudents(List<Student> selectedStudents) {
        this.selectedStudents = selectedStudents;
    }

    public void openNew() {
        System.out.println("openNew method called");
        this.selectedStudent = new Student();
        this.selectedStudent.setAccountCreation(AccountCreation.NotCreated);
    }
    
    public void createStudentAccount() {
        if (currentStudent != null && currentStudent.getAccountCreation() == AccountCreation.NotCreated) {
            createAccountForStudent(currentStudent);
            PrimeFaces.current().ajax().update("form:dt-students");
        }
    }
    
    private void createAccountForStudent(Student student) {
        String randomPassword = generateRandomPassword();
        student.setPassword(randomPassword);
        student.setAccountCreation(AccountCreation.Created);
    }

    public void saveStudent() {
        if (this.selectedStudent.getId() == null) {
            this.selectedStudent.setId("ST" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6));
            this.students.add(this.selectedStudent);
            if (createAccountCheck) {
            	createAccountForStudent(this.selectedStudent);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Added"));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Updated"));
        }

        PrimeFaces.current().executeScript("PF('manageStudentDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
    }

    public void deleteStudent() {
        this.students.remove(this.selectedStudent);
        this.selectedStudents.remove(this.selectedStudent);
        this.selectedStudent = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Student Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedStudents()) {
            int size = this.selectedStudents.size();
            return size > 1 ? size + " students selected" : "1 student selected";
        }

        return "Delete";
    }

    public boolean hasSelectedStudents() {
        return this.selectedStudents != null && !this.selectedStudents.isEmpty();
    }
    
    private String generateRandomPassword() {
        // Implémentez la logique pour générer un mot de passe aléatoire
        return UUID.randomUUID().toString().substring(0, 8); // Exemple simple
    }

    public void deleteSelectedStudents() {
        this.students.removeAll(this.selectedStudents);
        this.selectedStudents = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Students Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-students");
        PrimeFaces.current().executeScript("PF('dtStudents').clearFilters()");
    }

}