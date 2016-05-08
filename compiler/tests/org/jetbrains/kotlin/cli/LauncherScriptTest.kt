/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.cli

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.util.SystemInfo
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.test.TestCaseWithTmpdir
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import java.nio.charset.Charset

class LauncherScriptTest : TestCaseWithTmpdir() {
    private fun runProcess(
            executableName: String,
            vararg args: String,
            expectedStdout: String = "",
            expectedStderr: String = "",
            expectedExitCode: ExitCode = ExitCode.OK
    ) {
        val executableFileName = if (SystemInfo.isWindows) "$executableName.bat" else executableName
        val launcherFile = File(PathUtil.getKotlinPathsForDistDirectory().homePath, "bin/$executableFileName")
        assertTrue("Launcher script not found, run 'ant dist': ${launcherFile.absolutePath}", launcherFile.exists())

        val processOutput = ExecUtil.execAndGetOutput(GeneralCommandLine(launcherFile.absolutePath, *args))
        val stdout = processOutput.stdout
        val stderr = processOutput.stderr
        val exitCode = processOutput.exitCode

        try {
            assertEquals(expectedStdout, stdout)
            assertEquals(expectedStderr, stderr)
            assertEquals(expectedExitCode.code, exitCode)
        }
        catch (e: Exception) {
            System.err.println("exit code $exitCode")
            System.err.println("<stdout>$stdout</stdout>")
            System.err.println("<stderr>$stderr</stderr>")
            throw e
        }
    }

    private val testDataDirectory: String
        get() = KotlinTestUtils.getTestDataPathBase() + "/launcher"

    fun testKotlincSimple() {
        runProcess(
                "kotlinc",
                "$testDataDirectory/helloWorld.kt",
                "-d", tmpdir.path
        )
    }

    fun testKotlincJvmSimple() {
        runProcess(
                "kotlinc-jvm",
                "$testDataDirectory/helloWorld.kt",
                "-d", tmpdir.path
        )
    }

    fun testWithNonAsciiSymbols() {
        val old = resetDefaultCharset(Charsets.UTF_8.name())

        fun pathToTestFile(extension: String) = "$testDataDirectory/withNonAsciiSymbols.$extension"
        fun String.normalize() = replace('/', File.separatorChar).replace("\r\n", "\n")

        runProcess(
                "kotlinc",
                pathToTestFile("kt"),
                "-d", tmpdir.path,
                expectedStderr = File(pathToTestFile("out")).readText().normalize(),
                expectedExitCode = ExitCode.COMPILATION_ERROR
        )

        resetDefaultCharset(old)
    }

    fun testKotlincJsSimple() {
        runProcess(
                "kotlinc-js",
                "$testDataDirectory/emptyMain.kt",
                "-no-stdlib",
                "-output", File(tmpdir, "out.js").path
        )
    }
}

fun resetDefaultCharset(newDefault: String?): String? {
    if (newDefault == null) return null

    System.setProperty("file.encoding", newDefault)

    val charset = Charset::class.java.getDeclaredField("defaultCharset")
    charset.isAccessible = true

    val prev = charset.get(null) as String?

    charset.set(null, null)

    return prev
}
