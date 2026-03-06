#!/bin/bash
# Validation script for self-hosted GitHub Actions runner setup
# Checks all prerequisites needed for Claude Code workflows

set -uo pipefail

PASS=0
FAIL=0
WARN=0

pass() { echo "  [PASS] $1"; ((PASS++)); }
fail() { echo "  [FAIL] $1"; ((FAIL++)); }
warn() { echo "  [WARN] $1"; ((WARN++)); }

echo "=== Self-Hosted Runner Validation ==="
echo ""

# --- Git ---
echo "1. Git"
if command -v git &>/dev/null; then
    pass "git is installed ($(git --version | cut -d' ' -f3))"
else
    fail "git is not installed"
    echo "       Install: brew install git"
fi

if git config --global user.name &>/dev/null && [ -n "$(git config --global user.name)" ]; then
    pass "git user.name = $(git config --global user.name)"
else
    fail "git user.name not configured"
    echo "       Run: git config --global user.name \"Your Name\""
fi

if git config --global user.email &>/dev/null && [ -n "$(git config --global user.email)" ]; then
    pass "git user.email = $(git config --global user.email)"
else
    fail "git user.email not configured"
    echo "       Run: git config --global user.email \"your@email.com\""
fi
echo ""

# --- GitHub CLI ---
echo "2. GitHub CLI"
if command -v gh &>/dev/null; then
    pass "gh is installed ($(gh --version | head -1 | cut -d' ' -f3))"
else
    fail "gh is not installed"
    echo "       Install: brew install gh"
fi

if gh auth status &>/dev/null 2>&1; then
    pass "gh is authenticated"
else
    fail "gh is not authenticated"
    echo "       Run: gh auth login"
fi
echo ""

# --- Node.js ---
echo "3. Node.js"
if command -v node &>/dev/null; then
    NODE_VER=$(node --version)
    NODE_MAJOR=$(echo "$NODE_VER" | sed 's/v//' | cut -d. -f1)
    if [ "$NODE_MAJOR" -ge 18 ]; then
        pass "node is installed ($NODE_VER)"
    else
        warn "node version $NODE_VER may be too old (recommend v18+)"
        echo "       Update: brew install node"
    fi
else
    fail "node is not installed (required by Claude Code)"
    echo "       Install: brew install node"
fi
echo ""

# --- Claude Code ---
echo "4. Claude Code"
if command -v claude &>/dev/null; then
    pass "claude is installed"
else
    fail "claude is not installed"
    echo "       Install: npm install -g @anthropic-ai/claude-code"
fi
echo ""

# --- Runner ---
echo "5. GitHub Actions Runner"
RUNNER_DIR="$HOME/Projects/learn/actions-runner"
if [ -d "$RUNNER_DIR" ]; then
    pass "Runner directory exists ($RUNNER_DIR)"
else
    warn "Runner directory not found at $RUNNER_DIR"
    echo "       Download from: https://github.com/<org>/<repo>/settings/actions/runners/new"
fi

if [ -f "$RUNNER_DIR/.runner" ]; then
    pass "Runner is configured"
else
    fail "Runner is not configured"
    echo "       Run: cd $RUNNER_DIR && ./config.sh --url <repo-url> --token <token>"
fi

# Check if runner service/process is active
if pgrep -f "Runner.Listener" &>/dev/null; then
    pass "Runner process is running"
else
    warn "Runner process is not running"
    echo "       Start: cd $RUNNER_DIR && ./run.sh"
    echo "       Or as service: cd $RUNNER_DIR && sudo ./svc.sh install && sudo ./svc.sh start"
fi
echo ""

# --- ANTHROPIC_API_KEY ---
echo "6. Environment"
if [ -n "${ANTHROPIC_API_KEY:-}" ]; then
    pass "ANTHROPIC_API_KEY is set in environment"
else
    warn "ANTHROPIC_API_KEY not set in current shell (OK if added as GitHub secret)"
fi
echo ""

# --- Summary ---
echo "=== Summary ==="
echo "  Passed: $PASS | Failed: $FAIL | Warnings: $WARN"
echo ""

if [ "$FAIL" -gt 0 ]; then
    echo "Fix the failures above before running workflows."
    exit 1
else
    echo "Runner is ready!"
    exit 0
fi
