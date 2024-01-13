package project_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;
import team_package.Team;

@FacesConverter(value = "projectTeamConverter")
public class ProjectTeamConverter implements Converter {

	@Override
    public ProjectTeam getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if(value.charAt(0)=='1') {
			return Student.fromString(value);
		}else {
			return Team.fromString(value);
		}
		
        
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof ProjectTeam) {
            return ((ProjectTeam)value).toString();
        }
        return null;
    }
}
