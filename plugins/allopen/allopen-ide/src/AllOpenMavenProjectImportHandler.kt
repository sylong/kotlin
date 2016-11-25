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

package org.jetbrains.kotlin.allopen.ide

import org.jdom.Content
import org.jdom.Element
import org.jdom.Text
import org.jetbrains.idea.maven.project.MavenProject
import org.jetbrains.kotlin.allopen.AllOpenCommandLineProcessor
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.idea.maven.KotlinMavenImporter
import org.jetbrains.kotlin.idea.maven.MavenProjectImportHandler
import org.jetbrains.kotlin.idea.maven.KotlinMavenImporter.Companion.KOTLIN_PLUGIN_GROUP_ID
import org.jetbrains.kotlin.idea.maven.KotlinMavenImporter.Companion.KOTLIN_PLUGIN_ARTIFACT_ID

class AllOpenMavenProjectImportHandler : MavenProjectImportHandler {
    private companion object {
        val MAVEN_ALL_OPEN_ARTIFACT_NAME = "kotlin-maven-allopen"
        val ANNOTATATION_PARAMETER_PREFIX = "all-open:${AllOpenCommandLineProcessor.ANNOTATION_OPTION.name}="
    }

    override fun invoke(facet: KotlinFacet, mavenProject: MavenProject) {
        modifyAllOpenCompilerArguments(facet, getAnnotationsList(mavenProject))
    }

    private fun getAnnotationsList(mavenProject: MavenProject): List<String> {
        val kotlinPlugin = mavenProject.plugins.firstOrNull {
            it.groupId == KOTLIN_PLUGIN_GROUP_ID && it.artifactId == KOTLIN_PLUGIN_ARTIFACT_ID
        } ?: return emptyList()

        val hasAllOpenInDependencies = kotlinPlugin.dependencies.any {
            it.groupId == KOTLIN_PLUGIN_GROUP_ID && it.artifactId == MAVEN_ALL_OPEN_ARTIFACT_NAME
        }

        if (!hasAllOpenInDependencies) return emptyList()

        val configuration = kotlinPlugin.configurationElement ?: return emptyList()

        val enabledCompilerPlugins = configuration.getElement("compilerPlugins")?.getElements()?.flatMap { plugin ->
            plugin.content.mapNotNull { (it as? Text)?.text }
        }

        val allOpenAnnotations = configuration.getElement("pluginOptions")?.getElements()?.flatMap { it.content }
                ?.filter { (it as? Text)?.text?.startsWith(ANNOTATATION_PARAMETER_PREFIX) ?: false }
                ?.map { (it as Text).text.substring(ANNOTATATION_PARAMETER_PREFIX.length) }

        val a = 5
        return emptyList()
    }

    private fun Element.getElement(name: String) = content.firstOrNull { it is Element && it.name == name } as? Element

    @Suppress("UNCHECKED_CAST")
    private fun Element.getElements() = content.filterIsInstance<Element>()
}