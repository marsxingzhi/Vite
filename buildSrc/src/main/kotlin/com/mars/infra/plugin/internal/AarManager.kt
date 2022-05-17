package com.mars.infra.plugin.internal

import com.android.build.gradle.AppExtension
import com.mars.infra.plugin.ViteTest
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by Mars on 2022/5/17
 */
object AarManager {

    const val ASSEMBLE = "assemble"

    /**
     * 1. 找到对应的assemble${FlavorName}${BuildType}的任务，例如：assembleDebug
     */
    fun generate(appProject: Project) {
        // 必须是project: app才能获取到AppExtension
        val android = appProject.extensions.getByType(AppExtension::class.java)
        android.applicationVariants.forEach {
            Logger.i("AarManager", "variant: ${it}")
            val assembleTaskName =
                "$ASSEMBLE${it.flavorName.capitalize()}${it.buildType.name.capitalize()}"
            val taskProvider = appProject.tasks.named(assembleTaskName) ?: throw Exception("不存在name为${assembleTaskName}的Task")

            // TODO 测试，暂时指定login模块
            val loginProject = ViteTest.projectMap["login"]!!
            val bundleTask = obtainBundleTask(loginProject, it.buildType.name.capitalize())

            taskProvider.configure { task ->
                task.finalizedBy(bundleTask)
            }
        }
    }

    // 获取bundle${Flavor}${BuildType}Aar的Task，打aar的Task
    private fun obtainBundleTask(project: Project, variantName: String): Task {
        val taskName = "bundle${variantName}Aar"
        val task = try {
            project.tasks.named(taskName)
        } catch (ignore: Exception) {
            null
        }?:run {
            throw Exception("$taskName Task in $project is NotFound")
        }
        return task.get()
    }
}