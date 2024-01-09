package table_package;

public enum AccountCreation {
    Created("Created"),
    NotCreated("NotCreated");

    private final String text;

    private AccountCreation(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}