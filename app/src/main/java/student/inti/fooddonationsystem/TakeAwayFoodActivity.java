package student.inti.fooddonationsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TakeAwayFoodActivity extends AppCompatActivity {

    private ListView foodListView;
    private LinearLayout backButtonLayout;
    private ApiService apiService;
    private int userId;
    private String username;
    private SearchView searchView;
    private TextView selectedDateTextView;
    private String selectedDate = "";
    private List<FoodItem> originalFoodItems = new ArrayList<>();
    private FoodAdapter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_away_food);

        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");

        foodListView = findViewById(R.id.foodListView);
        backButtonLayout = findViewById(R.id.backButtonLayout);
        searchView = findViewById(R.id.searchView);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/fooddonation/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        loadAvailableFood();

        backButtonLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TakeAwayFoodActivity.this, HomeActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });

        foodListView.setOnItemClickListener((parent, view, position, id) -> {
            FoodItem selectedFoodItem = (FoodItem) parent.getItemAtPosition(position);
            Intent intent = new Intent(TakeAwayFoodActivity.this, FoodDetailActivity.class);
            intent.putExtra("food_id", selectedFoodItem.getId());
            intent.putExtra("food_name", selectedFoodItem.getFoodName());
            intent.putExtra("expiry_date", selectedFoodItem.getExpiryDate());
            intent.putExtra("quantity", selectedFoodItem.getQuantity());
            intent.putExtra("category", selectedFoodItem.getCategory());
            intent.putExtra("note", selectedFoodItem.getNote());
            intent.putExtra("location", selectedFoodItem.getLocation());
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        findViewById(R.id.datePickerBtn).setOnClickListener(v -> showDatePicker());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFood();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFood();
                return false;
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TakeAwayFoodActivity.this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
            selectedDateTextView.setText("Selected Date: " + dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            Log.d("SelectedDate", "Selected date is: " + selectedDate);
            filterFood();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void loadAvailableFood() {
        Call<List<FoodItem>> call = apiService.getAvailableFood();
        call.enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful()) {
                    List<FoodItem> foodList = response.body();
                    if (foodList != null && !foodList.isEmpty()) {
                        originalFoodItems.clear();
                        originalFoodItems.addAll(foodList);
                        filterFood();
                    } else {
                        Toast.makeText(TakeAwayFoodActivity.this, "No available food items", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TakeAwayFoodActivity.this, "Failed to load available food", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Toast.makeText(TakeAwayFoodActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterFood() {
        String query = searchView.getQuery().toString().toLowerCase();
        List<FoodItem> filteredList = new ArrayList<>();
        for (FoodItem item : originalFoodItems) {
            boolean matchesDate = selectedDate.isEmpty() || item.getExpiryDate().equals(selectedDate);
            boolean matchesQuery = query.isEmpty() || item.getFoodName().toLowerCase().contains(query) || item.getCategory().toLowerCase().contains(query);
            if (matchesDate && matchesQuery) {
                filteredList.add(item);
            }
        }
        updateListView(filteredList);
    }

    private void updateListView(List<FoodItem> filteredList) {
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No food items matching criteria", Toast.LENGTH_SHORT).show();
            foodListView.setAdapter(null);
        } else {
            foodAdapter = new FoodAdapter(this, filteredList);
            foodListView.setAdapter(foodAdapter);
        }
    }
}
