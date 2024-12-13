#!/bin/bash

# Check if git-filter-repo is installed
if ! command -v git-filter-repo &> /dev/null
then
    echo "git-filter-repo could not be found. Please install it first."
    exit 1
fi

# Run git filter-repo with --force to change commit messages
git filter-repo --force --message-callback '
    if b"Merge branch" in message:
        return b"merge commits"
    return message
'

