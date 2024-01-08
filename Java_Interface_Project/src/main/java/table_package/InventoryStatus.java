package table_package;

public enum InventoryStatus {
    AVAILABLE("Available"),
    OUT_OF_STOCK("Out of Stock"),
    BACK_ORDERED("Back Ordered"),
    DISCONTINUED("Discontinued");

    private final String text;

    private InventoryStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}