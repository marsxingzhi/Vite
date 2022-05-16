package com.mars.infra.plugin.internal

import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object ModuleManager {

     val modifiedModuleMap = mutableMapOf<String, Long>()

    fun computeModuleLastModifyTime(project: Project) {
        println("start compute last modify time of $project")
        var sum = 0L
        project.projectDir.listFiles()?.filter { !it.isBuildDir() }?.forEach {
            println("file: ${it.name}")
            sum += it.computeLastModifyTime().apply {
                println("last-modify time of $it is $this")
            }
        }
        modifiedModuleMap[project.name] = sum
    }
}

fun File.isBuildDir(): Boolean {
    return isDirectory && name == "build"
}

// TODO 是否可以直接根据目录的last modify？ 不可以，经过测试，如果修改目录中文件的内容，整个目录的last modify time是没有变化的
fun File.computeLastModifyTime(): Long {
    if (isDirectory) {
        println("目录的last-modify time: ${this.lastModified()}")
    }
    if (isFile) {
//        return this.lastModified()
    } else {
//        listFiles()?.forEach {
//            println("computeLastModifyTime file: $it")
//        }
//        walkTopDown().forEach {
//            println("computeLastModifyTime file: $it")
//        }
    }
    return lastModified()
}