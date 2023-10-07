⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️ 
⚠️ PROTOTYPE STATE ⚠️
⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️

# Semantic Gradle

Gradle Plugin for Automatic Semantic Versioning

## Configuration

### Prerequisites

This plugin requires `git` to be available.

###

TODO

## How it Works

- Finds the latest version based on git tags
- Looks at all commits since the latest version
- Depending on the commit message, decides how to bump the latest version
- Uses the GitHub API to create a new tag
