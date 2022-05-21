package com.mars.infra.plugin.internal

import org.gradle.api.Project

/**
 * Created by Mars on 2022/5/19
 */
object DependencyManager {

    // TODO 暂时写死，方便测试
    fun addDependencyWithAar(project: Project) {
        try {
            val configName = "implementation"
            val map = hashMapOf<String, String>()
            map["name"] = "account-debug"
            map["ext"] = "aar"
            project.dependencies.add(configName, map)
        } catch (ignore: Exception) {

        }
    }
}