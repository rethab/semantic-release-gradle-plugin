package io.github.rethab.semanticrelease

import org.gradle.api.provider.ListProperty

interface SemanticReleaseExtension {
    val releaseBranches: ListProperty<String>
}
