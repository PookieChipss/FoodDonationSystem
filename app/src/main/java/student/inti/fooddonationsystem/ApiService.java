package student.inti.fooddonationsystem;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {

    // Endpoint for user registration
    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseData> registerUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    // Endpoint for user login
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseData> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    // Endpoint for adding food donation
    @FormUrlEncoded
    @POST("add_food.php")
    Call<ResponseData> addFood(
            @Field("food_name") String foodName,
            @Field("expiry_date") String expiryDate,
            @Field("quantity") String quantity,
            @Field("category") String category,
            @Field("note") String note,
            @Field("location") String location,
            @Field("user_id") int userId
    );

    // Endpoint for retrieving available food for takeaway
    @GET("get_available_food.php")
    Call<List<FoodItem>> getAvailableFood();

    // Endpoint for retrieving donation and takeaway history for a specific user
    @FormUrlEncoded
    @POST("get_user_history.php")
    Call<HistoryResponse> getUserHistory(
            @Field("user_id") int userId
    );

    // Endpoint for marking food as taken away
    @FormUrlEncoded
    @POST("take_away_food.php")
    Call<ResponseData> takeAwayFood(
            @Field("food_id") int foodId,
            @Field("user_id") int userId
    );

    // Endpoint for updating food details
    @FormUrlEncoded
    @POST("update_food.php")
    Call<ResponseData> updateFood(
            @Field("food_id") int foodId,
            @Field("location") String location,
            @Field("quantity") String quantity,
            @Field("note") String note
    );

    // Endpoint for deleting a food item
    @FormUrlEncoded
    @POST("delete_food.php")
    Call<ResponseData> deleteFood(
            @Field("food_id") int foodId
    );

    // Endpoint for resetting a user's password
    @FormUrlEncoded
    @POST("reset_password.php")
    Call<ResponseData> resetPassword(
            @Field("email") String email,
            @Field("newPassword") String newPassword // updated field name
    );
}
