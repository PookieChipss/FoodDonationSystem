# 🍴 Food Donation System (WasteNot)

A mobile application built for Android to enable users to donate food and take away donated food. The app also allows users to view their food donation history and manage food details.

---

## 🚀 Features

### 🔑 **User Registration and Login**
- Allows users to sign up and log in to their accounts.

### 🍽️ **Food Donation**
- Users can donate food by entering food details (food name, expiry date, category, etc.).

### 🍔 **Takeaway Food**
- Users can view available donated food and take it away.

### 📜 **Food Donation History**
- Users can view a history of the food they’ve donated and received.

### 🔍 **Search and Filter**
- Users can search for food and filter by categories in History.

### ✏️ **Edit Food Details**
- Users can edit the details of the food they have donated.

### 🔑 **Forgot Password**
- Users can reset their password if they forget it.

---

## 🛠️ Technologies Used

- **Android SDK**: Core Android development tools.
- **Retrofit**: For API calls.
- **Gson**: For parsing JSON data from the backend.
- **OkHttp**: Manages network requests and responses.
- **AppCompat**: Ensures backward compatibility for modern Android features.
- **phpmyAdmin Database**: Used for storing local data (if applicable).
- **Java**: Main programming language used for the app.

---

## 📦 Setup Instructions

### Backend Setup (PHP and MySQL)

1. Set up your **PHP server** and **MySQL database** using **XAMPP** or any other server.
2. Create the necessary database and tables for users, food donations, and history.
3. Implement the required PHP API endpoints like:
   - **`register.php`**: Handles user registration.
   - **`login.php`**: Handles user login.
   - **`add_food.php`**: Handles food donation submission.
   - **`get_user_history.php`**: Retrieves the user's donation and takeaway history.
   - **`delete_food.php`**: Deletes a food donation entry.
   - **`update_food.php`**: Updates the details of a food donation.
   - **`reset_password.php`**: Resets a user's password.
   - **`take_away_food.php`**: Allows users to take away food.

