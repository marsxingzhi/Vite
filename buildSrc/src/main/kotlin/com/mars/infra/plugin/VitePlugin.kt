package com.mars.infra.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Mars on 2022/5/13
 */
class VitePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("VitePlugin apply 😄")
    }
}