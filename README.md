# Android TODO App + GitHub Actions with Claude Code

A sample Android TODO application demonstrating how to use **Claude Code** on a **self-hosted GitHub Actions runner** for automated code review, issue creation, and PR comment handling.

## Workflow Architecture

```
+-------------------------------------------------------------+
|                     GitHub Repository                        |
+-------------------------------------------------------------+
|                                                              |
|  PR Opened -------> claude-code-review.yml                   |
|                     +- Step 1: Review (read-only)            |
|                     +- Step 2: Auto-fix (formatting/typos)   |
|                                                              |
|  @claude comment -> claude-comment.yml                       |
|                     +- Implement request, commit, push       |
|                                                              |
|  Manual trigger --> claude-create-issues.yml                  |
|                     +- Break feature into GitHub issues       |
|                                                              |
+-------------------------------------------------------------+
|                   Self-Hosted Runner                          |
|         (JDK 17 + Android SDK + Claude Code CLI)             |
+-------------------------------------------------------------+
```

## Quick Start

1. **Clone the repo**
   ```bash
   git clone https://github.com/YOUR_USER/github-runner-tutorial.git
   cd github-runner-tutorial
   ```

2. **Build the app**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run tests**
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. **Set up the runner** -- follow [docs/SETUP-RUNNER.md](docs/SETUP-RUNNER.md)

## Workflows

| Workflow | Trigger | What it does |
|----------|---------|-------------|
| `claude-code-review.yml` | PR opened/updated | Reviews code, posts comments, auto-fixes formatting |
| `claude-comment.yml` | `@claude` in PR comment | Implements requested changes, commits, pushes |
| `claude-create-issues.yml` | Manual (workflow_dispatch) | Breaks a feature description into GitHub issues |

## Tech Stack

- **Kotlin 2.0** with **Jetpack Compose** (Material 3)
- **Room** database with KSP
- **MVVM** architecture with manual DI
- **GitHub Actions** with self-hosted runner
- **Claude Code** CLI for AI-powered automation

## Project Structure

```
├── .github/workflows/     # GitHub Actions workflow files
├── docs/                  # Setup documentation
├── app/src/main/          # Android application source
│   └── java/.../todoapp/
│       ├── data/          # Room entities, DAO, database, repository
│       ├── di/            # Manual dependency injection
│       └── ui/            # Compose UI + ViewModel
├── CLAUDE.md              # Project context for Claude Code
└── README.md              # This file
```

## License

This project is for educational purposes.
