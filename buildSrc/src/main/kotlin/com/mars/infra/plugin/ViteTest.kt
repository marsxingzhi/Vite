package com.mars.infra.plugin

import com.google.gson.Gson
import com.mars.infra.plugin.internal.*
import com.mars.infra.plugin.internal.model.MODULE_LAST_MODIFY
import com.mars.infra.plugin.internal.model.ModuleData
import com.mars.infra.plugin.internal.util.FileUtils
import com.mars.infra.plugin.internal.util.Logger
import org.gradle.api.Project
import java.io.File

/**
 * Created by Mars on 2022/5/16
 *
 * 测试代码
 */
object ViteTest {

    private val projectMap = mutableMapOf<String, Project>()

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

    /**
     * 收集项目中的project
     */
    fun collectProject(project: Project) {
        project.rootProject.allprojects.forEach {
            projectMap[it.name] = it
        }
        projectMap.forEach { (name, project) ->
            Logger.i("ViteTest", "name = $name, project = $project")
        }
    }

    /**
     * 测试：在login module中添加base-util module的依赖
     */
    fun addDependency() {
        try {
            val loginProject = ModuleManager.getAllModuleMap()["login"]
            val configName = "implementation"
            val map = hashMapOf<String, String>()
            map["name"] = "account-debug"
            map["ext"] = "aar"
            loginProject!!.dependencies.add(configName, map)
        } catch (ignore: Exception) {

        }

    }

    /**
     * 获取module的依赖
     */
    fun getModuleDependencies() {
        val module = ModuleManager.getAllModuleMap()["feed"]
        val configurationsImpl = module!!.configurations.maybeCreate("implementation")
        configurationsImpl.dependencies.forEach {
            Logger.i("getModuleDependencies", "implementation---dependency = $it")
        }
        val configurationsApi =  module.configurations.maybeCreate("api")
        configurationsApi.dependencies.forEach {
            Logger.i("getModuleDependencies", "api---dependency = $it")
        }
    }
}