package com.mars.infra.login

import com.mars.infra.account.AccountManager

/**
 * Created by Mars on 2022/5/16
 */
class LoginService: ILoginService {

    override fun login() {
        Thread.sleep(1000)
         AccountManager.getUser()
    }
}