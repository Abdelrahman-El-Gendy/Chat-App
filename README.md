# 📱 Modern Android Chat Application

A high-performance, real-time chat application built with **Modern Android Development (MAD)** practices. This project demonstrates a production-grade architecture focusing on modularity, scalability, and strict adherence to the latest Android privacy and background execution standards (Android 14+).

---

## ✨ Key Features

*   **⚡ Real-Time Messaging**: Instant message delivery using Firebase Realtime Database with reactive UI updates.
*   **📸 Media Sharing**: Seamlessly share images and videos.
    *   **Background Uploads**: Uses `WorkManager` for reliable uploads effectively handling app kills and network changes.
    *   **Foreground Services**: Compliant with Android 14 `FOREGROUND_SERVICE_TYPE_DATA_SYNC` requirements.
*   **🔒 Privacy First**:
    *   **Scoped Storage**: Zero storage permissions requested. Uses the implementation of `ActivityResultContracts.PickVisualMedia` (Photo Picker) for secure, user-initiated file access.
    *   **Secure File Handling**: Internal caching mechanism prevents "File Not Found" errors if permissions expire.
*   **🎨 Material 3 UI**: Beautiful, adaptive UI built 100% with Jetpack Compose featuring dynamic animations and dark/light mode support.
*   **🛠️ Robust Error Handling**: Automatic retry policies for failed messages and uploads using exponential backoff.

---

## 🛠️ Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/) (100%)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Architecture**:
    *   **MVVM** (Model-View-ViewModel) with Unidirectional Data Flow (UDF).
    *   **Clean Architecture**: Separation of concerns into UI, Domain, and Data layers.
    *   **Multi-Module**: Feature-based modularization (`core`, `feature:chat`, `feature:auth`, etc.).
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
*   **Asynchrony**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Background Work**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
*   **Backend / Cloud**:
    *   **Firebase Realtime Database**: For message syncing.
    *   **Firebase Storage**: For media assets.
    *   **Firebase Authentication**: For user identity.

---

## 📂 Project Structure

The project follows a modular structure to separate concerns and improve build times:

```
├── app/                  # Main application module (DI setup, Navigation host)
├── core/
│   ├── data/             # Repository implementations, API sources, Database
│   ├── domain/           # UseCases, Repository Interfaces, Models (Pure Kotlin)
│   └── ui/               # Shared UI components, Theme, Design System
├── feature/
│   ├── auth_identity/    # Authentication screens and logic
│   ├── chat_room/        # Main chat screen, message list, input handling
│   ├── media/            # Media selection and preview logic
│   └── work/             # Background workers for uploading/sending
```

For a deeper dive into the architectural decisions, check out [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Iguana or newer.
*   JDK 17.
*   A Firebase Project.

### Setup Instructions

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Abdelrahman-El-Gendy/Chat-App.git
    cd Chat-App
    ```

2.  **Firebase Configuration**:
    *   Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    *   Add an Android App with package name: `com.example.chatapp`.
    *   Download the `google-services.json` file.
    *   Place it in the `app/` directory: `app/google-services.json`.

3.  **Enable Firebase Services**:
    *   **Authentication**: Enable Email/Password or Anonymous auth.
    *   **Realtime Database**: Create a database (US Central recommended) and set rules.
    *   **Storage**: Enable Storage and set rules.

4.  **Build and Run**:
    *   Open the project in Android Studio.
    *   Sync Gradle.
    *   Select the `app` run configuration and run on an Emulator or Physical Device (Android 10+ recommended).

---

## 🛡️ Permissions & Privacy

This app is designed to be a "good citizen" on the Android platform:

| Permission | Usage | Reason |
| :--- | :--- | :--- |
| `INTERNET` | Network Access | Required for Firebase. |
| `POST_NOTIFICATIONS` | Notifications | Showing upload progress. |
| `FOREGROUND_SERVICE` | Background Work | ensuring reliable uploads. |
| `READ_EXTERNAL_STORAGE` | ❌ **NOT USED** | Replaced by System Photo Picker. |

---

## 🤝 Contribution

Contributions are welcome! Please fork the repository and submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request
