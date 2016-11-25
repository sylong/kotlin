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

import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArguments
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.allopen.AllOpenCommandLineProcessor.Companion.PLUGIN_ID
import org.jetbrains.kotlin.allopen.AllOpenCommandLineProcessor.Companion.ANNOTATION_OPTION

internal fun modifyAllOpenCompilerArguments(facet: KotlinFacet, annotations: List<String>) {
    val compileInfo = facet.configuration.settings.compilerInfo

    val newCommonArguments = compileInfo.commonCompilerArguments ?: CommonCompilerArguments.DummyImpl()

    val newOptions = (newCommonArguments.pluginOptions ?: emptyArray()).filterTo(mutableListOf()) { !it.startsWith("plugin:${PLUGIN_ID}:") } +
                     annotations.map { "plugin:${PLUGIN_ID}:${ANNOTATION_OPTION.name}=$it" }
    newCommonArguments.pluginOptions = newOptions.toTypedArray()

    compileInfo.commonCompilerArguments = newCommonArguments
}