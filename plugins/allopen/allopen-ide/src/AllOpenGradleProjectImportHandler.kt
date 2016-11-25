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

import com.intellij.openapi.externalSystem.model.project.ProjectData
import org.jetbrains.kotlin.idea.configuration.GradleProjectImportHandler
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.plugins.gradle.model.ExternalProject
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData
import org.jetbrains.plugins.gradle.service.project.data.ExternalProjectDataCache
import java.io.File

class AllOpenGradleProjectImportHandler : GradleProjectImportHandler {
    override fun invoke(facet: KotlinFacet, projectData: ProjectData?, sourceSetData: GradleSourceSetData) {
        modifyAllOpenCompilerArguments(facet, getAnnotationsList(facet, projectData, sourceSetData))
    }

    private fun getAnnotationsList(facet: KotlinFacet, projectData: ProjectData?, sourceSetData: GradleSourceSetData): List<String> {
        if (projectData == null) return emptyList()
        val externalProjectCache = ExternalProjectDataCache.getInstance(facet.module.project) ?: return emptyList()
        val linkedProjectPath = projectData.linkedExternalProjectPath
        val rootExternalProject = externalProjectCache.getRootExternalProject(sourceSetData.owner, File(linkedProjectPath)) ?: return emptyList()
        val moduleExternalProject = findProject(rootExternalProject, linkedProjectPath) ?: return emptyList()

        val allOpenDataStorageTaskDescription = moduleExternalProject.tasks["allOpenDataStorageTask"]?.description ?: return emptyList()
        return allOpenDataStorageTaskDescription.substringAfter(":", missingDelimiterValue = "").trim().split(',')
    }

    private fun findProject(externalProject: ExternalProject, modulePath: String): ExternalProject? {
        if (externalProject.projectDir.absolutePath == modulePath) {
            return externalProject
        }

        for (childProject in externalProject.childProjects.values) {
            findProject(childProject, modulePath)?.let { return it }
        }

        return null
    }
}