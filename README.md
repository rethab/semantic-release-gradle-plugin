# Semantic Gradle

Gradle Plugin for Automatic Semantic Versioning

## Configuration

### Prerequisites

- `git` must be available
- only works in conjunction with the `maven-publish` plugin

### Installation

Add this plugin alongside `maven-publish`:

```groovy
plugins {
    id 'ch.rethab.semantic-gradle' version '0.0.1'
    id 'maven-publish'
}
```

## Contributions

Contributions are welcome.

Please open an issue or create a pull request.

## How it Works

- Finds the latest version based on git tags
- Looks at all commits since the latest version
- Depending on the commit message, decides how to bump the latest version
- Overrides the project's version right before the POM is generated for the maven publication
- ...maven publication...
- Tags the current HEAD with the newly created version
- Pushes the tag
