package com.mars.infra.plugin

import com.google.gson.Gson
import com.mars.infra.plugin.internal.FileUtils
import com.mars.infra.plugin.internal.MODULE_LAST_MODIFY
import com.mars.infra.plugin.internal.ModuleData
import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 *
 * 测试代码
 */
object ViteTest {

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
}