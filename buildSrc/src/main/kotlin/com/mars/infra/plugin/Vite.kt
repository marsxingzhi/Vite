package com.mars.infra.plugin

import com.mars.infra.plugin.internal.*
import com.mars.infra.plugin.internal.model.TAG_STEP_ONE
import com.mars.infra.plugin.internal.util.FileUtils
import com.mars.infra.plugin.internal.util.Logger
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object Vite {

    var mLocalMavenCache: File? = null
    var mAppProject: Project? = null

    /**
     * 1. localMaven
     */
    fun prepare(project: Project) {
        Logger.enable(true)
        mAppProject = project
        mLocalMavenCache = FileUtils.getLocalMavenCache(project)
        flatDir()
    }

    private fun flatDir() {
        val map = hashMapOf<String, File>()
        map["dirs"] = mLocalMavenCache!!
        mAppProject!!.rootProject.allprojects.forEach {
            it.repositories.flatDir(map)
        }
    }

    /**
     * 检查模块是否修改过
     */
    fun checkModifiedModule(project: Project) {
        ViteTest.collectProject(project)
        ModuleManager.prepare()
        // 可以获取到所有的模块
        project.gradle.rootProject.allprojects.filter { it.name != it.rootProject.name }.forEach {
            ModuleManager.recordModule(it)
            ModuleManager.computeModuleLastModifyTime(it)
        }
        val modifiedProjects = mutableListOf<String>()
        val lastModuleModifyInfo = FileUtils.readModuleModifyInfo()
        lastModuleModifyInfo?.let {
            it.lastModify.forEach { (project, lastModifyTime) ->
                val curMap = ModuleManager.getModuleMap().apply {
                    if (this.isEmpty()) {
                        throw Exception("last-modify module map is Empty")
                    }
                }
                if (curMap[project] != lastModifyTime) {
                    // cur project has modified
                    modifiedProjects.add(project)
                }
            }
        } ?: run {
            // 所有的项目都变更了
            val curMap = ModuleManager.getModuleMap().apply {
                if (this.isEmpty()) {
                    throw Exception("last-modify module map is Empty")
                }
            }
            modifiedProjects.addAll(curMap.keys)
        }
        modifiedProjects.forEach {
            Logger.i(TAG_STEP_ONE, "project: $it has modified")
        }
        ModuleManager.saveModifiedModules(modifiedProjects)
        ModuleManager.writeModuleModifyInfo(project, ModuleManager.getModuleMap())
    }

    fun projectsEvaluated(gradle: Gradle) {
        ModuleManager.getModifiedModules().onEach { project ->
            Logger.i("modified-modules", "project name = ${project.name}")
            mAppProject?.let {
                AarManager.generate(it, project)
            }
        }
    }
}