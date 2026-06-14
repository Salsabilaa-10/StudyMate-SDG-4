# StudyMate 📚 | SDG 4: Quality Education

StudyMate is a comprehensive Android study companion designed to empower students with AI-powered learning tools and efficient academic management. Built with **Jetpack Compose**, it leverages modern Android architecture to provide a seamless, high-performance experience.

The application aligns with **SDG 4: Quality Education** by providing accessible, intelligent tools for students to organize their schedules and enhance their learning through AI.

## ✨ Features

### 🤖 AI Learning Assistant
- **Intelligent Chat:** Powered by Groq (Llama 3.3), providing instant answers to academic queries.
- **Vision Capabilities:** Upload images (diagrams, notes) for AI analysis using Llama 3.2 Vision.
- **Persistent History:** Chat sessions are saved locally using Room for future reference.

### 🃏 Smart Flashcards
- **AI Generation:** Automatically create study flashcards from your lecture notes.
- **Cloud Community:** Share your flashcard decks to the cloud and download decks shared by other students via **Firebase Firestore**.
- **Local Storage:** Manage your personal library of flashcards for offline study.

### 📅 Academic Planner
- **Assignment Tracker:** Keep track of deadlines and task status.
- **Exam Countdown:** Organize your exam schedule and stay prepared.
- **Class Timetable:** A dedicated view for your daily class schedules.
- **Local Persistence:** All data is managed through a robust Room Database.

### 🎨 Modern UI/UX
- **Material 3 Design:** Fully utilizes the latest Material Design components.
- **Dark Mode Support:** Dynamic theming for comfortable late-night study sessions.
- **Liquid Glass UI:** Custom gradients and glassmorphism effects for a premium feel.

## 🛠 Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Database:** [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- **Backend/Cloud:** [Firebase Firestore](https://firebase.google.com/docs/firestore)
- **AI Models:** Groq API (Llama 3.3 70B & Llama 3.2 11B Vision)
- **Image Processing:** CameraX & ML Kit
- **Concurrency:** Kotlin Coroutines & Flow

## 🚀 Getting Started

1. **Clone the repository:**
2. **Setup Firebase:**
   - Add your `google-services.json` to the `app/` directory.
   - Enable Firestore in your Firebase Console.
3. **API Configuration:**
   - The app uses Groq for AI features. Ensure your API key is correctly configured in `StudyMateViewModel.kt` (or move it to a secure `local.properties` in production).
4. **Build and Run:**
   - Open the project in Android Studio (Ladybug or newer).
   - Sync Gradle and run on an emulator or physical device.

## 📱 Screenshots

| Home Screen | AI Chat | Flashcards |
| :---: | :---: | :---: |
| _[Add Screenshot]_ | _[Add Screenshot]_ | _[Add Screenshot]_ |

---

**Developed by:** Salsabilaa Izwan (A207945)  
**Project:** Lab 3 Assignment - Mobile Application Development
