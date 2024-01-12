package team_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;

@FacesConverter(value = "teamConverter")
public class TeamConverter implements Converter {

	@Override
    public Team getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
			return Team.fromString(value);
	}
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
    		return ((Team)value).toString();
    }
}
