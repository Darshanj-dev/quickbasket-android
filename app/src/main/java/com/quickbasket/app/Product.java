package com.quickbasket.app;

/** Immutable catalog item used by the storefront and checkout flow. */
public final class Product {
    public final int id;
    public final String name;
    public final String category;
    public final String emoji;
    public final String unit;
    public final String description;
    public final int price;
    public final int mrp;
    public final int imageColor;

    public Product(int id, String name, String category, String emoji, String unit, String description,
                   int price, int mrp, int imageColor) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.emoji = emoji;
        this.unit = unit;
        this.description = description;
        this.price = price;
        this.mrp = mrp;
        this.imageColor = imageColor;
    }
}
