package com.mars.infra.plugin.internal

import com.android.build.gradle.LibraryExtension
import com.google.gson.Gson
import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object ModuleManager {

    // 全局所有的module的映射表, key: name, value: project
    private val allModuleMap = mutableMapOf<String, Project>()
    private val moduleMap = mutableMapOf<String, Long>()
    private val modifiedModuleList = mutableListOf<String>()

    fun saveModifiedModules(modifiedModules: MutableList<String>) {
        modifiedModuleList.clear()
        modifiedModuleList.addAll(modifiedModules)
    }

    /**
     * 存在修改的模块，不包含app模块
     */
    fun getModifiedModules(): List<Project> {
        val result = arrayListOf<Project>()
        modifiedModuleList.filter { it != "app" }.forEach {
            val project = allModuleMap[it]
            val libraryExtension =
                try {
                    project!!.extensions.getByType(LibraryExtension::class.java)
                } catch (ignore: Exception) {
                    null
                }
            if (libraryExtension != null) {
                result.add(project!!)
            }
        }
        return result
    }

    fun getModuleMap(): Map<String, Long> {
        return moduleMap
    }

    fun getAllModuleMap(): Map<String, Project> {
        return allModuleMap
    }

    fun prepare() {
        moduleMap.clear()
        allModuleMap.clear()
    }

    fun computeModuleLastModifyTime(project: Project) {
        Logger.i(TAG_STEP_ONE, "start compute last modify time of $project")
        var sum = 0L
        project.projectDir.listFiles()?.filter { !it.isBuildDir() }?.forEach {
            Logger.i(TAG_STEP_ONE, "file: ${it.name}")
            sum += it.computeLastModifyTime().apply {
                Logger.i(TAG_STEP_ONE, "last-modify time of $it is $this")
            }
        }
        moduleMap[project.name] = sum
//        Logger.i(TAG_STEP_ONE, "computeModuleLastModifyTime---project: ${project.name}, lastModifyTime: $sum")
    }

    fun writeModuleModifyInfo(project: Project, map: Map<String, Long>) {
        val localMavenCache = FileUtils.getLocalMavenCache(project)
        val json = File(localMavenCache, MODULE_LAST_MODIFY).apply {
            if (!exists()) {
                this.createNewFile()
            }
        }
        val content = Gson().toJson(ModuleData(map))
        json.writeText(content)
    }

    fun recordModule(project: Project) {
        Logger.i("ModuleManager", "project name = ${project.name}")
        allModuleMap[project.name] = project
    }
}

/**
 * app/build/
 * base/network/build/
 */
fun File.isBuildDir(): Boolean {
    return isDirectory && name == "build" || absolutePath.contains("build/")
}

/**
 * 是否可以直接根据目录的last modify？
 * 不可以，经过测试，如果修改目录中文件的内容，整个目录的last modify time是没有变化的
 */
fun File.computeLastModifyTime(): Long {
    if (isDirectory) {
        listFiles()?.forEach {
            it.computeLastModifyTime()
        }
    } else {
        if (!absolutePath.contains("build/")) {
            return this.lastModified()
        }
    }
    return 0
}