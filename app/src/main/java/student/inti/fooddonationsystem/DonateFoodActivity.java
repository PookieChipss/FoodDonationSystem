package student.inti.fooddonationsystem;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DonateFoodActivity extends AppCompatActivity {

    private TextView foodName, expiryDate, quantity, note, location;
    private Spinner categorySpinner;
    private int userId;
    private String username;
    private boolean isFormChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_food);

        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");

        // Initialize UI components
        foodName = findViewById(R.id.foodName);
        expiryDate = findViewById(R.id.expiryDate);
        quantity = findViewById(R.id.quantity);
        note = findViewById(R.id.note);
        location = findViewById(R.id.location);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Set up the category spinner with a list of options, including the default option
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set default selection to "Choose a Category"
        categorySpinner.setSelection(0);

        // Set up date picker for expiry date field
        expiryDate.setOnClickListener(v -> showDatePicker());

        // Handle Add Food button click with validation and data submission
        findViewById(R.id.addFoodBtn).setOnClickListener(v -> {
            String food = foodName.getText().toString();
            String expiry = expiryDate.getText().toString();
            String qty = quantity.getText().toString();
            String cat = categorySpinner.getSelectedItem().toString();
            String foodNote = note.getText().toString();
            String loc = location.getText().toString();

            // Validate inputs before proceeding
            if (validateInputs(food, expiry, qty, cat, loc)) {
                showConfirmationDialog(food, expiry, qty, cat, foodNote, loc);
            }
        });

        // Handle Back button click
        findViewById(R.id.backBtn).setOnClickListener(v -> {
            if (isFormChanged) {
                showBackConfirmationDialog();
            } else {
                navigateToHome();
            }
        });

        // Add text change listeners to detect if the user changes any field
        addTextChangeListeners();
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(DonateFoodActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    expiryDate.setText(formattedDate);
                    isFormChanged = true;  // User changed the form
                }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInputs(String food, String expiry, String quantity, String category, String location) {
        if (food.isEmpty()) {
            foodName.setError("Food name is required");
            return false;
        }
        if (expiry.isEmpty()) {
            expiryDate.setError("Expiry date is required");
            return false;
        }
        if (quantity.isEmpty()) {
            this.quantity.setError("Quantity is required");
            return false;
        }
        if (category.equals("Choose a Category")) {  // Validate category selection
            Toast.makeText(this, "Category is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (location.isEmpty()) {
            this.location.setError("Location is required");
            return false;
        }
        return true;
    }

    private void showConfirmationDialog(String foodName, String expiryDate, String quantity, String category, String note, String location) {
        new AlertDialog.Builder(DonateFoodActivity.this)
                .setTitle("Confirm Donation")
                .setMessage("Are you sure you want to add the food donation?")
                .setPositiveButton("Yes", (dialog, which) -> addFood(foodName, expiryDate, quantity, category, note, location))
                .setNegativeButton("No", null)
                .show();
    }

    private void addFood(String foodName, String expiryDate, String quantity, String category, String note, String location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/fooddonation/")  // Use appropriate IP address or domain for production
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ResponseData> call = apiService.addFood(foodName, expiryDate, quantity, category, note, location, userId);

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DonateFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(DonateFoodActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DonateFoodActivity", "API call failed: " + t.getMessage());
            }
        });
    }

    private void handleError(Response<ResponseData> response) {
        try {
            if (response.errorBody() != null) {
                String errorResponse = response.errorBody().string();
                Log.e("DonateFoodActivity", "Error Response: " + errorResponse);
            }
        } catch (Exception e) {
            Log.e("DonateFoodActivity", "Exception reading error response: " + e.getMessage());
        }
        Toast.makeText(DonateFoodActivity.this, "Failed to add food. Check logs for details.", Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(DonateFoodActivity.this, HomeActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private void addTextChangeListeners() {
        foodName.addTextChangedListener(new SimpleTextWatcher(() -> isFormChanged = true));
        expiryDate.addTextChangedListener(new SimpleTextWatcher(() -> isFormChanged = true));
        quantity.addTextChangedListener(new SimpleTextWatcher(() -> isFormChanged = true));
        note.addTextChangedListener(new SimpleTextWatcher(() -> isFormChanged = true));
        location.addTextChangedListener(new SimpleTextWatcher(() -> isFormChanged = true));
    }

    private void showBackConfirmationDialog() {
        new AlertDialog.Builder(DonateFoodActivity.this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Are you sure you want to go back?")
                .setPositiveButton("Yes", (dialog, which) -> navigateToHome())
                .setNegativeButton("No", null)
                .show();
    }

    // SimpleTextWatcher to avoid boilerplate code for text change listeners
    private static class SimpleTextWatcher implements android.text.TextWatcher {
        private final Runnable onTextChanged;

        SimpleTextWatcher(Runnable onTextChanged) {
            this.onTextChanged = onTextChanged;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            onTextChanged.run();
        }

        @Override
        public void afterTextChanged(android.text.Editable editable) {}
    }
}
