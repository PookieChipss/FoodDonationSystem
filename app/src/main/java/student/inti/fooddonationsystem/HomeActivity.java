package student.inti.fooddonationsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    LinearLayout logoutBtn, tipsBtn;
    CardView donateFoodCard, takeAwayFoodCard, donationHistoryCard, takeawayHistoryCard;
    TextView welcomeTextView;
    int userId;
    String username;

    private static final int REQUEST_CODE_HISTORY_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutBtn = findViewById(R.id.logoutBtn);
        tipsBtn = findViewById(R.id.tipsBtn);
        donateFoodCard = findViewById(R.id.donateFoodCard);
        takeAwayFoodCard = findViewById(R.id.takeAwayFoodCard);
        donationHistoryCard = findViewById(R.id.donationHistoryCard);
        takeawayHistoryCard = findViewById(R.id.takeawayHistoryCard);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        // Retrieve user data from Intent (for initial load)
        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");

        setWelcomeMessage();

        logoutBtn.setOnClickListener(v -> showLogoutConfirmationDialog());

        tipsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TipsActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        donateFoodCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DonateFoodActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        takeAwayFoodCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TakeAwayFoodActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        donationHistoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            intent.putExtra("history_type", "donation");
            startActivityForResult(intent, REQUEST_CODE_HISTORY_ACTIVITY);
        });

        takeawayHistoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            intent.putExtra("history_type", "takeaway");
            startActivityForResult(intent, REQUEST_CODE_HISTORY_ACTIVITY);
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_HISTORY_ACTIVITY && resultCode == RESULT_OK && data != null) {
            userId = data.getIntExtra("user_id", -1);
            username = data.getStringExtra("username");
            setWelcomeMessage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve user data again on resume in case it was updated
        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");
        setWelcomeMessage();
    }

    private void setWelcomeMessage() {
        if (username != null && !username.isEmpty()) {
            welcomeTextView.setText("Welcome, " + username);
        } else {
            welcomeTextView.setText("Welcome");
        }
    }
}
