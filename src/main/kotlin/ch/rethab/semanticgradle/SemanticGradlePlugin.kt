package ch.rethab.semanticgradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemanticGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("semanticGradle", SemanticGradleExtension::class.java)

        target.tasks.create("semanticGradleSetVersion", SetSemanticVersionTask::class.java)
        target.tasks.create("semanticGradleTagVersion", TagNewVersionTask::class.java)

        target.afterEvaluate { evaluatedProject ->
            // override project version before generating pom
            evaluatedProject.tasks.findByName("generatePomFileForMavenJavaPublication")
                ?.dependsOn("semanticGradleSetVersion")
                ?: println("WARNING: could not find maven task generatePomFileForMavenJavaPublication")

            // tag current head after publication
            evaluatedProject.tasks.findByName("publish")
                ?.finalizedBy("semanticGradleTagVersion")
                ?: println("WARNING: could not find maven task publish")
        }

    }
}
