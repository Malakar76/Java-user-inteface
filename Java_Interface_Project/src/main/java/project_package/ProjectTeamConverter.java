package project_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;

@FacesConverter(value = "projectTeamConverter")
public class ProjectTeamConverter implements Converter {

	@Override
    public ProjectTeam getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		System.out.println("getAsObject : "+value);
        return Student.fromString(value);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
    	System.out.println("getAsString: "+value.toString());
        if (value instanceof ProjectTeam) {
            return ((ProjectTeam)value).toString();
        }
        return null;
    }
}
