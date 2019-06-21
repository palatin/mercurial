package com.palatin.mercurial.ui.explorer

import androidx.lifecycle.ViewModel
import com.palatin.mercurial.data.model.FTPRemoteConfig
import com.palatin.mercurial.domain.di.kodein
import com.palatin.mercurial.domain.interceptor.RemoteConfigInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.generic.instance

class ExplorerVM : ViewModel() {

    private val disposable = CompositeDisposable()
    private val remoteConfigInterceptor: RemoteConfigInterceptor by kodein.instance()
    private var ftpRemoteConfig: FTPRemoteConfig? = null

    init {
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        disposable.add(remoteConfigInterceptor.getConfig()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ftpRemoteConfig = it
            }, {
                it.printStackTrace()
            }))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}