package com.example.pet_inventory.models;


public enum Category {
    DOG_FOOD("Dog Food"),
    CAT_FOOD("Cat Food"),
    DOG_TREAT("Dog Treat"),
    CAT_TREAT("Cat Treat"),
    ACCESSORY("Accessory");

    private final String dbValue;

    Category(String dbValue) {
        this.dbValue = dbValue;
    }
    public String getDbValue() {
        return dbValue;
    }

    // 👉 dùng khi load từ database
    public static Category fromDb(String value) {
        for (Category category: Category.values()) {
            if (category.dbValue.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category from DB: " + value);
    }

    // 👉 hiển thị đẹp trên UI / TableView
    @Override
    public  String toString() {
        return dbValue;
    }
}