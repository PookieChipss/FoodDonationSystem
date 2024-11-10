package student.inti.fooddonationsystem;

import com.google.gson.annotations.SerializedName;

public class FoodItem {

    @SerializedName("id")
    private int id;

    @SerializedName("food_name")
    private String foodName;

    @SerializedName("expiry_date")
    private String expiryDate;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("category")
    private String category;

    @SerializedName("note")
    private String note;

    @SerializedName("location")
    private String location;

    // Getters
    public int getId() {
        return id;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getNote() {
        return note;
    }

    public String getLocation() {
        return location;
    }
}
