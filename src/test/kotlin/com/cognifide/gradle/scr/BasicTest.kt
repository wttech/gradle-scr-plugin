package com.cognifide.gradle.scr

import org.junit.Assert.assertTrue
import org.junit.Test

class BasicTest : BuildTest() {

    @Test
    fun shouldGenerateDescriptorsAndAddManifestEntry() {
        build("basic", ":jar", { result ->
            val pkg = result.file("build/libs/example-1.0.0.jar")

            assertTrue(pkg.exists())
            assertTaskOutcome(result.build, ":jar")
        })
    }

}
