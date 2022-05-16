package com.mars.infra.plugin.internal

import com.android.build.gradle.AppExtension
import org.gradle.api.Project

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
            val assembleTaskName = "$ASSEMBLE${it.flavorName.capitalize()}${it.buildType.name.capitalize()}"
        }
    }
}