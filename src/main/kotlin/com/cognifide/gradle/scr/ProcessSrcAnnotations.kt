package com.cognifide.gradle.scr

import org.apache.felix.scrplugin.ant.SCRDescriptorTask
import org.apache.tools.ant.types.Path
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProcessSrcAnnotations : DefaultTask() {

    companion object {
        const val MAIN_TASK_NAME = "processScrAnnotations"

        const val TEST_TASK_NAME = "processTestScrAnnotations"
    }

    @Input
    var sourceSetName: String = "main"

    @get:OutputDirectory
    val output: File
        get() = File(findSourceSet(project).output.classesDir, "OSGI-INF")

    init {
        group = "Build"
        description = "Scans classes to find annotated with the Felix SCR service annotations and creates proper xml files required by OSGI (in OSGI-INF directory)."
    }

    fun configure(sourceSetName: String) {
        this.sourceSetName = sourceSetName

        val tree = project.fileTree(findSourceSet(project).output.classesDir)
        tree.exclude("**/OSGI-INF/**")

        inputs.files(tree)
    }

    @TaskAction
    fun run() {
        val sourceSet = findSourceSet(project)
        val classesDir = sourceSet.output.classesDir

        logger.info("Running SCR for $classesDir")

        if (classesDir.exists()) {
            val antProject = project.ant.project
            val runtimePath = sourceSet.runtimeClasspath.asPath

            val scrTask = SCRDescriptorTask().run {
                srcdir = classesDir
                destdir = classesDir
                classpath = Path(antProject, runtimePath)
                strictMode = false
                project = antProject
                scanClasses = true
            }

            scrTask.execute()
        }
    }

    fun findSourceSet(project: Project): SourceSet {
        convention.findPlugin(JavaPluginConvention::class.java)?.sourceSets?.getByName(sourceSetName)
    }
}