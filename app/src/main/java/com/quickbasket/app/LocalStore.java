package com.quickbasket.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small persistence boundary for the offline-first prototype. */
public final class LocalStore {
    private static final String PREFS = "quickbasket_local";
    private final SharedPreferences preferences;

    public LocalStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public Snapshot load() {
        Snapshot snapshot = new Snapshot();
        try {
            JSONObject savedCart = new JSONObject(preferences.getString("cart", "{}"));
            Iterator<String> keys = savedCart.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                int productId = Integer.parseInt(key);
                int quantity = savedCart.getInt(key);
                if (productId >= 0 && productId < ProductCatalog.all().size() && quantity > 0) {
                    snapshot.cart.put(productId, quantity);
                }
            }
            JSONArray savedAddresses = new JSONArray(preferences.getString("addresses", "[]"));
            for (int i = 0; i < savedAddresses.length(); i++) snapshot.addresses.add(savedAddresses.getString(i));
            JSONArray savedOrders = new JSONArray(preferences.getString("orders", "[]"));
            for (int i = 0; i < savedOrders.length(); i++) snapshot.orders.add(savedOrders.getString(i));
            snapshot.selectedAddress = preferences.getString("selected", snapshot.selectedAddress);
        } catch (Exception ignored) {
            // Keep the safe empty snapshot if persisted data is malformed.
        }
        return snapshot;
    }

    public void save(Map<Integer, Integer> cart, ArrayList<String> addresses,
                     ArrayList<String> orders, String selectedAddress) {
        try {
            JSONObject savedCart = new JSONObject();
            for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                savedCart.put(String.valueOf(item.getKey()), item.getValue());
            }
            preferences.edit()
                    .putString("cart", savedCart.toString())
                    .putString("addresses", new JSONArray(addresses).toString())
                    .putString("orders", new JSONArray(orders).toString())
                    .putString("selected", selectedAddress)
                    .apply();
        } catch (Exception ignored) {
            // In-memory shopping remains available if the cache cannot be written.
        }
    }

    public static final class Snapshot {
        public final Map<Integer, Integer> cart = new LinkedHashMap<>();
        public final ArrayList<String> addresses = new ArrayList<>();
        public final ArrayList<String> orders = new ArrayList<>();
        public String selectedAddress = "Home · Add your delivery address";
    }
}
