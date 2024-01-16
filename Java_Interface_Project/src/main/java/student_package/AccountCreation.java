package student_package;

/**
 * The `AccountCreation` enum represents the possible states of an account
 * creation for a student. It provides two values: `Created` and `NotCreated`.
 * Each value has a corresponding text representation.
 * 
 * @author Kilyan Bentchakal
 * @version 1.0
 */
public enum AccountCreation {
	Created("Created"), NotCreated("NotCreated");

	private final String text;

	/**
	 * Constructs an `AccountCreation` enum value with the given text
	 * representation.
	 *
	 * @param text The text representation of the enum value.
	 */
	private AccountCreation(String text) {
		this.text = text;
	}

	/**
	 * Get the text representation of the enum value.
	 *
	 * @return The text representation.
	 */
	public String getText() {
		return text;
	}
}