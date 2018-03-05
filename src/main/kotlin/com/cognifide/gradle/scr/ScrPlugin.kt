package com.cognifide.gradle.scr

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet

open class ScrPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val processScrAnnotations = project.tasks.create(ProcessSrcAnnotations.MAIN_TASK_NAME, ProcessSrcAnnotations::class.java)
        processScrAnnotations.dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        processScrAnnotations.configure(SourceSet.MAIN_SOURCE_SET_NAME)


        project.tasks.getByName(JavaPlugin.JAR_TASK_NAME).dependsOn(processScrAnnotations)

        val processTestScrAnnotations = project.tasks.create(ProcessSrcAnnotations.TEST_TASK_NAME, ProcessSrcAnnotations::class.java)
        processTestScrAnnotations.dependsOn(JavaPlugin.TEST_CLASSES_TASK_NAME)

        processTestScrAnnotations.configure(SourceSet.TEST_SOURCE_SET_NAME)

        project.tasks.getByName(JavaPlugin.JAR_TASK_NAME).dependsOn(processTestScrAnnotations)

        /*
         * This task has to been run separately from "processScrAnnotations" task, because of the output and inputs.
         * Ant src descriptor task generates MANIFEST.MF, which does not contain Service-Component: line. It has to be
         * added every time application is build.
         */
        val appendServicesToManifest = project.tasks.create(AppendServicesToManifest.TASK_NAME, AppendServicesToManifest)
        appendServicesToManifest.dependsOn processScrAnnotations
        project.tasks.getByName(JavaPlugin.JAR_TASK_NAME).dependsOn(appendServicesToManifest)
    }
}