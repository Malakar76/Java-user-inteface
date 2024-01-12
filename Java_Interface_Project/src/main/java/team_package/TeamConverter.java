package team_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;
import project_package.ProjectTeam;

@FacesConverter(value = "TeamConverter")
public class TeamConverter implements Converter {

	@Override
    public ProjectTeam getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
			return Student.fromString(value);
	}
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof ProjectTeam) {
            return ((ProjectTeam)value).toString();
        }
        return null;
    }
}
