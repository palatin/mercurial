package com.palatin.mercurial.domain.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import com.palatin.mercurial.IFTPConnectionInterface
import com.palatin.mercurial.IFTPInterface
import com.palatin.mercurial.data.FirebaseRemoteRepository
import com.palatin.mercurial.data.Resource
import com.palatin.mercurial.data.model.FTPRemoteConfig
import com.palatin.mercurial.data.model.RemoteFile
import com.palatin.mercurial.domain.di.kodein
import com.palatin.mercurial.domain.interceptor.FTPInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.generic.instance

class FTPSyncService : Service() {

    private val compositeDisposable = CompositeDisposable()
    private val ftpInterceptor: FTPInterceptor by kodein.instance()

    private val mBinder = object : IFTPInterface.Stub() {


        override fun connect(config: FTPRemoteConfig, callback: IFTPConnectionInterface) {
            compositeDisposable.clear()
            compositeDisposable.add(ftpInterceptor.connect(config).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.complete(it.status == Resource.Status.SUCCESS, it.message)
                }, {
                    it.printStackTrace()
                }))
        }

        override fun fetchData(parent: String, callback: IFTPConnectionInterface) {
            compositeDisposable.clear()
            compositeDisposable.add(ftpInterceptor.getCatalog(parent).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.onCatalogFetched(it,null)
                }, {
                    it.printStackTrace()
                    callback.onCatalogFetched(null, it.message)
                }))
        }

        override fun getFile(file: RemoteFile, callback: IFTPConnectionInterface) {
            compositeDisposable.clear()
            compositeDisposable.add(ftpInterceptor.getFile(file).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.onFileDownloaded(it,null)
                }, {
                    it.printStackTrace()
                    callback.onCatalogFetched(null, it.message)
                }))
        }

        override fun newFolder(folderName: String, callback: IFTPConnectionInterface) {
            compositeDisposable.clear()
            compositeDisposable.add(ftpInterceptor.newFolder(folderName).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.onCreationCompleted(true,null)
                }, {
                    it.printStackTrace()
                    callback.onCreationCompleted(false, it.message)
                }))
        }

        override fun addFile(path: Uri, callback: IFTPConnectionInterface) {
            compositeDisposable.clear()
            compositeDisposable.add(ftpInterceptor.addFile(path).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    callback.onCreationCompleted(true,null)
                }, {
                    it.printStackTrace()
                    callback.onCreationCompleted(false, it.message)
                }))
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }



    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}