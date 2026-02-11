# MCP Setup Guide for Mangala Wallet

This guide covers setting up MCP (Model Context Protocol) servers for the Claude Code development workflow.

## Currently Active MCPs

### Context7 (Library Documentation)
- **Status**: Active
- **Purpose**: Look up library docs, API references, code examples
- **Tools**: `mcp__context7__resolve-library-id`, `mcp__context7__query-docs`
- **Used by**: All 16 commands
- **No setup needed** - works out of the box

### Figma Remote MCP (Design Specs)
- **Status**: Active
- **Purpose**: Extract design specifications and screenshots from Figma files
- **Tools**: `mcp__figma-remote-mcp__get_design_context`, `mcp__figma-remote-mcp__get_screenshot`
- **Used by**: `/design-ux`, `/code`, `/write-ux`, `/review`
- **No setup needed** - already configured

## Pending MCPs (Enable When Ready)

### GitHub MCP

**Why**: Manage issues, PRs, code reviews, and Actions directly from Claude Code.

**Integration with commands**:
| Command | GitHub MCP Usage |
|---------|-----------------|
| `/do` | Auto-create branch + PR after implementation |
| `/analyze` | Create issues from CRITICAL/HIGH findings |
| `/review` | Read PR diff, post review comments |
| `/deploy-check` | Check Actions status, PR merge state |
| `/debug` | Read issue description, linked PRs, CI logs |
| `/requirements` | Link user stories to GitHub issues |

**Setup steps**:

1. Create a GitHub Personal Access Token (PAT):
   - Go to https://github.com/settings/tokens
   - Create a **Fine-grained token** with these permissions:
     - Repository access: `MangalaLabs/mangala-wallet`
     - Permissions: Issues (Read/Write), Pull Requests (Read/Write), Contents (Read), Actions (Read)
   - Or create a **Classic token** with scopes: `repo`, `read:org`

2. Set the environment variable:
   ```bash
   # Add to your shell profile (~/.zshrc or ~/.bashrc)
   export GITHUB_PERSONAL_ACCESS_TOKEN="ghp_your_token_here"
   ```

3. Reload shell and restart Claude Code:
   ```bash
   source ~/.zshrc
   ```

4. Enable the plugin in Claude Code:
   ```bash
   claude mcp add github
   # Or enable via Claude Code settings
   ```

5. Verify it works:
   ```
   # In Claude Code, try:
   "List open issues on MangalaLabs/mangala-wallet"
   ```

**Expected tools after enabling**:
- Issue management: create, read, update, list, search issues
- PR management: create, read, list, review, merge pull requests
- Code search: search code across repositories
- Actions: view workflow runs, check CI status

---

### Firebase MCP

**Why**: Query Crashlytics crash reports, Analytics data, Performance traces, and manage App Distribution directly from Claude Code.

**Integration with commands**:
| Command | Firebase MCP Usage |
|---------|-------------------|
| `/debug` | Read Crashlytics crash reports - stack traces, affected users, device info |
| `/analyze` | Query Analytics events - user behavior, drop-off points, screen views |
| `/optimize` | Read Performance traces - startup time, screen rendering, network latency |
| `/deploy-check` | Check App Distribution status, manage releases |

**Setup steps**:

1. Install Firebase CLI (if not already):
   ```bash
   npm install -g firebase-tools
   ```

2. Login to Firebase:
   ```bash
   firebase login
   ```
   This opens a browser for Google OAuth. Use the account that has access to the Mangala Wallet Firebase project.

3. Verify Firebase access:
   ```bash
   firebase projects:list
   # Should show the Mangala Wallet project
   ```

4. Enable the plugin in Claude Code:
   ```bash
   claude mcp add firebase
   # Or enable via Claude Code settings
   ```

5. Verify it works:
   ```
   # In Claude Code, try:
   "List recent Crashlytics issues for Mangala Wallet"
   ```

**Expected tools after enabling**:
- Crashlytics: list issues, get crash details, view stack traces
- Analytics: query events, audience data
- Firestore: read/write database (if used)
- App Distribution: manage releases, testers
- Remote Config: read/update feature flags

**Important**: Firebase MCP runs `npx firebase-tools@latest mcp` which downloads the latest firebase-tools on each invocation. For faster startup, use the globally installed version.

---

## MCP Integration Architecture

```
┌──────────────── Claude Code Session ─────────────────────┐
│                                                           │
│  User: /do "Fix crash in portfolio screen (#42)"          │
│                                                           │
│  ┌─────────── /do Orchestrator ────────────────────────┐  │
│  │                                                     │  │
│  │  Phase: DISCOVER                                    │  │
│  │    ├── GitHub MCP: read issue #42 (description,     │  │
│  │    │   labels, comments, linked PRs)                │  │
│  │    ├── Firebase MCP: query Crashlytics for related  │  │
│  │    │   crashes (stack trace, device, frequency)      │  │
│  │    └── Codebase: trace flow with /explore protocol  │  │
│  │                                                     │  │
│  │  Phase: ANALYZE                                     │  │
│  │    ├── Firebase MCP: check Analytics for affected   │  │
│  │    │   user count and screen flow                   │  │
│  │    └── Firebase MCP: Performance traces for timing  │  │
│  │                                                     │  │
│  │  Phase: IMPLEMENT                                   │  │
│  │    ├── Context7 MCP: look up API docs if needed     │  │
│  │    ├── Figma MCP: check design specs if UI change   │  │
│  │    └── Write code following conventions             │  │
│  │                                                     │  │
│  │  Phase: DELIVER                                     │  │
│  │    └── GitHub MCP: create PR linking to #42         │  │
│  │                                                     │  │
│  └─────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────┘
```

## Future MCPs to Consider

| MCP | When to Enable | Purpose |
|-----|---------------|---------|
| **Linear** | If team adopts Linear for project management | Task tracking, sprint planning |
| **Slack** | If team moves from Telegram to Slack | Build notifications, team communication |
| **Greptile** | If codebase grows significantly | AI-powered code search (may be redundant with Claude's built-in search) |

## Custom MCP Ideas

| Custom MCP | Purpose | Priority |
|-----------|---------|----------|
| **Blockchain Explorer** | Query Etherscan, validate addresses, check tx status | Low - useful when debugging on-chain issues |
| **Gradle Project** | List modules, dependencies, build tasks | Low - ./gradlew commands work fine |

## Troubleshooting

### MCP not responding
```bash
# Check MCP status
claude mcp list

# Restart specific MCP
claude mcp restart <name>
```

### GitHub MCP auth issues
```bash
# Verify token is set
echo $GITHUB_PERSONAL_ACCESS_TOKEN

# Test GitHub API access
curl -H "Authorization: Bearer $GITHUB_PERSONAL_ACCESS_TOKEN" https://api.github.com/user
```

### Firebase MCP auth issues
```bash
# Re-login
firebase login --reauth

# Check project access
firebase projects:list
```
