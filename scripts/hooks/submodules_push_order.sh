#!/bin/bash
# .git/hooks/pre-push
# Prevents pushing main repo if submodules have unpushed commits

UNPUSHED=$(git submodule foreach --quiet 'git log --oneline @{u}.. | wc -l')
if [ "$UNPUSHED" -gt 0 ]; then
    echo "ERROR: Submodules have unpushed commits!"
    git submodule foreach 'git log --oneline @{u}..'
    echo "Push submodules first with: git submodule foreach git push"
    exit 1
fi