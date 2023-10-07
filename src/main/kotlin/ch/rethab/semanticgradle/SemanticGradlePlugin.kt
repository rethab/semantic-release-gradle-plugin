package ch.rethab.semanticgradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class SemanticGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("semanticGradle", SemanticGradleExtension::class.java)

        target.afterEvaluate { evaluatedProject ->
            evaluatedProject.tasks.findByName("generatePomFileForMavenJavaPublication")
                ?.dependsOn("semanticGradleSetVersion")
                ?: println("WARNING: could not find maven task generatePomFileForMavenJavaPublication")
        }

        target.tasks.create("semanticGradleSetVersion", SetSemanticVersionTask::class.java)
    }
}
