package project_package;

/**
 * The ProjectTeam interface represents a team associated with a project. It
 * defines methods for displaying the team name, converting to a string, and
 * retrieving the team ID.
 *
 * Implementing classes should provide specific implementations for these
 * methods.
 *
 * @author Robin Deplanques
 * @version 1.0
 */
public interface ProjectTeam {

	/**
	 * Returns the display name of the project team.
	 *
	 * @return A string representing the display name of the project team.
	 */
	public String displayName();

	/**
	 * Returns a string representation of the project team.
	 *
	 * @return A string representation of the project team.
	 */
	public String toString();

	/**
	 * Returns the unique identifier (ID) of the project team.
	 *
	 * @return A string representing the ID of the project team.
	 */
	public String getId();

}
