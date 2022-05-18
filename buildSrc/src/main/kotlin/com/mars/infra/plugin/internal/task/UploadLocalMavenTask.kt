package com.mars.infra.plugin.internal.task

import com.mars.infra.plugin.Vite
import com.mars.infra.plugin.internal.FileUtils
import com.mars.infra.plugin.internal.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.jetbrains.annotations.TestOnly
import java.io.File

/**
 * Created by Mars on 2022/5/18
 *
 * 将module生成的aar拷贝至本地LocalMaven的cache目录下
 */
open class UploadLocalMavenTask : DefaultTask() {

    private var start = 0L
    private var mProjectName: String? = null
    private var mProject: Project? = null
    private var mVariantName: String? = null

    fun config(project: Project, variantName: String) {
        mProject = project
        mProjectName = project.name
        mVariantName = variantName.lowercase()
    }

    @TaskAction
    fun upload() {
        start -= System.currentTimeMillis()
        Logger.i("UploadLocalMavenTask", "start upload task")
        val localMavenCache =
            Vite.mLocalMavenCache ?: FileUtils.getLocalMavenCache(Vite.mAppProject)
        val destFile = File(localMavenCache, "$mProjectName-$mVariantName.aar")  // 需要加入.aar后缀

        if (destFile.exists()) {
            destFile.delete()
        }

        val srcPath = "${mProject!!.buildDir}/outputs/aar/${mProjectName}-$mVariantName.aar"

        val srcFile = File(srcPath).apply {
            if (!exists()) {
                throw Exception("${mProjectName}-$mVariantName.aar not exists")
            }
        }

        srcFile.copyTo(destFile)
        start += System.currentTimeMillis()
        Logger.i("UploadLocalMavenTask", "complete upload task, cost $start ms")
    }

    @TestOnly
    fun uploadTest() {
        val localMavenCache =
            Vite.mLocalMavenCache ?: FileUtils.getLocalMavenCache(Vite.mAppProject)
        val destFile = File(localMavenCache, "login-debug1.aar")  // 需要加入.aar后缀

        if (destFile.exists()) {
            destFile.delete()
        }

        val srcFile =
            File("/Users/geyan/projects/github/Vite/login/build/outputs/aar/login-debug.aar")

        srcFile.copyTo(destFile)
        Logger.i("UploadLocalMavenTask", "start upload task")
    }
}