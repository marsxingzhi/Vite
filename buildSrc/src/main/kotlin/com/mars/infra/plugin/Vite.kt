package com.mars.infra.plugin

import com.mars.infra.plugin.internal.FileUtils
import com.mars.infra.plugin.internal.Logger
import com.mars.infra.plugin.internal.ModuleManager
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
        // 这个只能获取到app模块
//        project.allprojects.forEach {
//        }
        // 可以获取到所有的模块
        project.gradle.rootProject.allprojects.filter { it.name != it.rootProject.name }.forEach {
            Logger.i("Vite", "project name: ${it.name}")
            ModuleManager.computeModuleLastModifyTime(it)
        }

        FileUtils.readModuleModifyInfo()
//        ViteTest.writeModuleModifyInfo(project, ModuleManager.modifiedModuleMap)
    }
}