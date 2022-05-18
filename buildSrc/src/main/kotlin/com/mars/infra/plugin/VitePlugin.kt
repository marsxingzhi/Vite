package com.mars.infra.plugin

import com.mars.infra.plugin.internal.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Mars on 2022/5/13
 * 1. 判断每个module是否修改过
 */
class VitePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        Logger.i("Vite-Plugin", "apply 😄, project: $project")
        Vite.prepare(project)
        Vite.checkModifiedModule(project)

        // 所有项目的build.gradle执行完成后，回调的
        project.gradle.projectsEvaluated {
            Logger.i("Vite-Plugin", "$it")
            Vite.projectsEvaluated(it)
            ViteTest.addDependency()
        }
    }
}