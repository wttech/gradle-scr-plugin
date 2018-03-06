package com.cognifide.gradle.scr

import org.apache.felix.scrplugin.ant.SCRDescriptorTask
import org.apache.tools.ant.types.Path
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import java.io.File

open class ScrGenerateTask : DefaultTask() {

    companion object {

        fun name(sourceSet: SourceSet) = "scrGenerate${sourceSet.name.capitalize()}"

    }

    @get:Internal
    lateinit var sourceSet: SourceSet

    @get:Internal
    val classesDir
        get() = sourceSet.output.classesDir

    @get:InputFiles
    val classes: FileCollection
        get() = project.fileTree(sourceSet.output.classesDir).apply {
            exclude("**/${ScrPlugin.OSGI_DIR}/**")
        }

    @get:OutputDirectory
    val osgiDir: File
        get() = File(sourceSet.output.classesDir, ScrPlugin.OSGI_DIR)

    init {
        group = "Build"
        description = "Generate service component descriptor files basing on classes using SCR annotations."
    }

    @get:Internal
    val descriptorOptions: (SCRDescriptorTask) -> Unit = {}

    @TaskAction
    fun run() {
        if (!classesDir.exists()) {
            logger.info("No classes directory found at path '$classesDir' so no SCR descriptor files to be generated basing on $sourceSet")
            return
        }

        if (osgiDir.exists()) {
            osgiDir.deleteRecursively()
            osgiDir.mkdirs()
        }

        logger.info("Generating SCR descriptor files basing on classes at path '$classesDir' from $sourceSet")

        val antProject = project.ant.project
        val runtimePath = sourceSet.compileClasspath.asPath + sourceSet.runtimeClasspath.asPath

        val scrTask = SCRDescriptorTask().apply {
            project = antProject
            setSrcdir(classesDir)
            setDestdir(classesDir)
            setClasspath(Path(antProject, runtimePath)) // TODO fixme!
            setStrictMode(false)
            isScanClasses = true
        }

        scrTask.apply(descriptorOptions)
        scrTask.execute()
    }
}