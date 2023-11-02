package io.github.rethab.semanticrelease

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemanticReleasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("semanticRelease", SemanticReleaseExtension::class.java)

        extension.releaseBranches.convention(listOf("main", "master"))

        target.tasks.create("semanticReleaseSetVersion", SetSemanticVersionTask::class.java)
        target.tasks.create("semanticReleaseTagVersion", TagNewVersionTask::class.java) {
            it.releaseBranches.set(extension.releaseBranches)
        }

        target.afterEvaluate { evaluatedProject ->
            // override project version before generating pom
            evaluatedProject.tasks.findByName("generatePomFileForMavenJavaPublication")
                ?.dependsOn("semanticReleaseSetVersion")
                ?: target.logger.error("Could not find maven task generatePomFileForMavenJavaPublication")

            // tag current head after publication
            evaluatedProject.tasks.findByName("publish")
                ?.finalizedBy("semanticReleaseTagVersion")
                ?: target.logger.error("Could not find maven task publish")
        }
    }
}
