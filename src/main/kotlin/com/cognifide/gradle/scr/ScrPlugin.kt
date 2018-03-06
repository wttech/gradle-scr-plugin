package com.cognifide.gradle.scr

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

open class ScrPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configureSourceSet(
                SourceSet.MAIN_SOURCE_SET_NAME,
                JavaPlugin.CLASSES_TASK_NAME,
                JavaPlugin.JAR_TASK_NAME
        )

        project.configureSourceSet(
                SourceSet.TEST_SOURCE_SET_NAME,
                JavaPlugin.TEST_CLASSES_TASK_NAME,
                JavaPlugin.TEST_TASK_NAME
        )
    }

    fun Project.configureSourceSet(sourceSetName: String, classesTaskName: String, buildTaskName: String) {
        val convention = convention.getPlugin(JavaPluginConvention::class.java)
        val sourceSet = convention.sourceSets.getByName(sourceSetName)

        val classesTask = tasks.getByName(classesTaskName)
        val buildTask = tasks.getByName(buildTaskName)

        val generateTask = tasks.create(ScrGenerateTask.name(sourceSet), ScrGenerateTask::class.java, { it.sourceSet = sourceSet })
        val manifestTask = tasks.create(ScrManifestTask.name(sourceSet), ScrManifestTask::class.java, { it.sourceSet = sourceSet })

        generateTask.dependsOn(classesTask)
        buildTask.dependsOn(generateTask)
        buildTask.dependsOn(manifestTask)
    }

    companion object {

        const val MANIFEST_ENTRY = "Service-Component"

        const val OSGI_DIR = "OSGI-INF"

    }

}