package com.mars.infra.plugin.internal

import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object ModuleManager {

    private val modifiedModuleMap = mutableMapOf<String, Long>()

    fun prepare() {
        modifiedModuleMap.clear()
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
        modifiedModuleMap[project.name] = sum
        Logger.i(TAG_STEP_ONE, "computeModuleLastModifyTime---project: ${project.name}, lastModifyTime: $sum")
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