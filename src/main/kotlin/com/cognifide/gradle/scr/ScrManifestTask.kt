package com.cognifide.gradle.scr

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import java.io.File

open class ScrManifestTask : DefaultTask() {

    init {
        group = "Build"
        description = "Adds Jar manifest entry for generated service components."
    }

    @get:Internal
    lateinit var sourceSet: SourceSet

    @get:Internal
    val osgiDir: File
        get() = File(sourceSet.output.classesDir, ScrPlugin.OSGI_DIR)

    @TaskAction
    fun run() {
        val files = osgiDir.listFiles({ _, name -> name.endsWith(".xml") })

        if (files != null && files.isNotEmpty()) {
            val fileNames = files.map { file -> "${ScrPlugin.OSGI_DIR}/${file.name}" }

            logger.info("Found SCR descriptor files: $fileNames.")

            val jar = project.tasks.getByName(Jar.TASK_NAME) as Jar
            jar.manifest.attributes(mapOf(
                    ScrPlugin.MANIFEST_ENTRY to fileNames.joinToString(",")
            ))

            logger.info("Manifest entry 'Service-Components' added.")
        } else {
            logger.debug("No SCR descriptor files found at path: '$osgiDir'")
        }
    }

    companion object {

        fun name(sourceSet: SourceSet) = "scrManifest${sourceSet.name.capitalize()}"

    }

}