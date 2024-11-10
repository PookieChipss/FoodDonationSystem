package student.inti.fooddonationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {

    LinearLayout backButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        backButtonLayout = findViewById(R.id.backButtonLayout);

        backButtonLayout.setOnClickListener(view -> {
            // Get user data from Intent
            int userId = getIntent().getIntExtra("user_id", -1);
            String username = getIntent().getStringExtra("username");

            // Return to HomeActivity with username and userId
            Intent intent = new Intent(TipsActivity.this, HomeActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
            finish(); // Close TipsActivity
        });
    }
}
