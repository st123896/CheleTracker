POE

Overview
The Expense Tracker App is an Android application designed to help users manage their personal finances by tracking expenses, setting budgets, and generating reports. It features a clean UI with navigation drawer, multiple activity flows, and data persistence using Room Database.

Features
Core Functionality
User Authentication: Login and registration system

Expense Management: Add, view, and categorize expenses

Budget Tracking: Set monthly budget goals

Reporting: Generate monthly financial reports in PDF format

Notifications: Get notified about recent expenses

Achievements: Earn badges for financial milestones

Data Visualization: Charts for expense analysis

Technical Highlights
MVVM Architecture with ViewModel and LiveData

Room Database for local storage

Navigation components with DrawerLayout

PDF generation for reports

Camera integration for expense receipts

Notification system

MPAndroidChart for data visualization

Activities
Main Activities
LoginActivity: Handles user authentication

RegisterActivity: New user registration

HomeActivity: Main dashboard with recent transactions

AddExpenseActivity: Form for adding new expenses with photo capture

ExpenseListActivity: Displays list of all expenses

ExpenseNotificationActivity: Shows notifications for recent expenses

ReportActivity: Generates monthly financial reports

Fragments
MainActivity: Hosts various feature fragments

ExpensesFragment: Displays list of expenses with filtering options

CategoriesFragment: Manages expense categories (add/delete)

BudgetFragment: Sets and tracks monthly budget goals

GraphFragment: Visualizes expense data with charts

AchievementsFragment: Shows earned financial milestones

Adapters
ExpenseAdapter:

Displays expense items in RecyclerView

Shows amount, description, date, and category

Supports photo display for expenses with receipts

Efficient updates with DiffUtil

CategoryAdapter:

Manages category display in RecyclerView

Includes delete functionality

Uses DiffUtil for efficient list updates

AchievementAdapter:

Displays earned and locked achievements

Shows icons, titles, descriptions and earned dates

Different visual treatment for earned vs locked achievements

Setup Instructions
Prerequisites
Android Studio (latest version)

Android SDK (API level 24+)

Kotlin plugin

Installation
Clone the repository

Open the project in Android Studio

Sync Gradle dependencies

Build and run the app

Configuration
Update build.gradle files with your preferred versions

Configure file provider paths in AndroidManifest.xml if modifying file operations

Set up notification channels as needed

Add required permissions for camera and storage

Usage
Register a new account or login

Navigate using the drawer menu or bottom navigation

Add expenses with optional photos

Set budget goals in the Budget section

View reports and generate PDFs

Check achievements for financial milestones

Analyze spending patterns with charts


Dependencies
Key dependencies include:

AndroidX libraries (Navigation, Room, Lifecycle)

Material Design Components

MPAndroidChart (for graphs)

Glide (for image loading)

PDF generation libraries

Architecture
The app follows the MVVM (Model-View-ViewModel) architecture pattern with the following components:

Data Layer
Repositories: Mediate between data sources (Room Database) and ViewModels

Room Database: Local SQLite database with DAO interfaces

SharedPreferences: For simple key-value storage

Domain Layer
ViewModels: Provide data to UI and handle business logic

Use Cases: Encapsulate specific business rules (handled within ViewModels)

Presentation Layer
Activities: Manage UI components and navigation

Fragments: Reusable UI components

Adapters: Handle RecyclerView data binding

Key Components
Repositories
AchievementRepository

insert(achievement: Achievement): Stores new achievements

getAchievementsByUser(userId: Int): Retrieves user's achievements

getAchievementByType(userId: Int, type: String): Checks for specific achievement

BudgetGoalRepository

insert(goal: BudgetGoal): Stores budget goals

getBudgetGoal(userId: Int, month: Int, year: Int): Retrieves monthly budget

update(goal: BudgetGoal): Updates existing budget goals

CategoryRepository

insert(category: Category): Adds new expense categories

getCategoriesByUser(userId: Int): Gets user's categories

delete(category: Category): Removes categories

ExpenseRepository

insert(expense: Expense): Logs new expenses

Various query methods for expense retrieval by date, category, etc.

delete(expense: Expense): Removes expenses

StreakRepository

Tracks user streaks for consistent expense logging

Methods for maintaining and resetting streaks

UserRepository

Handles user authentication and registration

getUser(username: String, password: String): Validates credentials

checkUsernameExists(username: String): Verifies username availability

ViewModels
AchievementViewModel

Manages achievement data and business logic

Tracks user accomplishments and unlocks new achievements

BudgetGoalViewModel

Handles budget setting and tracking

Calculates budget progress and achievements

Integrates with ExpenseViewModel for spending analysis

CategoryViewModel

Manages expense category operations

Provides category data to UI components

ExpenseViewModel

Core expense management functionality

Handles expense creation, retrieval, and deletion

Manages notifications and streaks

Integrates with BudgetGoalViewModel for spending analysis

StreakViewModel

Tracks and maintains user streaks

Handles streak achievements and resets

UserViewModel

Manages user authentication flow

Handles registration and login processes

Database Structure
The Room Database contains the following entities:

User: Stores user credentials and preferences

Expense: Records all expense transactions

Category: Expense categorization system

BudgetGoal: Monthly budget targets

Achievement: Tracks user accomplishments

Streak: Maintains logging streaks

Key Features
Budget Management
Set monthly budget ranges (min/max)

Visual progress tracking

Budget achievement unlocks

Spending analysis against budget

Expense Tracking
Add expenses with photos

Categorize expenses

Filter by date ranges

View spending patterns

Achievements System
Streak tracking (daily logging)

Budget adherence rewards

Milestone accomplishments

Visual badge display

Notifications
Expense logging alerts

Budget threshold warnings

Achievement unlocks

Setup Instructions
Prerequisites
Android Studio (latest version)

Android SDK (API level 24+)

Kotlin plugin

Installation
Clone the repository

Open the project in Android Studio

Sync Gradle dependencies

Build and run the app

Configuration
Update build.gradle files with your preferred versions

Configure file provider paths in AndroidManifest.xml

Set up notification channels as needed

Add required permissions for camera and storage

Dependencies
Key dependencies include:

AndroidX libraries (Navigation, Room, Lifecycle)

Material Design Components

MPAndroidChart (for graphs)

Glide (for image loading)

PDF generation libraries

Contributing
Fork the repository

Create a feature branch

Commit your changes

Push to the branch

Create a Pull Request

License
This project is licensed under the MIT License - see the LICENSE file for details.
