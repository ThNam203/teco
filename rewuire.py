import subprocess
import os
import random
from datetime import datetime, timedelta

# List of possible authors (username, email)
authors = [
    {"name": "NTDKhoa04", "email": "22520679@gm.uit.edu.vn"},
    {"name": "PhuscBui", "email": "duyphucbui522@gmail.com"},
    {"name": "sen1or", "email": "hthnam203@gmail.com"},
]

# Function to get a list of commits with their hashes and messages
def get_commit_hashes():
    result = subprocess.run(
        ["git", "log", "--reverse", "--pretty=format:%H %P %s"], capture_output=True, text=True
    )
    return [line.split(' ', 2) for line in result.stdout.splitlines()]

# Function to randomly assign authors evenly across 200 commits
def evenly_distributed_author(commit_index, total_commits):
    author_count = len(authors)
    return authors[commit_index % author_count]  # Rotates evenly among authors

# Function to generate a random time within the day
def random_time():
    return timedelta(seconds=random.randint(0, 86400))  # Random time during the day

# Function to incrementally assign commit dates (with more focus on Thursdays and Sundays)
def incremental_date_generator(start_date, total_commits, focus_days):
    current_date = start_date
    commit_dates = []

    while len(commit_dates) < total_commits:
        day_commits = random.randint(1, 4)  # Random number of commits for each day

        # Focus more commits on Thursday (3) and Sunday (6)
        if current_date.weekday() in focus_days:
            day_commits += random.randint(1, 3)

        for _ in range(day_commits):
            if len(commit_dates) >= total_commits:
                break
            commit_time = current_date + random_time()
            commit_dates.append(commit_time.strftime("%a %b %d %H:%M:%S %Y +0000"))

        # Move to the next day
        current_date += timedelta(days=1)

    return commit_dates

# Function to rewrite a single commit using git commit-tree
def rewrite_commit(commit_hash, parent_hash, message, author, date):
    # Set environment variables for the new author and date
    env = os.environ.copy()
    env["GIT_AUTHOR_NAME"] = author["name"]
    env["GIT_AUTHOR_EMAIL"] = author["email"]
    env["GIT_COMMITTER_NAME"] = author["name"]
    env["GIT_COMMITTER_EMAIL"] = author["email"]
    env["GIT_AUTHOR_DATE"] = date
    env["GIT_COMMITTER_DATE"] = date

    # Get the tree associated with the commit
    tree_hash = subprocess.run(
        ["git", "rev-parse", f"{commit_hash}^{{tree}}"],
        capture_output=True, text=True
    ).stdout.strip()

    # Create a new commit with the new author info, date, and message
    if parent_hash:
        result = subprocess.run(
            ["git", "commit-tree", tree_hash, "-p", parent_hash, "-m", message],
            capture_output=True, text=True, env=env
        )
    else:  # Handle the root commit (no parent)
        result = subprocess.run(
            ["git", "commit-tree", tree_hash, "-m", message],
            capture_output=True, text=True, env=env
        )

    # Return the new commit hash
    return result.stdout.strip()

# Main function to iterate over all commits
def rewrite_commit_history(repo_path):
    # Change to the Git repository directory
    os.chdir(repo_path)

    # Get the list of commit hashes, parent hashes, and commit messages
    commits = get_commit_hashes()

    # Define the date range for commits
    start_date = datetime(2024, 9, 1)  # Start of September 2024
    total_commits = len(commits)
    focus_days = [3, 6]  # Focus more on Thursday (3) and Sunday (6)

    # Generate incremental commit dates
    commit_dates = incremental_date_generator(start_date, total_commits, focus_days)

    # Dictionary to track the new commit hashes (old_hash -> new_hash)
    new_hashes = {}

    for commit_index, commit in enumerate(commits):
        commit_hash, parent_hash, message = commit
        parent_hash = parent_hash.strip()  # Get parent commit hash (if any)

        # If the parent commit has been rewritten, get its new hash
        if parent_hash and parent_hash in new_hashes:
            parent_hash = new_hashes[parent_hash]

        print(f"\nProcessing commit {commit_hash}...")

        # Choose author based on even distribution
        author = evenly_distributed_author(commit_index, total_commits)
        print(f"Selected author: {author['name']} ({author['email']})")

        # Get the next incremental date
        date = commit_dates[commit_index]
        print(f"Generated date: {date}")

        # Rewrite commit and get new hash
        new_commit_hash = rewrite_commit(commit_hash, parent_hash, message, author, date)
        print(f"New commit hash: {new_commit_hash}")

        # Store the new commit hash for reference in future commits
        new_hashes[commit_hash] = new_commit_hash

    # Update the branch to point to the new head
    final_hash = list(new_hashes.values())[-1]
    subprocess.run(["git", "reset", "--hard", final_hash])

# Usage
if __name__ == "__main__":
    repo_path = "./"  # Replace with your Git repo path
    rewrite_commit_history(repo_path)

