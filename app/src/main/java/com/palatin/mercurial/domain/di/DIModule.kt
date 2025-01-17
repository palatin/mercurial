package com.palatin.mercurial.domain.di

import com.palatin.mercurial.data.FTPClient
import com.palatin.mercurial.data.FirebaseRemoteRepository
import com.palatin.mercurial.domain.interceptor.FTPInterceptor
import com.palatin.mercurial.domain.interceptor.RemoteConfigInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.*

val kodein = Kodein {
    bind<FirebaseRemoteRepository>() with singleton { FirebaseRemoteRepository() }
    bind<RemoteConfigInterceptor>() with provider { RemoteConfigInterceptor(instance()) }

    bind<FTPClient>() with singleton { FTPClient() }
    bind<FTPInterceptor>() with provider { FTPInterceptor(instance()) }
}