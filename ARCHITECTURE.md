# Architecture Documentation - Chat App

## Overview
This application follows **Clean Architecture** principles with a modularized structure to ensure scalability, maintainability, and testability. The UI is built using **Jetpack Compose** with a unidirectional data flow (UDF) pattern.

---

## Modular Structure
The project is divided into several Gradle modules:

```
├── app/                    # Main application module (DI setup, Navigation)
├── core/
│   ├── domain/             # Pure Kotlin: Models, Repository Interfaces, Use Cases
│   ├── data/               # Repository implementations, Firebase services
│   └── ui/                 # Shared UI components, Theme, Design System
├── feature/
│   ├── auth_identity/      # User onboarding and username management
│   ├── chat_room/          # Main chat UI, message list, media picking
│   └── work/               # Background workers (WorkManager)
```

### Module Dependencies
```
app ──► feature:* ──► core:domain
         │              │
         ▼              ▼
    core:data ◄─── core:ui
```

- **`:core:domain`**: Pure Kotlin module with no Android dependencies (except Coroutines/Flow). Contains domain models, repository interfaces, and use cases.
- **`:core:data`**: Implements repository interfaces. Manages Firebase Realtime Database, Firebase Storage, and DataStore.
- **`:core:ui`**: Shared composables, design tokens (Theme, Colors, Typography).
- **`:feature:*`**: Feature-specific screens and ViewModels.

---

## Clean Architecture Layers

### 1. Domain Layer (`core:domain`)
The innermost layer containing business logic with zero external dependencies.

**Components:**
- **Models**: `Message`, `MessageStatus`, `MessageState`, `User`
- **Repository Interfaces**: `IMessageRepository`, `IUserRepository`, `IWorkScheduler`
- **Use Cases**: Single-responsibility classes that encapsulate business operations
  - `SendMessageUseCase` - Queue messages for sending
  - `GetMessagesUseCase` - Retrieve and observe messages
  - `DeleteMessageUseCase` - Remove messages
  - `GetTypingUsersUseCase` - Observe typing indicators
  - `SetTypingStatusUseCase` - Update typing status

### 2. Data Layer (`core:data`)
Implements repository interfaces and manages data sources.

**Components:**
- **Repositories**: `MessageRepository`, `UserRepository`
- **Remote Services**: `FirebaseMessageService`, `FirebaseUserService`
- **Local Storage**: `DataStoreManager`
- **Utilities**: `ContentUriCopier` (handles scoped storage)

### 3. Presentation Layer (`feature:*`)
Feature modules containing UI components and ViewModels.

**Pattern**: MVVM with Unidirectional Data Flow (UDF)
- ViewModels expose `StateFlow<UiState>`
- UI observes state and sends events/intents to ViewModel
- State is immutable and only modified through ViewModel

---

## Key Architectural Decisions

### 1. Unidirectional Data Flow (UDF)
```
┌──────────┐     Events      ┌───────────┐
│    UI    │ ───────────────► │ ViewModel │
│ (Compose)│                  │           │
└──────────┘ ◄─────────────── └───────────┘
               StateFlow
```

Each feature ViewModel:
- Exposes a single `StateFlow<UiState>` for UI state
- Processes user intents through public functions
- Delegates business logic to Use Cases
- Handles errors gracefully with state updates

**Example: ChatUiState**
```kotlin
data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginatedLoading: Boolean = false,
    val error: String? = null,
    val currentUser: String = "",
    val currentUserName: String = "",
    val hasMoreMessages: Boolean = true,
    val typingUsers: List<String> = emptyList()
)
```

### 2. Reliable Messaging with WorkManager
Messages survive app restarts and network changes through WorkManager:

```
User sends message
       │
       ▼
┌─────────────────────┐
│ MessageRepository   │
│ queueMessage()      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐     ┌─────────────────────┐
│ UploadMediaWorker   │ ──► │ SendMessageWorker   │
│ (if has media)      │     │                     │
└─────────────────────┘     └─────────────────────┘
```

**Worker Features:**
- Automatic retry with exponential backoff (up to 3 attempts)
- Network connectivity constraints
- Foreground service notifications for long-running uploads
- Status updates reflected in UI (`SENDING` → `SENT` / `FAILED`)

### 3. Real-time Updates with Flows
Firebase listeners wrapped in `callbackFlow`:

```kotlin
fun getMessages(): Flow<List<Message>> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySend(messages)
        }
        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    query.addValueEventListener(listener)
    awaitClose { query.removeEventListener(listener) }
}
```

### 4. Dependency Injection with Hilt
- Modules organized by layer/feature
- `IWorkScheduler` interface in `core:domain` prevents circular dependencies
- `WorkSchedulerImpl` in `feature:work` implements the interface

### 5. Scoped Storage & File Handling
```
Photo Picker ──► Content URI ──► Copy to Internal ──► Worker Access
                                    Storage              (guaranteed)
```

- No storage permissions required (uses system Photo Picker)
- Files copied to app's private cache before background processing
- Workers operate on internal files, avoiding permission issues

### 6. Android 14+ Compliance
- Foreground services use `FOREGROUND_SERVICE_TYPE_DATA_SYNC`
- Proper notification channels for upload progress
- No deprecated permission requests

---

## Error Handling Strategy

### UI Layer
- `UiState.error` property for displaying error messages
- Graceful degradation (show cached data when available)

### Data Layer
- Repository catches and transforms exceptions
- Meaningful error messages for common failures

### Background Workers
- Retry policy: up to 3 attempts with backoff
- Failed messages marked with `MessageStatus.FAILED`
- Users can retry failed messages from UI

---

## Testing Strategy

### Unit Tests
Located in `src/test/java` directories:

| Module | Tests |
|--------|-------|
| `core:domain` | Use Case tests |
| `core:data` | Repository tests |
| `feature:work` | Worker tests |
| `feature:chat_room` | ViewModel tests |

**Tools Used:**
- JUnit 4
- MockK for mocking
- Turbine for Flow testing
- kotlinx-coroutines-test

### Instrumentation Tests
Located in `src/androidTest/java` directories:

- **UI Tests**: Compose UI testing with `createComposeRule`
- **Integration Tests**: WorkManager testing with `work-testing`

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest
```

---

## Git Flow Workflow

This project follows Git Flow:

```
main ────────────────────────────────────────────► (production releases)
  │
  └── develop ────────────���───────────────────────► (integration branch)
        │
        ├── feature/feature-name ─────────────────► (new features)
        │
        ├── bugfix/bug-description ───────────────► (bug fixes)
        │
        └── release/v1.0.0 ───────────────────────► (release candidates)
```

### Branch Naming
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `release/*` - Release preparation
- `hotfix/*` - Production hotfixes

### Commit Message Format
```
type(scope): description

[optional body]

[optional footer]
```

Types: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`

---

## Scalability Considerations

### Adding New Features
1. Create feature module under `feature/`
2. Define interfaces in `core:domain`
3. Implement data sources in `core:data`
4. Add DI bindings in `app` module

### Performance Optimizations
- Pagination for message lists
- Image caching with Coil
- Lazy loading in Compose lists
- Background processing for heavy operations

### Future Extensions Ready
- **Offline Support**: Add Room database in `core:data`
- **Push Notifications**: Integrate FCM in `feature:work`
- **Multiple Channels**: Extend message model with channel ID
- **E2E Encryption**: Add encryption layer in `core:data`

---

## Future Improvements
- Local caching of messages using Room for offline reading
- End-to-end encryption for private messages
- Push notifications using Firebase Cloud Messaging (FCM)
- Message search functionality
- Read receipts and delivery status
- File/document sharing beyond images/videos
