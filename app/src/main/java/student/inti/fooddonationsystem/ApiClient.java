package student.inti.fooddonationsystem;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;


public class ApiClient {
    // Base URL of the API
    private static final String BASE_URL = "http://10.0.2.2/fooddonation/";

    // Singleton instance of Retrofit
    private static Retrofit retrofit = null;

    // Method to get Retrofit client instance
    public static Retrofit getClient() {
        if (retrofit == null) {

            // Enable logging for Retrofit (logs request and response body for debugging)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Use BODY for full logs, NONE for production

            // Build OkHttpClient with logging interceptor
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Create a custom Gson instance with lenient mode for handling non-standard JSON
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Build the Retrofit instance with base URL, client, and Gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
