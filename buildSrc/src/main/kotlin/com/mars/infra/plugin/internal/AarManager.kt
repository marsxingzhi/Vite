package com.mars.infra.plugin.internal

import com.android.build.gradle.AppExtension
import com.mars.infra.plugin.internal.task.UploadLocalMavenTask
import com.mars.infra.plugin.internal.util.Logger
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by Mars on 2022/5/17
 */
object AarManager {

    private const val ASSEMBLE = "assemble"

    /**
     * 1. 找到对应的assemble${FlavorName}${BuildType}的任务，例如：assembleDebug
     * 2. 在assembleDebug后面插入bundleDebugTask
     * 3. 在bundleDebugTask任务后面插入upload local maven task
     */
    fun generate(appProject: Project, childProject: Project) {
        Logger.i("Aar-Manager", "start generate $childProject module aar")
        // 必须是project: app才能获取到AppExtension
        val android = appProject.extensions.getByType(AppExtension::class.java)
        android.applicationVariants.forEach {
            Logger.i("AarManager", "variant: $it")
            val assembleTaskName =
                "$ASSEMBLE${it.flavorName.capitalize()}${it.buildType.name.capitalize()}"
            val taskProvider = appProject.tasks.named(assembleTaskName) ?: throw Exception("不存在name为${assembleTaskName}的Task")

//            val loginProject = ViteTest.projectMap["login"]!!
            val bundleTask = obtainBundleTask(childProject, it.buildType.name.capitalize())

            taskProvider.configure { task ->
                task.finalizedBy(bundleTask)
            }
            bundleTask.finalizedBy(getUploadLocalMavenTask(childProject, it.buildType.name.capitalize()))
        }
    }

    // 将module的aar拷贝至LocalMavenCache
    private fun getUploadLocalMavenTask(project: Project, variantName: String): Task {
        // 1.创建task
        val task =  project.tasks.maybeCreate(
            "uploadLocalMaven${variantName}Task",
            UploadLocalMavenTask::class.java
        )
        task.config(project, variantName)
        return task
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