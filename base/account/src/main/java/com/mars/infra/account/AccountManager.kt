package com.mars.infra.account

import android.util.Log

/**
 * Created by Mars on 2022/5/19
 */
object AccountManager {

    fun getUser(): User {
//        Log.e("gy", "这个是来自aar的打印日志")
        Log.e("gy", "这是来自project的打印日志")
        return User("zhangsan", 18)
    }
}