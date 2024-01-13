package team_package;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import student_package.AccountCreation;
import student_package.Student;
import project_package.ProjectTeam;

@SuppressWarnings("serial")
public class Team implements Serializable, ProjectTeam {

    private String id;
    private String name;
    private List<Student> students;

    public Team() {
    	this.students = new ArrayList<Student>();
    }

    public Team(String id, String name, List<Student> students) {
        this.id = id;
        this.name = name;
        this.students = students;
    }

    @Override
    public Team clone() {
        return new Team(getId(), getName(), getStudents());
    }

    // Getters and Setters

    public List<Student> getStudents(){
    	return students;
    }
    
    public void setStudents(List<Student> students){
    	this.students = students;
    }
    
    public void setProjectTeams(List<Student> students) {
    	this.students = new ArrayList<Student>();
    	this.students.addAll(students);
    			
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
  
    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }
    // hashCode and equals based on 'id

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

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
    
    public void addStudent(Student student) {
    	if (student != null) {
    		this.students.add(student);
    	}
    	
    }
    
    public String getTeamsList() {
    	String result = "";
    	for (ProjectTeam team : this.getStudents()) {
    		result += team.getId()+":";
    		}
    		return result;
    }

    public String displayName() {
    	return this.name;
    }
    
    public String toString() {
    	String result = (id+":"+name+":");
    	for (Student student : students){
    		result+=(student.toString()+":");
    }
    	return result;
}

	public static Team fromString(String value) {
    	String [] parts = value.split(":");
    	Team team = new Team();
    	team.setId(parts[0]);
    	team.setName(parts[1]);
    	int size = (parts.length-2)/6;
    	for (int i=0; i<size; i++){
        	Student student = new Student();
        	student.setId(parts[(i*6)+2]);
        	student.setCode(parts[(i*6)+3]);
        	student.setFirstName(parts[(i*6)+4]);
        	student.setLastName(parts[(i*6)+5]);
        	if (parts[6].equals("Created")) {
        		student.setPassword(parts[(i*6)+ 6]);
        		student.setAccountCreation(AccountCreation.Created);
        	}else {
        		student.setAccountCreation(AccountCreation.NotCreated);
        	}
        	team.addStudent(student); 	    	
    	}
    	return team;
	}
}