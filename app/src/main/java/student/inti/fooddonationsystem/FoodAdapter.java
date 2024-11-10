package student.inti.fooddonationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

public class FoodAdapter extends BaseAdapter {

    private final Context context;
    private final List<FoodItem> foodItemList;

    // Constructor
    public FoodAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
    }

    @Override
    public int getCount() {
        return foodItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // ViewHolder class to improve performance by avoiding repeated calls to findViewById
    private static class ViewHolder {
        TextView foodName;
        TextView expiryDate;
        TextView quantity;
        TextView category;
        TextView note;
        TextView location;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Check if the view already exists. If not, inflate it and create the ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_item_layout, parent, false);

            holder = new ViewHolder();
            holder.foodName = convertView.findViewById(R.id.foodName);
            holder.expiryDate = convertView.findViewById(R.id.expiryDate);
            holder.quantity = convertView.findViewById(R.id.quantity);
            holder.category = convertView.findViewById(R.id.category);
            holder.note = convertView.findViewById(R.id.note);
            holder.location = convertView.findViewById(R.id.location);

            convertView.setTag(holder);  // Store the holder with the view
        } else {
            holder = (ViewHolder) convertView.getTag();  // Reuse the holder
        }

        // Get the current FoodItem
        FoodItem foodItem = foodItemList.get(position);

        // Populate the ViewHolder fields with data
        holder.foodName.setText("Food Name: " + foodItem.getFoodName());
        holder.expiryDate.setText("Expiry Date: " + foodItem.getExpiryDate());
        holder.quantity.setText("Quantity: " + foodItem.getQuantity());
        holder.category.setText("Category: " + foodItem.getCategory());
        holder.note.setText("Note: " + foodItem.getNote());
        holder.location.setText("Location: " + foodItem.getLocation());

        return convertView;
    }
}
