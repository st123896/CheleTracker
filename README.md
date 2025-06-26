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
