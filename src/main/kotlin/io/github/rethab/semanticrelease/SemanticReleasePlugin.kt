package io.github.rethab.semanticrelease

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemanticReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("semanticRelease", SemanticReleaseExtension::class.java)

        target.tasks.create("semanticReleaseSetVersion", SetSemanticVersionTask::class.java)
        target.tasks.create("semanticReleaseTagVersion", TagNewVersionTask::class.java)

        target.afterEvaluate { evaluatedProject ->
            // override project version before generating pom
            evaluatedProject.tasks.findByName("generatePomFileForMavenJavaPublication")
                ?.dependsOn("semanticReleaseSetVersion")
                ?: println("WARNING: could not find maven task generatePomFileForMavenJavaPublication")

            // tag current head after publication
            evaluatedProject.tasks.findByName("publish")
                ?.finalizedBy("semanticReleaseTagVersion")
                ?: println("WARNING: could not find maven task publish")
        }

    }
}
