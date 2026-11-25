package com.quickbasket.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Bundled demo catalog, available while the device is offline. */
public final class ProductCatalog {
    private ProductCatalog() { }

    private static final List<Product> PRODUCTS = Collections.unmodifiableList(Arrays.asList(
            new Product(0, "Alphonso Mangoes", "Fruits", "🥭", "1 pack · 500 g", "Naturally sweet, aromatic Alphonso mangoes selected at peak ripeness. Handle gently and refrigerate after ripening.", 129, 159, 0xfffff2d4),
            new Product(1, "Farm Fresh Tomatoes", "Vegetables", "🍅", "1 unit · 500 g", "Firm, juicy tomatoes from trusted farms. A versatile everyday ingredient for salads, curries and sauces.", 32, 40, 0xffffe9e5),
            new Product(2, "Amul Taaza Milk", "Dairy", "🥛", "1 litre", "Fresh toned milk with a smooth, light taste. Keep refrigerated and consume before the date on the pack.", 28, 32, 0xffe7f2ff),
            new Product(3, "Brown Bread", "Bakery", "🍞", "1 pack · 400 g", "Soft whole-wheat bread baked for everyday breakfasts and sandwiches. Store in a cool, dry place.", 45, 55, 0xfffff0d9),
            new Product(4, "Hass Avocado", "Fruits", "🥑", "1 unit", "Creamy Hass avocado with a rich texture. Allow it to ripen at room temperature before serving.", 89, 110, 0xffe8f4e8),
            new Product(5, "Bananas", "Fruits", "🍌", "1 dozen", "Naturally sweet bananas, packed with care. A convenient snack for any time of day.", 42, 50, 0xfffff4cc),
            new Product(6, "Red Bell Pepper", "Vegetables", "🫑", "1 unit", "Crisp red bell pepper with a mild sweetness. Rinse before use and keep refrigerated.", 55, 69, 0xffffece5),
            new Product(7, "Greek Yogurt", "Dairy", "🥣", "1 cup · 200 g", "Thick, creamy Greek-style yogurt. Keep refrigerated and enjoy on its own or with fresh fruit.", 65, 79, 0xfff1e9ff)
    ));

    public static List<Product> all() { return PRODUCTS; }
}
