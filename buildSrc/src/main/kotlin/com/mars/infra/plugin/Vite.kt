package com.mars.infra.plugin

import com.mars.infra.plugin.internal.*
import com.mars.infra.plugin.internal.model.TAG_STEP_ONE
import com.mars.infra.plugin.internal.util.FileUtils
import com.mars.infra.plugin.internal.util.Logger
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.invocation.Gradle
import java.io.File

/**
 * Created by Mars on 2022/5/16
 */
object Vite {

    var mLocalMavenCache: File? = null
    var mAppProject: Project? = null

    /**
     * 1. localMaven
     */
    fun prepare(project: Project) {
        Logger.enable(true)
        mAppProject = project
        mLocalMavenCache = FileUtils.getLocalMavenCache(project)
        flatDir()
    }

    private fun flatDir() {
        val map = hashMapOf<String, File>()
        map["dirs"] = mLocalMavenCache!!
        mAppProject!!.rootProject.allprojects.forEach {
            it.repositories.flatDir(map)
        }
    }

    /**
     * 检查模块是否修改过
     */
    fun checkModifiedModule(project: Project) {
        ViteTest.collectProject(project)
        ModuleManager.prepare()
        // 可以获取到所有的模块
        project.gradle.rootProject.allprojects.filter { it.name != it.rootProject.name }.forEach {
            ModuleManager.recordModule(it)
            ModuleManager.computeModuleLastModifyTime(it)
        }
        val modifiedProjects = mutableListOf<String>()
        val lastModuleModifyInfo = FileUtils.readModuleModifyInfo()
        lastModuleModifyInfo?.let {
            it.lastModify.forEach { (project, lastModifyTime) ->
                val curMap = ModuleManager.getModuleMap().apply {
                    if (this.isEmpty()) {
                        throw Exception("last-modify module map is Empty")
                    }
                }
                if (curMap[project] != lastModifyTime) {
                    // cur project has modified
                    modifiedProjects.add(project)
                }
            }
        } ?: run {
            // 所有的项目都变更了
            val curMap = ModuleManager.getModuleMap().apply {
                if (this.isEmpty()) {
                    throw Exception("last-modify module map is Empty")
                }
            }
            modifiedProjects.addAll(curMap.keys)
        }
        modifiedProjects.forEach {
            Logger.i(TAG_STEP_ONE, "project: $it has modified")
        }
        ModuleManager.saveModifiedModules(modifiedProjects)
        ModuleManager.writeModuleModifyInfo(project, ModuleManager.getModuleMap())
    }

    fun projectsEvaluated(gradle: Gradle) {
        ModuleManager.getModifiedModules().onEach { project ->
            Logger.i("modified-modules", "project name = ${project.name}")
            mAppProject?.let {
                AarManager.generate(it, project)
            }
        }
        val accountModule = ModuleManager.getAllModuleMap()["account"]
        // 修改依赖
        modifyDependencies(accountModule!!)
    }

    private var hasModified = false

    /**
     * TODO
     * 假设account module并未改变，此时需要将account对应的aar传递给依赖account的module
     *
     * 找到依赖project的module
     */
    private fun modifyDependencies(project: Project) {
        if (hasModified) {
            return
        }
        hasModified = true
        val parentProjectMap = mutableMapOf<Project, MutableList<Configuration>>()

        mAppProject!!.gradle.rootProject.allprojects.filter { it != mAppProject!!.rootProject }.forEach {
            if (it.name == "login") {
                it.getConfigurationList().forEach { configuration ->
                    // 当前module是否依赖project
                    val isDepend = configuration.findDependencies(project)
                    // 如果是，则将account.aar以及account的child依赖全部传递到当前module
                    if (isDepend) {
                        parentProjectMap[it]?.apply {
                            add(configuration)
                        }?: kotlin.run {
                            val configList = arrayListOf<Configuration>()
                            configList.add(configuration)
                            parentProjectMap[it] = configList
                        }
                    }
                }
            }
        }

        /**
         * 依赖向上转移
         * 1. 删除原始依赖
         * 2. 添加aar依赖
         * 3. 添加child依赖
         */
        parentProjectMap.forEach {
            it.value.forEach { configuration ->
                // 1. 删除原始依赖
                configuration.dependencies.removeAll { dependency ->
                    dependency is DefaultProjectDependency && dependency.name.contains(project.name)
                }
                // 2. 添加aar依赖
                DependencyManager.addDependencyWithAar(it.key)
                // TODO 3. child依赖传递给当前module
            }
        }
    }
}

private fun Configuration.findDependencies(project: Project): Boolean {
    this.dependencies.forEach { dependency ->
        Logger.i("findDependencies", "dependency = $dependency")
        // TODO 这里不严谨. dependency.name: account-debug
        if (dependency is DefaultProjectDependency && dependency.name.contains(project.name)) {
            return true
        }
    }
    return false
}
