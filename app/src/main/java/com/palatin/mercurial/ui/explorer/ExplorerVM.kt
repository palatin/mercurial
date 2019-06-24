package com.palatin.mercurial.ui.explorer

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.palatin.mercurial.AUTO_UPDATE_TIME_RATE
import com.palatin.mercurial.IFTPConnectionInterface
import com.palatin.mercurial.IFTPInterface
import com.palatin.mercurial.data.Resource
import com.palatin.mercurial.data.model.FTPRemoteConfig
import com.palatin.mercurial.data.model.RemoteFile
import com.palatin.mercurial.domain.di.kodein
import com.palatin.mercurial.domain.interceptor.RemoteConfigInterceptor
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class ExplorerVM : ViewModel() {

    private val disposable = CompositeDisposable()
    private val remoteConfigInterceptor: RemoteConfigInterceptor by kodein.instance()
    private var ftpRemoteConfig: FTPRemoteConfig? = null
    private var ftpInterface: IFTPInterface? = null
    private val mPendingPath: Stack<String> = Stack()
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable: Runnable = Runnable {
        ftpInterface?.let {
            mPendingPath.clear()
            mPendingPath.addAll(viewState.path)
            viewState = viewState.loading()
            it.fetchData(getFolderPath(), ftpConnectionInterface)
        }
    }

    private var viewState = ViewState()
    set(value) {
        field = value
        _state.value = value
    }

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState>
    get() = _state

    private val ftpConnectionInterface = object : IFTPConnectionInterface.Stub() {


        override fun complete(result: Boolean, message: String?) {
            if(result) {
                viewState = viewState.copy(isConnected = true)
                updateHandler.post(updateRunnable)
            } else {
                viewState = viewState.copy(folder = Resource.error(message, viewState.folder.data))
            }
        }

        override fun onCatalogFetched(files: MutableList<RemoteFile>?, errorMessage: String?) {
            viewState = if(errorMessage != null) {
                viewState.copy(folder = Resource.error(errorMessage, viewState.folder.data), isConnected = false)
            } else {
                updateHandler.postDelayed(updateRunnable, AUTO_UPDATE_TIME_RATE)
                viewState.path.clear()
                viewState.path.addAll(mPendingPath)
                viewState.copy(folder = Resource.success(files))
            }
        }

        override fun onFileDownloaded(filePath: String?, errorMessage: String?) {
            updateHandler.postDelayed(updateRunnable, AUTO_UPDATE_TIME_RATE)
            viewState = if(errorMessage != null) {
                viewState.copy(folder = Resource.error(errorMessage, viewState.folder.data))
            } else {
                viewState.copy(folder =  Resource.unit(viewState.folder.data), requestedFile = Resource.success(filePath))
            }
        }

        override fun onCreationCompleted(result: Boolean, message: String?) {
            if(result)
                updateRunnable.run()
            else {
                viewState = viewState.copy(folder = Resource.unit( viewState.folder.data))
            }
        }

    }



    fun initFTPInterface(ftpInterface: IFTPInterface) {
        updateHandler.removeCallbacks(updateRunnable)
        viewState = viewState.copy(isConnected = false, folder = Resource.unit(viewState.folder.data))
        this.ftpInterface = ftpInterface
        disposable.add(fetchRemoteConfig().subscribe({
            viewState = viewState.copy(hostName = ftpRemoteConfig!!.hostName)
            ftpInterface.connect(ftpRemoteConfig!!, ftpConnectionInterface)
        }, {
            it.printStackTrace()
        }))
    }


    fun moveToFolder(folderName: String) {
        updateHandler.removeCallbacks(updateRunnable)
        mPendingPath.clear()
        mPendingPath.addAll(viewState.path)
        mPendingPath.push(folderName)
        fetchData()
    }

    fun moveBack() {
        mPendingPath.clear()
        mPendingPath.addAll(viewState.path)
        mPendingPath.pop()
        fetchData()
    }

    fun getFile(remoteFile: RemoteFile) {
        ftpInterface?.let {
            updateHandler.removeCallbacks(updateRunnable)
            viewState = viewState.loading()
            it.getFile(getFolderPath(), remoteFile, ftpConnectionInterface)
        }
    }

    fun newFolder(folderName: String) {
        ftpInterface?.let {
            updateHandler.removeCallbacks(updateRunnable)
            viewState = viewState.loading()
            it.newFolder(folderName, getFolderPath(), ftpConnectionInterface)
        }
    }

    fun newFile(path: Uri) {
        ftpInterface?.let {
            updateHandler.removeCallbacks(updateRunnable)
            viewState = viewState.loading()
            it.addFile(path, getFolderPath(), ftpConnectionInterface)
        }
    }

    fun disconnect() {
        ftpInterface = null
        updateHandler.removeCallbacks(updateRunnable)
        viewState = viewState.copy(isConnected = false)
    }

    private fun fetchData() {
        ftpInterface?.let {
            updateHandler.removeCallbacks(updateRunnable)
            viewState = viewState.loading()
            it.fetchData(getFolderPath(), ftpConnectionInterface)
        }
    }

    private fun fetchRemoteConfig(): Completable {
        return remoteConfigInterceptor.getConfig()
            .doOnSubscribe { viewState = viewState.loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { ftpRemoteConfig = it }.ignoreElement()

    }

    private fun getFolderPath(): String {
        return mPendingPath.joinToString(separator = ".")
    }

    data class ViewState(val hostName: String = "Loading", val folder: Resource<List<RemoteFile>> = Resource.unit(),
                         val isConnected: Boolean = false, val path: Stack<String> = Stack(),
                         val requestedFile: Resource<String> = Resource.unit()) {

        fun loading(): ViewState = copy(folder = Resource.loading(folder.data), requestedFile = Resource.unit())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        updateHandler.removeCallbacks(updateRunnable)
    }
}