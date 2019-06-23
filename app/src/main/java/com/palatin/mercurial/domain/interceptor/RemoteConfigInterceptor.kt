package com.palatin.mercurial.domain.interceptor

import com.palatin.mercurial.data.FirebaseRemoteRepository
import com.palatin.mercurial.data.model.FTPRemoteConfig
import io.reactivex.Single

class RemoteConfigInterceptor(private val firebaseRemoteRepository: FirebaseRemoteRepository) {

    fun getConfig(): Single<FTPRemoteConfig> {
        return firebaseRemoteRepository.getFTPConfig()
    }

}