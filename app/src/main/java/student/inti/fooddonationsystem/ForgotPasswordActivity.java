package student.inti.fooddonationsystem;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email, newPassword, confirmPassword;
    private Button resetPasswordBtn, backBtn;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.email);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        backBtn = findViewById(R.id.backBtn);

        Retrofit retrofit = ApiClient.getClient();
        apiService = retrofit.create(ApiService.class);

        resetPasswordBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String newPasswordText = newPassword.getText().toString().trim();
            String confirmPasswordText = confirmPassword.getText().toString().trim();

            if (emailText.isEmpty() || newPasswordText.isEmpty() || confirmPasswordText.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPasswordText.equals(confirmPasswordText)) {
                Toast.makeText(ForgotPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("ForgotPassword", "Sending email: " + emailText + ", newPassword: " + newPasswordText);

            Call<ResponseData> call = apiService.resetPassword(emailText, newPasswordText);
            call.enqueue(new Callback<ResponseData>() {
                @Override
                public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseData responseData = response.body();
                        Log.d("ForgotPassword", "Response Status: " + responseData.getStatus() + ", Message: " + responseData.getMessage());
                        if ("success".equals(responseData.getStatus())) {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, responseData.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Error: Missing required fields", Toast.LENGTH_SHORT).show();
                        Log.e("ForgotPassword", "Error response: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseData> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ForgotPassword", "API Failure: " + t.getMessage());
                }
            });
        });

        backBtn.setOnClickListener(v -> finish());
    }
}
