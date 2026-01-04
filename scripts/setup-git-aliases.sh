#!/bin/bash
# scripts/setup-git-aliases.sh

echo "Installing project Git aliases..."

# Include the project's git config
git config --local include.path ../.gitconfig.aliases

echo "✓ Git aliases installed for this repository"
echo ""
echo "Available commands:"
echo "  git co-all <branch>      - Checkout branch in main repo and all submodules"
echo "  git new-branch <branch>  - Create new branch in all repos"
echo "  git branch-all           - Show current branch for all repos"
echo "  git sync-branches        - Sync all submodules to main repo's branch"