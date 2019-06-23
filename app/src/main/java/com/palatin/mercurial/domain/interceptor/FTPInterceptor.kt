package com.palatin.mercurial.domain.interceptor

import android.content.Context
import android.os.Environment
import androidx.core.os.EnvironmentCompat
import com.palatin.mercurial.data.FTPClient
import com.palatin.mercurial.data.Resource
import com.palatin.mercurial.data.model.FTPRemoteConfig
import com.palatin.mercurial.data.model.RemoteFile
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.net.ftp.FTPFile
import java.io.File

class FTPInterceptor(private val ftpClient: FTPClient) {

    fun connect(ftpRemoteConfig: FTPRemoteConfig): Single<Resource<Unit>> {
        return Single.create<Resource<Unit>> { emitter ->
            when(ftpClient.connect(ftpRemoteConfig)) {
                is FTPClient.Status.Connected -> emitter.onSuccess(Resource.success(Unit))
                is FTPClient.Status.InvalidCredentials -> emitter.onSuccess(Resource.error("Invalid server credentials", Unit))
                is FTPClient.Status.Unknown -> emitter.onSuccess(Resource.error("Unknown connection error", Unit))
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getCatalog(parent: String): Single<List<RemoteFile>> {
        return Single.create<List<RemoteFile>> { emitter ->
            val res = ftpClient.getFiles(parent)
            if(res.status == Resource.Status.SUCCESS) {
                emitter.onSuccess(res.data!!.map { RemoteFile(it.name, it.isDirectory, it.timestamp.timeInMillis, it.size) })
            } else {
                emitter.tryOnError(Exception(res.message))
            }
        }.subscribeOn(Schedulers.io())
    }

    fun getFile(remoteFile: RemoteFile): Single<String> {
        return Single.create<String> {  emitter ->

            val file = File(Environment.getExternalStorageDirectory().absolutePath + "/" + remoteFile.name)
            if(file.exists()) {
                if(file.lastModified() == remoteFile.date) {
                    emitter.onSuccess(file.absolutePath)
                    return@create
                }
                else {
                    file.delete()
                }
            }
            val res = ftpClient.getFile(remoteFile.name, file.outputStream())
            if(res.status == Resource.Status.SUCCESS) {
                emitter.onSuccess(file.path)
            } else {
                emitter.tryOnError(Exception(res.message))
            }
        }.subscribeOn(Schedulers.io())
    }

}