package com.mars.infra.plugin.internal

/**
 * Created by Mars on 2022/5/16
 */
object Logger {

    private var mEnable = false

    fun enable(enable: Boolean) {
        mEnable = enable
    }

    fun i(tag: String, msg: String) {
        if (!mEnable) return
        println("$tag: >>> $msg")
    }
}