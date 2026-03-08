package com.example.restaurantapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    public static final String[] DINNER_ITEMS  = {
            "Rice + Beef", "Rice + Chicken", "Macaroni + Beef", "Fish & Chips"
    };
    public static final double[] DINNER_PRICES = {45.00, 40.00, 35.00, 47.50};

    public static final String[] DESSERT_ITEMS  = {
            "Custard & Jelly", "Chocolate Cake Slice", "Cupcakes", "Cup Cake"
    };
    public static final double[] DESSERT_PRICES = {30.00, 25.00, 20.00, 25.00};

    public static final String[] DRINK_ITEMS  = {
            "None", "Wine", "Pepsi", "Juice", "Water"
    };
    public static final double[] DRINK_PRICES = {0.00, 35.00, 15.00, 20.00, 10.00};

    private boolean[] dinnerSelected = new boolean[DINNER_ITEMS.length];
    private int       dessertIndex   = -1;
    private int       drinkIndex     = 0;
    private double    amountTendered = 0.0;

    public double getTotalAmount() {
        double total = 0;
        for (int i = 0; i < DINNER_ITEMS.length; i++)
            if (dinnerSelected[i]) total += DINNER_PRICES[i];
        if (dessertIndex >= 0)    total += DESSERT_PRICES[dessertIndex];
        if (drinkIndex > 0)       total += DRINK_PRICES[drinkIndex];
        return total;
    }

    public double getChange() {
        return amountTendered - getTotalAmount();
    }

    public List<String> getOrderLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < DINNER_ITEMS.length; i++)
            if (dinnerSelected[i])
                lines.add(String.format("  %-22s M%.2f",
                        DINNER_ITEMS[i], DINNER_PRICES[i]));
        if (dessertIndex >= 0)
            lines.add(String.format("  %-22s M%.2f",
                    DESSERT_ITEMS[dessertIndex], DESSERT_PRICES[dessertIndex]));
        if (drinkIndex > 0)
            lines.add(String.format("  %-22s M%.2f",
                    DRINK_ITEMS[drinkIndex], DRINK_PRICES[drinkIndex]));
        return lines;
    }

    public String getTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"));
    }

    public void reset() {
        dinnerSelected  = new boolean[DINNER_ITEMS.length];
        dessertIndex    = -1;
        drinkIndex      = 0;
        amountTendered  = 0.0;
    }

    public boolean[] getDinnerSelected()             { return dinnerSelected; }
    public void setDinnerSelected(int i, boolean v)  { dinnerSelected[i] = v; }
    public int  getDessertIndex()                    { return dessertIndex; }
    public void setDessertIndex(int i)               { dessertIndex = i; }
    public int  getDrinkIndex()                      { return drinkIndex; }
    public void setDrinkIndex(int i)                 { drinkIndex = i; }
    public double getAmountTendered()                { return amountTendered; }
    public void setAmountTendered(double v)          { amountTendered = v; }
}