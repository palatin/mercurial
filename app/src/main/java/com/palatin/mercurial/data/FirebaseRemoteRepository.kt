package com.palatin.mercurial.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.palatin.mercurial.R
import com.palatin.mercurial.data.model.FTPRemoteConfig
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FirebaseRemoteRepository {

    private val config: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setDefaults(R.xml.remote_ftp_config)
        }
    }

    private var ftpRemoteConfig: FTPRemoteConfig? = null

    fun getFTPConfig(): Single<FTPRemoteConfig> {
        return Single.create<FTPRemoteConfig> { emmiter ->
            if(ftpRemoteConfig != null) {
                emmiter.onSuccess(ftpRemoteConfig!!)
                return@create
            }
            config.fetchAndActivate().addOnCompleteListener {
                try {
                    val hostName = config.getString("host_name")
                    val serverPath = config.getString("server_path")
                    val username = config.getString("user_name")
                    val password = config.getString("user_password")
                    emmiter.onSuccess(FTPRemoteConfig(hostName, serverPath, username, password).also {
                        ftpRemoteConfig = it
                    })
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    emmiter.tryOnError(ex)
                }
            }
        }.subscribeOn(Schedulers.io())

    }
}