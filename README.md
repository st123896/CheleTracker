# 📊 Chelete Tracker App

The **Expense Tracker App** is an Android application designed to help users manage personal finances by tracking expenses, setting monthly budgets, viewing visual spending charts, and generating reports. Built with a clean UI and powerful features, it uses Room Database for data persistence and follows the MVVM architecture.

---

## 🚀 Features

- 🔐 **User Authentication** – Login and register securely  
- 💸 **Expense Management** – Add, view, and categorize expenses with photo receipts  
- 🎯 **Budget Tracking** – Set and monitor monthly budget goals  
- 📈 **Data Visualization** – Analyze expenses using charts (MPAndroidChart)  
- 🏆 **Achievements System** – Unlock badges for financial milestones and logging streaks  
- 📄 **Report Generation** – Export monthly financial reports as PDFs  
- 🔔 **Notifications** – Alerts for new expenses, budget warnings, and achievement unlocks  

---

## 🏗️ Architecture

- **MVVM Pattern** – Clean separation between UI, business logic, and data
- **Room Database** – Local SQLite-based data storage
- **ViewModel + LiveData** – Efficient UI state handling
- **Repository Pattern** – Manages all data operations and business logic

---

## 📱 Activities

| Activity | Description |
|----------|-------------|
| `LoginActivity` | Handles user login |
| `RegisterActivity` | New user registration |
| `HomeActivity` | Main dashboard with recent transactions |
| `AddExpenseActivity` | Add expenses with optional photo capture |
| `ExpenseListActivity` | View all recorded expenses |
| `ExpenseNotificationActivity` | Shows alerts related to expenses |
| `ReportActivity` | Generate and view PDF reports |

---

## 🔄 Fragments

- `MainActivity` – Hosts all feature fragments via Navigation Drawer
- `ExpensesFragment` – List, filter, and manage expenses
- `CategoriesFragment` – Add and remove expense categories
- `BudgetFragment` – Set and track monthly budget
- `GraphFragment` – Visualize expense trends using MPAndroidChart
- `AchievementsFragment` – View unlocked and locked achievements

---

## 🔧 Adapters

- **`ExpenseAdapter`** – RecyclerView display for expenses (uses DiffUtil, supports receipt image)
- **`CategoryAdapter`** – Displays and manages categories
- **`AchievementAdapter`** – Lists unlocked and locked achievements

---

## 🧱 Room Entities

- `User` – Stores login credentials
- `Expense` – Records transactions
- `Category` – Stores custom expense categories
- `BudgetGoal` – Tracks monthly spending goals
- `Achievement` – Stores earned achievements
- `Streak` – Daily logging consistency

---

## 📦 ViewModels & Repositories

| Component | Responsibilities |
|----------|------------------|
| `UserViewModel` / `UserRepository` | Login, registration, and user data |
| `ExpenseViewModel` / `ExpenseRepository` | Add, retrieve, and delete expenses |
| `BudgetGoalViewModel` / `BudgetGoalRepository` | Manage budgets and track progress |
| `AchievementViewModel` / `AchievementRepository` | Track, unlock, and display badges |
| `CategoryViewModel` / `CategoryRepository` | Add/delete user categories |
| `StreakViewModel` / `StreakRepository` | Handle user logging streaks |

---

## 🛠️ Setup Instructions

### ✅ Prerequisites
- Android Studio (Latest version)
- Kotlin Plugin
- Android SDK API level 24+

### 📦 Installation

1. **Clone the repository:** https://github.com/st123896/CheleTracker.git
   ```bash
 


  Open in Android Studio

Sync Gradle dependencies

Build and run the app

⚙️ Configuration
Update build.gradle versions as needed

Configure file provider paths in AndroidManifest.xml

Add required permissions for camera and storage

Set up notification channels for alerts
========================================================
🔌 Dependencies
AndroidX Navigation

Room Persistence Library

MPAndroidChart – Charting library

Glide – Image loading

PDF Generation Libraries

Material Components







-------------------------------------------------------------
📚 Usage
-Register or Login

-Use the Navigation Drawer to switch between screens

-Add expenses with categories and optional receipts

-Set and monitor your monthly budget

-Track achievements and view spending reports

-Export reports as PDFs and review progress charts

========================================================






📝 License
-This project is licensed under the MIT License – see the LICENSE file for details.



-------------------------------------------------------------------------------------------


🙌 Acknowledgements
-MPAndroidChart by PhilJay
-----------------------------------------------------------------------------------------
-Glide by Bumptech

-Android Jetpack & Material Design Components
====================================================================================
References:

-This project adapts some code from this source:

-Author: Mkr Developer

-Source:https://youtu.be/i8mObAOJaRQ?si=gyDZXVf9mpvTM0Rv

-Source :Head First Android Development A Brain-Friendly Guide, 2nd Edition


-PhilJay. (2024). MPAndroidChart - Powerful & easy to use chart library for Android. GitHub. Available at: https://github.com/PhilJay/MPAndroidChart
------------------------------------------------------------------------------
Author

-Your Name - Tshepiso Zikhali


my video presentation YT Link : https://youtube.com/shorts/FdEmFjQUKus?si=5qq95X0dzQEb-_Wj

----------------------------------------------------------------------------------------

## 📦 APK Build Instructions

To generate the APK or App Bundle for the Expense Tracker App in Android Studio:

### 🔨 Steps

1. Open the project in **Android Studio**.
2. Navigate to the top menu and click on:

============================================================
Build > Generate Signed Bundle / APK..

3. In the dialog, choose one of the following:
- **Android App Bundle** (.aab) — for Google Play Store
- **APK** (.apk) — for direct installation or testing
4. Click **Next** and fill in the **Key Store** credentials:
- Key store path
- Key alias
- Password
5. Select the **release** build variant and click **Finish**.
6. Wait for the build to complete. You’ll find the output in:

-------------------------------------
/app/build/outputs/apk/release/
==================================================

![APK Build Screenshot](https://github.com/user-attachments/assets/363bb7c0-984c-453f-ba7d-979fc4532b12)











