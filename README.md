# Semantic Gradle

Gradle Plugin for Automatic Semantic Versioning

## Automatic Semantic What?

[Semantic Versioning](https://semver.org/) is this idea that in a version like `MAJOR.MINOR.PATCH`, each of the components is updated based on the change.
For example, for a new feature that is backwards compatible, the minor version is updated (e.g. `1.2.3` --> `1.3.0`).

[Conventional Commits](https://www.conventionalcommits.org/) is this idea that your commits are formatted in a machine-readable way showing what changed.
For example, a commit that fixes a bug would have a message like `fix: banner url typo`.

These two combined, it possible to automate releasing.
Based on the commit message, we can automatically determine what version to publish next.
Imagine your latest published version is `1.2.3`.
Now you're merging a commit `feat: added new banner`.
Since this is a new feature, the new version will be `1.3.0`.

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

### CI Integration

#### GitHub Actions

TODO

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
