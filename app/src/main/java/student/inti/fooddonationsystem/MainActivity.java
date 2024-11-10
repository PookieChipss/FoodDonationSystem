package student.inti.fooddonationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException; // Add this import


public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private EditText email, password;
    private Button loginBtn, signupBtn;
    private TextView forgotPasswordLink; // Add this for the forgot password link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink); // Initialize the link

        // Initialize Retrofit with lenient Gson parser
        Gson gson = new GsonBuilder()
                .setLenient() // Allow Gson to parse lenient/malformed JSON responses
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/fooddonation/") // Ensure this matches your server URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Use lenient Gson
                .build();

        apiService = retrofit.create(ApiService.class);

        // Login Button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                // Logging to verify email and password before making the request
                Log.d("MainActivity", "Email: " + emailText);
                Log.d("MainActivity", "Password: " + passwordText);

                // Email format validation
                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Toast.makeText(MainActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Email and Password are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make login API call
                Call<ResponseData> call = apiService.loginUser(emailText, passwordText);
                call.enqueue(new Callback<ResponseData>() {
                    @Override
                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ResponseData responseData = response.body();
                            if (responseData.getStatus().equals("success")) {
                                // Login successful, navigate to HomeActivity
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("username", responseData.getUsername());
                                intent.putExtra("user_id", responseData.getUserId()); // Pass user ID
                                startActivity(intent);
                                finish(); // Close the current activity
                            } else {
                                // Show error message from the response
                                Toast.makeText(MainActivity.this, responseData.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Log and display the error
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    Log.e("MainActivity", "Error: " + errorBody);
                                    Toast.makeText(MainActivity.this, "Error: " + errorBody, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Error: Unable to read error body", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseData> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "Login failed: " + t.getMessage());
                    }
                });
            }
        });

        // Sign Up Button click listener
        signupBtn.setOnClickListener(v -> {
            // Navigate to RegisterActivity when Sign Up button is clicked
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Forgot Password Link click listener
        forgotPasswordLink.setOnClickListener(v -> {
            // Navigate to ForgotPasswordActivity when the link is clicked
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}
