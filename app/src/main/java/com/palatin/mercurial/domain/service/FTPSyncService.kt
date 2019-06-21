package com.palatin.mercurial.domain.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.palatin.mercurial.IFTPInterface
import com.palatin.mercurial.data.FirebaseRemoteRepository
import com.palatin.mercurial.domain.di.kodein
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.generic.instance

class FTPSyncService : Service() {

    private val firebaseRemoteRepository: FirebaseRemoteRepository by kodein.instance()
    private val compositeDisposable = CompositeDisposable()

    private val mBinder = object : IFTPInterface.Stub() {

        override fun fetchData() {
            this@FTPSyncService.fetchData()
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    private fun fetchData() {
        firebaseRemoteRepository.getFTPConfig()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}