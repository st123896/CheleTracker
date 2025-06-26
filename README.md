# 📊 Expense Tracker App

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

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/expense-tracker-app.git
   cd expense-tracker-app
