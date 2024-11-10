package student.inti.fooddonationsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private SearchView searchView;
    private TextView noHistoryTextView;
    private TextView selectedDateTextView;
    private Button datePickerBtn;
    private String historyType;
    private int userId;
    private List<HistoryItem> originalHistoryItems = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    private String selectedDate; // Added to store the selected date
    private LinearLayout backButtonLayout; // Modified to use LinearLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        noHistoryTextView = findViewById(R.id.noHistoryTextView);
        searchView = findViewById(R.id.searchView);
        datePickerBtn = findViewById(R.id.datePickerBtn);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        backButtonLayout = findViewById(R.id.backButtonLayout); // Initialize backButtonLayout

        userId = getIntent().getIntExtra("user_id", -1);
        historyType = getIntent().getStringExtra("history_type");

        backButtonLayout.setOnClickListener(v -> finish()); // Set onClickListener for backButtonLayout

        // Fetch the history
        fetchHistory();

        // Set up the SearchView listener for food name and category filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterHistory(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterHistory(newText);
                return false;
            }
        });

        // Set up the date picker for filtering by date
        datePickerBtn.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format selected date as "YYYY-MM-DD"
                    selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                    selectedDateTextView.setText("Selected Date: " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);

                    Log.d("SelectedDate", "Selected date is: " + selectedDate); // Log for debugging

                    // Filter history based on selected date
                    filterHistoryByDate(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void fetchHistory() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<HistoryResponse> historyCall = apiService.getUserHistory(userId);

        historyCall.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistoryItem> allHistory = response.body().getData();
                    originalHistoryItems.clear();

                    // Filter based on history type
                    for (HistoryItem item : allHistory) {
                        if ("donation".equals(historyType) && item.getAction().equalsIgnoreCase("donate")) {
                            originalHistoryItems.add(item);
                        } else if ("takeaway".equals(historyType) && item.getAction().equalsIgnoreCase("takeaway")) {
                            originalHistoryItems.add(item);
                        }
                    }
                    updateListView();
                } else {
                    Toast.makeText(HistoryActivity.this, "No history found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                Toast.makeText(HistoryActivity.this, "Error fetching history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterHistory(String query) {
        List<HistoryItem> filteredList = new ArrayList<>();
        for (HistoryItem item : originalHistoryItems) {
            if (item.getFood_name().toLowerCase().contains(query.toLowerCase())
                    || item.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        updateListView(filteredList);
    }

    private void filterHistoryByDate(String date) {
        List<HistoryItem> dateFilteredHistory = new ArrayList<>();
        for (HistoryItem item : originalHistoryItems) {
            if (item.getTimestamp().startsWith(date)) {
                dateFilteredHistory.add(item);
            }
        }
        updateListView(dateFilteredHistory);
    }

    private void updateListView() {
        if (originalHistoryItems.isEmpty()) {
            showNoHistoryMessage();
        } else {
            noHistoryTextView.setVisibility(View.GONE);
            historyListView.setVisibility(View.VISIBLE);
            originalHistoryItems.sort((item1, item2) -> item1.getStatus().equalsIgnoreCase("pending") ? -1 : 1);
            historyAdapter = new HistoryAdapter(this, originalHistoryItems, historyType);
            historyListView.setAdapter(historyAdapter);
        }
    }

    private void updateListView(List<HistoryItem> filteredList) {
        if (filteredList.isEmpty()) {
            showNoHistoryMessage();
        } else {
            noHistoryTextView.setVisibility(View.GONE);
            historyListView.setVisibility(View.VISIBLE);
            historyAdapter.clear();
            historyAdapter.addAll(filteredList);
            historyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchHistory();
    }

    public void showNoHistoryMessage() {
        noHistoryTextView.setVisibility(View.VISIBLE);
        historyListView.setVisibility(View.GONE);
    }
}
