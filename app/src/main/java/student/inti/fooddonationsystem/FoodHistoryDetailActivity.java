package student.inti.fooddonationsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodHistoryDetailActivity extends AppCompatActivity {

    private TextView foodNameTextView, statusTextView, categoryTextView;
    private EditText locationEditText, quantityEditText, noteEditText;
    private Button saveButton, backButton;
    private ApiService apiService;
    private int foodId;
    private String donationStatus;
    private int userId;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_history_detail);

        foodNameTextView = findViewById(R.id.foodNameTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        statusTextView = findViewById(R.id.statusTextView);
        locationEditText = findViewById(R.id.locationEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backBtn);

        foodId = getIntent().getIntExtra("food_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);
        String foodName = getIntent().getStringExtra("food_name");
        String category = getIntent().getStringExtra("category");
        donationStatus = getIntent().getStringExtra("donation_status");
        String location = getIntent().getStringExtra("location");
        String quantity = getIntent().getStringExtra("quantity");
        String note = getIntent().getStringExtra("note");

        foodNameTextView.setText("Food Name: " + foodName);
        categoryTextView.setText("Category: " + category);
        statusTextView.setText("Donation Status: " + donationStatus);
        locationEditText.setText(location);
        quantityEditText.setText(quantity);
        noteEditText.setText(note);

        if ("successful".equals(donationStatus)) {
            saveButton.setEnabled(false);
            locationEditText.setEnabled(false);
            quantityEditText.setEnabled(false);
            noteEditText.setEnabled(false);
        }

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        saveButton.setOnClickListener(v -> showConfirmationDialog());

        backButton.setOnClickListener(v -> showUnsavedChangesDialog());

        locationEditText.setOnKeyListener((v, keyCode, event) -> {
            hasChanges = true;
            return false;
        });

        quantityEditText.setOnKeyListener((v, keyCode, event) -> {
            hasChanges = true;
            return false;
        });

        noteEditText.setOnKeyListener((v, keyCode, event) -> {
            hasChanges = true;
            return false;
        });
    }

    private void navigateBackToHistory() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        showUnsavedChangesDialog();
    }

    private void updateFoodDetails(int foodId, String location, String quantity, String note) {
        Call<ResponseData> call = apiService.updateFood(foodId, location, quantity, note);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                // Log the response code and full body for debugging
                Log.d("API Response", "Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    // Log the complete response body
                    ResponseData responseData = response.body();
                    Log.d("API Response", "Response Status: " + responseData.getStatus() + ", Message: " + responseData.getMessage());

                    // Check if the response indicates success
                    if ("success".equalsIgnoreCase(responseData.getStatus())) {
                        Toast.makeText(FoodHistoryDetailActivity.this, "Food details updated successfully", Toast.LENGTH_SHORT).show();
                        navigateBackToHistory();
                    } else {
                        Toast.makeText(FoodHistoryDetailActivity.this, "Failed to update food details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FoodHistoryDetailActivity.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                    Log.e("API Response", "Unexpected response: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.e("API Error", "Error: " + t.getMessage());
                Toast.makeText(FoodHistoryDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Changes")
                .setMessage("Are you sure you want to make the changes?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String updatedLocation = locationEditText.getText().toString();
                    String updatedQuantity = quantityEditText.getText().toString();
                    String updatedNote = noteEditText.getText().toString();
                    updateFoodDetails(foodId, updatedLocation, updatedQuantity, updatedNote);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showUnsavedChangesDialog() {
        if (hasChanges) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("Changes you made may not be saved. Do you want to leave without saving?")
                    .setPositiveButton("Leave", (dialog, which) -> navigateBackToHistory())
                    .setNegativeButton("Stay", null)
                    .show();
        } else {
            navigateBackToHistory();
        }
    }
}
