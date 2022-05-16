package com.mars.infra.plugin

import com.mars.infra.plugin.internal.ModuleManager
import org.gradle.api.Project

/**
 * Created by Mars on 2022/5/16
 */
object Vite {

    /**
     * 检查模块是否修改过
     */
    fun checkModifiedModule(project: Project) {
        // 这个只能获取到app模块
        project.allprojects.forEach {
//            println("project name: ${it.name}")
        }
        // 可以获取到所有的模块
        project.gradle.rootProject.allprojects.forEach {
            println("project---1 name: ${it.name}")
            ModuleManager.computeModuleLastModifyTime(it)
        }
        ModuleManager.modifiedModuleMap.forEach { (project, lastModifyTime) ->
            println("checkModifiedModule---result")
            println("project: $project, lastModifiedTime: $lastModifyTime")
        }
    }
}