package com.example.pet_inventory.models;

/**
 * Enum representing product categories.
 * Maps database values to user-friendly display names.
 */
public enum Category {

    DOG_FOOD("Dog Food"),
    CAT_FOOD("Cat Food"),
    DOG_TREAT("Dog Treat"),
    CAT_TREAT("Cat Treat"),
    ACCESSORY("Accessory");

    private final String dbValue;

    // Constructor
    Category(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Get the database value of this category.
     *
     * @return String representation stored in DB
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Convert a database string value to a Category enum.
     *
     * @param value DB value
     * @return Category enum
     * @throws IllegalArgumentException if value is invalid
     */
    public static Category fromDb(String value) {
        for (Category category : Category.values()) {
            if (category.dbValue.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category from DB: " + value);
    }

    /**
     * Return user-friendly name for display in UI / TableView.
     *
     * @return Display name
     */
    @Override
    public String toString() {
        return dbValue;
    }
}