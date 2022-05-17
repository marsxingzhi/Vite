package com.mars.infra.plugin

import com.android.build.gradle.LibraryExtension
import com.mars.infra.plugin.internal.*
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
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
        ViteTest.collectProject(project)
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
        } ?: run {
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

    // TODO 测试
    fun projectsEvaluated(gradle: Gradle) {
        gradle.rootProject.allprojects.forEach {
            if (it.name == "login") {
                val libraryExtension = try {
                    it.project.extensions.getByType(LibraryExtension::class.java)
                } catch (ignore: Exception) {
                    null
                }
                if (libraryExtension != null) {
                    // 生成aar
                    AarManager.generate(mAppProject)
                }
            }
        }
    }
}