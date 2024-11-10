package student.inti.fooddonationsystem;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResponseData {

    @SerializedName("status")   // Ensure that the field matches with API response
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("username")
    private String username;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("data")    // Ensure that the field matches with API response
    private List<HistoryItem> data;  // List to hold history items

    // Getters and Setters
    public String getStatus() {
        return status != null ? status : "";  // Null safety
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message != null ? message : "No message available";  // Null safety with default message
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username != null ? username : "Anonymous";  // Null safety with default username
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<HistoryItem> getData() {
        return data != null ? data : List.of();  // Null safety: return empty list if null
    }

    public void setData(List<HistoryItem> data) {
        this.data = data;
    }

    // Helper method to check if data is empty
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
}
