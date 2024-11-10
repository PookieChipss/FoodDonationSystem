package student.inti.fooddonationsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodDetailActivity extends AppCompatActivity {

    private TextView foodName, expiryDate, quantity, category, note, location;
    private LinearLayout takeAwayBtn, backBtn;
    private int foodId, userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        initializeUI();
        retrieveIntentData();

        displayFoodDetails();

        // Set onClickListeners for buttons
        takeAwayBtn.setOnClickListener(v -> confirmTakeAway());
        backBtn.setOnClickListener(v -> navigateBackToTakeAwayFood());
    }

    private void initializeUI() {
        // Initialize UI components
        foodName = findViewById(R.id.foodNameTextView);
        expiryDate = findViewById(R.id.expiryDateTextView);
        quantity = findViewById(R.id.quantityTextView);
        category = findViewById(R.id.categoryTextView);
        note = findViewById(R.id.noteTextView);
        location = findViewById(R.id.locationTextView);
        takeAwayBtn = findViewById(R.id.takeAwayButton);

        // Assign backBtn by finding the actionButtonLayout first
        LinearLayout actionButtonLayout = findViewById(R.id.actionButtonLayout);
        backBtn = (LinearLayout) actionButtonLayout.getChildAt(0);
    }

    private void retrieveIntentData() {
        // Retrieve data from the Intent
        foodId = getIntent().getIntExtra("food_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");
    }

    private void displayFoodDetails() {
        // Set food details in TextViews
        foodName.setText("Food Name: " + getIntent().getStringExtra("food_name"));
        expiryDate.setText("Expiry Date: " + getIntent().getStringExtra("expiry_date"));
        quantity.setText("Quantity: " + getIntent().getStringExtra("quantity"));
        category.setText("Category: " + getIntent().getStringExtra("category"));
        note.setText("Note: " + getIntent().getStringExtra("note"));
        location.setText("Location: " + getIntent().getStringExtra("location"));
    }

    private void confirmTakeAway() {
        // Show a confirmation dialog before taking away the food
        new AlertDialog.Builder(this)
                .setTitle("Confirm Take Away")
                .setMessage("Are you sure you want to take away this food item?")
                .setPositiveButton("Yes", (dialog, which) -> takeAwayFood(foodId, userId))
                .setNegativeButton("No", null)
                .show();
    }

    private void takeAwayFood(int foodId, int userId) {
        // Initialize Retrofit for network call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/fooddonation/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ResponseData> call = apiService.takeAwayFood(foodId, userId);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FoodDetailActivity.this, "Food taken successfully", Toast.LENGTH_SHORT).show();
                    navigateBackToTakeAwayFood();
                } else {
                    showErrorToast("Failed to take away food. Try again.");
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                showErrorToast("Error: " + t.getMessage());
            }
        });
    }

    private void showErrorToast(String message) {
        Toast.makeText(FoodDetailActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateBackToTakeAwayFood() {
        // Navigate back to TakeAwayFoodActivity
        Intent intent = new Intent(FoodDetailActivity.this, TakeAwayFoodActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
