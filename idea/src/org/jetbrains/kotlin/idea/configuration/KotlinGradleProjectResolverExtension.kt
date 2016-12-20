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

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.project.ModuleDependencyData
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.roots.DependencyScope
import org.gradle.tooling.model.idea.IdeaModule
import org.jetbrains.kotlin.gradle.KotlinGradleModel
import org.jetbrains.kotlin.gradle.KotlinGradleModelBuilder
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension

class KotlinGradleProjectResolverExtension : AbstractProjectResolverExtension() {
    override fun getToolingExtensionsClasses(): Set<Class<out Any>> {
        return setOf(KotlinGradleModelBuilder::class.java, Unit::class.java)
    }

    override fun getExtraProjectModelClasses(): Set<Class<out Any>> {
        return setOf(KotlinGradleModel::class.java)
    }

    override fun populateModuleDependencies(gradleModule: IdeaModule, ideModule: DataNode<ModuleData>, ideProject: DataNode<ProjectData>) {
        val gradleModel = resolverCtx.getExtraProject(gradleModule, KotlinGradleModel::class.java)
        if (gradleModel != null && gradleModel.implements != null) {
            val targetModule = ideProject.children
                    .map { it.data }
                    .filterIsInstance<ModuleData>()
                    .find { it.id == gradleModel.implements }
            if (targetModule != null) {
                val moduleDependencyData = ModuleDependencyData(ideModule.data, targetModule)
                moduleDependencyData.scope = DependencyScope.COMPILE
                moduleDependencyData.isExported = false
                ideModule.createChild(ProjectKeys.MODULE_DEPENDENCY, moduleDependencyData)
            }
        }
        super.populateModuleDependencies(gradleModule, ideModule, ideProject)
    }
}
