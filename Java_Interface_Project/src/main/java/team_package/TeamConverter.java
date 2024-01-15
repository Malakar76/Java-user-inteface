package team_package;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import student_package.Student;

/**
 * A converter class for converting between Team objects and their string
 * representations.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
@FacesConverter(value = "teamConverter")
public class TeamConverter implements Converter {

	/**
	 * Converts a string representation of a Team into a Team object.
	 *
	 * @param facesContext The FacesContext instance.
	 * @param uiComponent  The UIComponent instance.
	 * @param value        The string value to convert.
	 * @return A Team object parsed from the provided string.
	 */
	@Override
	public Student getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		return Student.fromString(value);
	}

	/**
	 * Converts a Team object into its string representation.
	 *
	 * @param facesContext The FacesContext instance.
	 * @param uiComponent  The UIComponent instance.
	 * @param value        The Team object to convert.
	 * @return The string representation of the Team object.
	 */
	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		return ((Student) value).toString();
	}
}
