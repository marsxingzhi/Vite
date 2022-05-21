package com.mars.infra.plugin.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * Created by Mars on 2022/5/21
 */
fun Project.getConfigurationList(): List<Configuration> {
    val configurationList = arrayListOf<Configuration>()
    listOf("implementation", "api", "compileOny").forEach {
        val configuration = this.configurations.maybeCreate(it)
        configurationList.add(configuration)
    }
    return configurationList
}