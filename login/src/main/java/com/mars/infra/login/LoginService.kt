package com.mars.infra.login

import android.util.Log
import com.mars.infra.account.AccountManager

//import com.mars.infra.account.AccountManager

//import com.mars.infra.base.util.Utils

/**
 * Created by Mars on 2022/5/16
 */
class LoginService: ILoginService {

    override fun login() {
        Thread.sleep(1000)
//        val deviceId = Utils.getDeviceId()
//        Log.e("gy", "LoginService---login---deviceId = $deviceId")

        AccountManager.getUser()
    }
}