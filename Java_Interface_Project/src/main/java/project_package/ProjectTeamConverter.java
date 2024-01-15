package project_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;
import team_package.Team;

/**
 * The ProjectTeamConverter class is a JSF converter for converting ProjectTeam
 * objects to and from strings.
 *
 * It is annotated with @FacesConverter and registered with the value
 * "projectTeamConverter" for use in JSF applications. This converter is
 * specifically designed to convert instances of ProjectTeam, which may be
 * either Student or Team objects.
 *
 * @author Robin Deplanques
 * @version 1.0
 */
@FacesConverter(value = "projectTeamConverter")
public class ProjectTeamConverter implements Converter {

	/**
	 * Converts the given string value to a ProjectTeam object.
	 *
	 * If the value starts with '1', it is assumed to be a Student; otherwise, it is
	 * treated as a Team.
	 *
	 * @param facesContext The FacesContext.
	 * @param uiComponent  The UIComponent.
	 * @param value        The string value to be converted.
	 * @return A ProjectTeam object representing the converted value.
	 */
	@Override
	public ProjectTeam getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if (value.charAt(0) == '1') {
			return ((ProjectTeam) Student.fromString(value));
		} else {
			return Team.fromString(value);
		}

	}

	/**
	 * Converts the given ProjectTeam object to its string representation.
	 *
	 * @param facesContext The FacesContext.
	 * @param uiComponent  The UIComponent.
	 * @param value        The ProjectTeam object to be converted.
	 * @return A string representation of the ProjectTeam object.
	 */
	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value instanceof ProjectTeam) {
			return ((ProjectTeam) value).toString();
		}
		return null;
	}
}
