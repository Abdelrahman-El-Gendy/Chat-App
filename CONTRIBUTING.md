# Contributing to Chat App

Thank you for your interest in contributing! This document provides guidelines for contributing to this project.

## Git Flow Workflow

This project follows the **Git Flow** branching model for a structured and organized development workflow.

### Branch Structure

```
main (or master)  ─────────────────────────────────► Production-ready code
     │
     └── develop  ─────────────────────────────────► Integration branch
           │
           ├── feature/feature-name ───────────────► New features
           │
           ├── bugfix/bug-description ─────────────► Bug fixes  
           │
           ├── release/vX.Y.Z ─────────────────────► Release preparation
           │
           └── hotfix/urgent-fix ──────────────────► Production hotfixes
```

### Main Branches

| Branch | Purpose |
|--------|---------|
| `main` | Production-ready code. Protected branch. |
| `develop` | Integration branch for features. All feature branches merge here. |

### Supporting Branches

| Branch Type | Naming Convention | Base | Merges Into |
|-------------|-------------------|------|-------------|
| Feature | `feature/<name>` | `develop` | `develop` |
| Bugfix | `bugfix/<name>` | `develop` | `develop` |
| Release | `release/vX.Y.Z` | `develop` | `main` & `develop` |
| Hotfix | `hotfix/<name>` | `main` | `main` & `develop` |

---

## Development Workflow

### 1. Starting a New Feature

```bash
# Ensure you're on develop and it's up to date
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/my-new-feature

# Work on your feature...
# Commit changes regularly with meaningful messages
git add .
git commit -m "feat(scope): add feature description"

# Push to remote
git push -u origin feature/my-new-feature
```

### 2. Completing a Feature

```bash
# Update develop branch
git checkout develop
git pull origin develop

# Merge feature branch
git merge feature/my-new-feature

# Push to remote
git push origin develop

# Delete feature branch
git branch -d feature/my-new-feature
git push origin --delete feature/my-new-feature
```

**Or create a Pull Request** (recommended):
1. Push your feature branch
2. Create a PR from `feature/my-new-feature` → `develop`
3. Request code review
4. Merge after approval

### 3. Creating a Release

```bash
# Create release branch from develop
git checkout develop
git checkout -b release/v1.0.0

# Update version numbers, changelog, etc.
# Test thoroughly

# Merge into main
git checkout main
git merge release/v1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin main --tags

# Merge back into develop
git checkout develop
git merge release/v1.0.0
git push origin develop

# Delete release branch
git branch -d release/v1.0.0
```

### 4. Hotfix for Production

```bash
# Create hotfix from main
git checkout main
git checkout -b hotfix/critical-bug-fix

# Fix the bug
git commit -m "fix: resolve critical issue"

# Merge into main
git checkout main
git merge hotfix/critical-bug-fix
git tag -a v1.0.1 -m "Hotfix release"
git push origin main --tags

# Merge into develop
git checkout develop
git merge hotfix/critical-bug-fix
git push origin develop

# Delete hotfix branch
git branch -d hotfix/critical-bug-fix
```

---

## Commit Message Guidelines

We follow **Conventional Commits** specification:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code style changes (formatting, etc.) |
| `refactor` | Code refactoring |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks |
| `perf` | Performance improvements |
| `ci` | CI/CD changes |

### Scopes (optional)

- `domain` - Core domain layer changes
- `data` - Data layer changes
- `ui` - UI components
- `chat` - Chat feature
- `auth` - Authentication feature
- `work` - Background workers
- `deps` - Dependency updates

### Examples

```
feat(chat): add message pagination support

fix(work): resolve upload worker retry logic

docs: update architecture documentation

test(domain): add unit tests for SendMessageUseCase

refactor(data): extract Firebase operations to service class
```

---

## Code Review Guidelines

### Before Submitting a PR

1. **Run Tests**: Ensure all tests pass
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

2. **Code Style**: Follow Kotlin coding conventions
   ```bash
   ./gradlew ktlintCheck
   ```

3. **Build**: Ensure the project builds successfully
   ```bash
   ./gradlew assembleDebug
   ```

### PR Checklist

- [ ] Code follows project architecture (Clean Architecture + UDF)
- [ ] Unit tests added for new functionality
- [ ] UI tests added for new screens/components
- [ ] Documentation updated if needed
- [ ] Commit messages follow convention
- [ ] No merge conflicts with develop

### Review Process

1. Submit PR with clear description
2. Request review from team members
3. Address feedback and update
4. Get approval from at least one reviewer
5. Squash and merge (if multiple commits)

---

## Project Setup

### Prerequisites

- Android Studio Iguana or newer
- JDK 17
- Firebase project configured

### Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/Abdelrahman-El-Gendy/Chat-App.git
   cd Chat-App
   ```

2. Set up Git Flow
   ```bash
   # If not exists, create develop branch
   git checkout -b develop
   git push -u origin develop
   ```

3. Configure Firebase
   - Create Firebase project
   - Download `google-services.json`
   - Place in `app/` directory

4. Build and run
   ```bash
   ./gradlew assembleDebug
   ```

---

## Questions?

Feel free to open an issue for:
- Bug reports
- Feature requests
- Questions about the codebase

Thank you for contributing!
