# Setting Up a Self-Hosted GitHub Actions Runner

This guide walks you through setting up a self-hosted runner that can execute Claude Code in your GitHub Actions workflows.

## Prerequisites

- A machine (Linux/macOS) with:
  - 4GB+ RAM
  - 20GB+ free disk space
  - Internet access
- A GitHub repository (this one)
- An Anthropic API key

## Step 1: Install Required Software

### Git
```bash
# macOS
brew install git

# Ubuntu/Debian
sudo apt-get install git
```

### GitHub CLI
```bash
# macOS
brew install gh

# Ubuntu/Debian
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
sudo apt update && sudo apt install gh
```

Then authenticate:
```bash
gh auth login
```

### JDK 17
```bash
# macOS
brew install openjdk@17

# Ubuntu/Debian
sudo apt-get install openjdk-17-jdk
```

### Android SDK (command-line tools)
```bash
# Download command-line tools from https://developer.android.com/studio#command-line-tools-only
# Extract to ~/android-sdk/cmdline-tools/latest/

# Set environment variables (add to ~/.bashrc or ~/.zshrc)
export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses and install required packages
sdkmanager --licenses
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
```

### Node.js 18+
```bash
# Using nvm (recommended)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
nvm install 18
nvm use 18
```

### Claude Code CLI
```bash
npm install -g @anthropic-ai/claude-code
```

Verify installation:
```bash
claude --version
```

## Step 2: Configure the GitHub Actions Runner

1. Go to your repository on GitHub
2. Navigate to **Settings > Actions > Runners**
3. Click **New self-hosted runner**
4. Select your OS and architecture
5. Follow the download and configuration instructions:

```bash
# Example for Linux x64
mkdir actions-runner && cd actions-runner
curl -o actions-runner-linux-x64-2.319.1.tar.gz -L https://github.com/actions/runner/releases/download/v2.319.1/actions-runner-linux-x64-2.319.1.tar.gz
tar xzf ./actions-runner-linux-x64-2.319.1.tar.gz

# Configure
./config.sh --url https://github.com/YOUR_USER/YOUR_REPO --token YOUR_TOKEN

# Install as service (Linux)
sudo ./svc.sh install
sudo ./svc.sh start
```

For macOS:
```bash
# After config.sh, install as LaunchAgent
./svc.sh install
./svc.sh start
```

## Step 3: Set Up Secrets

1. Go to **Settings > Secrets and variables > Actions**
2. Add a new repository secret:
   - Name: `ANTHROPIC_API_KEY`
   - Value: your Anthropic API key

The `GITHUB_TOKEN` is automatically provided by GitHub Actions.

## Step 4: Environment Variables on the Runner

Ensure these are available in the runner's environment (add to `~/.bashrc`, `~/.zshrc`, or the runner's `.env` file):

```bash
export ANDROID_HOME=$HOME/android-sdk
export JAVA_HOME=$(/usr/libexec/java_home -v 17)  # macOS
# export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  # Linux
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$JAVA_HOME/bin
```

## Step 5: Test the Setup

1. Create a test branch:
   ```bash
   git checkout -b test/runner-check
   echo "// test" >> app/src/main/java/com/example/todoapp/TodoApp.kt
   git add . && git commit -m "test: verify runner setup"
   git push -u origin test/runner-check
   ```

2. Open a Pull Request on GitHub

3. Check the **Actions** tab -- you should see the "Claude Code Review" workflow running

4. After review completes, try commenting `@claude fix the formatting` on the PR

## Security Considerations

- **Private repository recommended** -- prevents unauthorized workflow triggers from forks
- **`--allowedTools` flag** -- limits what Claude can do in each step (read-only review vs. write access)
- **Timeouts** -- all workflows have `timeout-minutes` to prevent runaway processes
- **API spending caps** -- set usage limits in your Anthropic dashboard
- **Runner isolation** -- consider running the runner in a VM or container for additional isolation
- **Secret rotation** -- rotate your `ANTHROPIC_API_KEY` periodically
