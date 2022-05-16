package com.mars.infra.plugin

import com.mars.infra.plugin.internal.FileUtils
import com.mars.infra.plugin.internal.Logger
import com.mars.infra.plugin.internal.ModuleManager
import com.mars.infra.plugin.internal.TAG_STEP_ONE
import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object Vite {

    lateinit var mLocalMavenCache: File
    lateinit var mAppProject: Project

    /**
     * 1. localMaven
     */
    fun prepare(project: Project) {
        Logger.enable(true)
        mAppProject = project
        mLocalMavenCache = FileUtils.getLocalMavenCache(project)
    }

    /**
     * 检查模块是否修改过
     */
    fun checkModifiedModule(project: Project) {
        ModuleManager.prepare()
        // 可以获取到所有的模块
        project.gradle.rootProject.allprojects.filter { it.name != it.rootProject.name }.forEach {
            Logger.i("Vite", "project name: ${it.name}")
            ModuleManager.computeModuleLastModifyTime(it)
        }
        val modifiedProjects = mutableListOf<String>()
        val lastModuleModifyInfo = FileUtils.readModuleModifyInfo()
        lastModuleModifyInfo?.let {
            it.lastModify.forEach { (project, lastModifyTime) ->
                val curMap = ModuleManager.getModifiedModuleMap().apply {
                    if (this.isEmpty()) {
                        throw Exception("last-modify module map is Empty")
                    }
                }
                if (curMap[project] != lastModifyTime) {
                    // cur project has modified
                    modifiedProjects.add(project)
                }
            }
        }?: run {
            // 所有的项目都变更了
            val curMap = ModuleManager.getModifiedModuleMap().apply {
                if (this.isEmpty()) {
                    throw Exception("last-modify module map is Empty")
                }
            }
            modifiedProjects.addAll(curMap.keys)
        }
        modifiedProjects.forEach {
            Logger.i(TAG_STEP_ONE, "project: $it has modified")
        }
        ModuleManager.writeModuleModifyInfo(project, ModuleManager.getModifiedModuleMap())
    }
}