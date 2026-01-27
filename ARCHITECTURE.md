# Architecture Documentation - Chat App

## Overview
This application follows **Clean Architecture** principles with a modularized structure to ensure scalability, maintainability, and testability. The UI is built using **Jetpack Compose** with a unidirectional data flow (UDF) pattern.

## Modular Structure
The project is divided into several Gradle modules:

- **`:app`**: The entry point of the application. Handles Hilt dependency injection wiring and navigation.
- **`:core:domain`**: Contains the core business logic, including Domain Models, Repository Interfaces, and Use Cases. This module has no dependencies on Android or external libraries (except for Coroutines/Flow).
- **`:core:data`**: Implementation of repository interfaces. Manages data sources like Firebase Realtime Database, Firebase Storage, and DataStore for local persistence.
- **`:core:ui`**: Shared UI components and the Design System (Theme, Colors, Typography).
- **`:feature:auth_identity`**: Handles user onboarding and username setting.
- **`:feature:chat_room`**: The main chat screen, including message listing, real-time updates, and media picking.
- **`:feature:work`**: Background processing implementation using **WorkManager**. Handles reliable message sending and media uploading.

## Key Decisions

### 1. Unidirectional Data Flow (UDF)
Each feature uses a `ViewModel` that exposes a single `StateFlow<UiState>`. The UI observes this state and sends events back to the ViewModel. This ensures a predictable UI state and easier debugging.

### 2. Reliable Messaging with WorkManager
To meet the requirement of surviving app restarts and maintaining robust connections, all outgoing messages are queued via **WorkManager**. 
- `UploadMediaWorker`: Handles concurrent/sequential media uploads to Firebase Storage.
- `SendMessageWorker`: Finalizes the message by updating the Realtime Database.
Status updates (`SENDING`, `SENT`, `FAILED`) are reflected in the UI in real-time by observing the database.

### 3. Real-time Updates with Flows
Firebase Realtime Database listeners are wrapped in `callbackFlow`, allowing the domain and UI layers to consume message streams as standard Kotlin Flows.

### 4. Modular DI with Hilt
Hilt is used for dependency injection across all modules. `IWorkScheduler` interface in `core:domain` allows the `core:data` layer to trigger background work without having a direct dependency on the `feature:work` module (preventing circular dependencies).

### 5. Premium UI/UX
- **Animations**: Typing indicators use `AnimatedVisibility` and `animateContentSize`.
- **Aesthetics**: Custom color palette with soft gradients, asymmetrical bubble shapes, and soft shadows to provide a premium feel.
- **Media Support**: Modern Photo Picker is used for granular permissions, avoiding the need for broad storage access.

### 6. Scoped Storage & File Correctness
To ensure robust media handling without persistent storage permissions:
- **Copy-First Strategy**: Selected media from the Photo Picker is immediately copied to the app's internal private cache.
- **Worker Isolation**: Background workers operate on these internal files, guaranteeing access even if the app is killed or original permissions expire.

### 7. Android 14+ Compliance
- **Foreground Services**: Uploads use `FOREGROUND_SERVICE_TYPE_DATA_SYNC` to comply with stricter background execution limits on newer Android versions.

## Future Improvements
- Local caching of messages using Room to support offline reading.
- End-to-end encryption for private messages.
- Push notifications using Firebase Cloud Messaging (FCM).
