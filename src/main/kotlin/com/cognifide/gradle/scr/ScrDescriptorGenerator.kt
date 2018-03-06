package com.cognifide.gradle.scr

import org.apache.felix.scrplugin.Source
import org.apache.felix.scrplugin.ant.SCRDescriptorTask
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.resources.FileResource
import java.io.File

class ScrDescriptorGenerator : SCRDescriptorTask() {

    override fun getSourceFiles(sourceFiles: FileSet): MutableCollection<Source> {
        val prefix = sourceFiles.dir.absolutePath
        val prefixLength = prefix.length + 1

        return sourceFiles.fold(mutableListOf(), { result, sourceFile ->
            if (sourceFile is FileResource) {
                val file = sourceFile.file
                if (file.name.endsWith(".class")) {
                    result += object : Source {
                        override fun getFile(): File {
                            return file
                        }

                        override fun getClassName(): String {
                            val relativeFilename = file.absolutePath.substring(prefixLength)
                            val normalized = relativeFilename.replace(File.separatorChar, '/')
                            val name = normalized.replace('/', '.')

                            return name.removeSuffix(".class")
                        }
                    }

                }
            }
            result
        })
    }

}