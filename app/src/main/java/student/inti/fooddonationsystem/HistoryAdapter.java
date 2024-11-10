package student.inti.fooddonationsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

    private ApiService apiService;
    private List<HistoryItem> historyItems;
    private Context context;
    private String historyType;

    public HistoryAdapter(Context context, List<HistoryItem> historyItems, String historyType) {
        super(context, 0, historyItems);
        this.context = context;
        this.historyItems = historyItems;
        this.historyType = historyType; // Save the historyType
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryItem historyItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        }

        // Initialize views
        TextView foodNameTextView = convertView.findViewById(R.id.foodNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView timestampTextView = convertView.findViewById(R.id.timestampTextView);
        TextView quantityTextView = convertView.findViewById(R.id.quantityTextView);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        TextView noteTextView = convertView.findViewById(R.id.noteTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        ImageButton editIcon = convertView.findViewById(R.id.editIcon);
        ImageButton deleteIcon = convertView.findViewById(R.id.deleteIcon);

        // Set text data for each view
        foodNameTextView.setText("Food Name: " + historyItem.getFood_name());
        statusTextView.setText("Status: " + historyItem.getStatus());
        timestampTextView.setText("Timestamp: " + historyItem.getTimestamp());
        quantityTextView.setText("Quantity: " + historyItem.getQuantity());
        categoryTextView.setText("Category: " + historyItem.getCategory());
        noteTextView.setText("Note: " + historyItem.getNotes());
        locationTextView.setText("Location: " + historyItem.getLocation());

        // Show or hide edit/delete icons based on the donation status
        if ("pending".equals(historyItem.getStatus())) {
            editIcon.setVisibility(View.VISIBLE);
            deleteIcon.setVisibility(View.VISIBLE);

            // Set click listener for the edit icon
            editIcon.setOnClickListener(v -> openEditActivity(historyItem));

            // Set click listener for the delete icon
            deleteIcon.setOnClickListener(v -> confirmDeletion(historyItem, position));
        } else {
            editIcon.setVisibility(View.GONE);
            deleteIcon.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Open edit activity with item details
    private void openEditActivity(HistoryItem historyItem) {
        Intent intent = new Intent(context, FoodHistoryDetailActivity.class);
        intent.putExtra("food_id", historyItem.getFood_id());
        intent.putExtra("user_id", historyItem.getUser_id());
        intent.putExtra("food_name", historyItem.getFood_name());
        intent.putExtra("donation_status", historyItem.getStatus());
        intent.putExtra("location", historyItem.getLocation());
        intent.putExtra("quantity", String.valueOf(historyItem.getQuantity()));
        intent.putExtra("note", historyItem.getNotes());
        context.startActivity(intent);
    }

    // Confirm before deleting a food item
    private void confirmDeletion(HistoryItem historyItem, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Food")
                .setMessage("Are you sure you want to delete this food item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteFoodItem(historyItem, position))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // Method to delete food item from the server and update the list
    private void deleteFoodItem(HistoryItem historyItem, int position) {
        Call<ResponseData> call = apiService.deleteFood(historyItem.getFood_id());

        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(context, "Food item deleted successfully", Toast.LENGTH_SHORT).show();

                    // Remove item from the list and notify adapter
                    historyItems.remove(position);
                    notifyDataSetChanged();

                    // Check if the list is now empty and notify the parent activity if needed
                    if (historyItems.isEmpty() && context instanceof HistoryActivity) {
                        ((HistoryActivity) context).showNoHistoryMessage();
                    }
                } else {
                    Toast.makeText(context, "Failed to delete food item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
