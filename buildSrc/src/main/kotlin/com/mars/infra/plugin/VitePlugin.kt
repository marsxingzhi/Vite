package com.mars.infra.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Mars on 2022/5/13
 * 1. 判断每个module是否修改过
 */
class VitePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("VitePlugin apply 😄")
        Vite.checkModifiedModule(project)
    }
}