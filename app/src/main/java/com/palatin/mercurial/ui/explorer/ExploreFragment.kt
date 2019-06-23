package com.palatin.mercurial.ui.explorer


import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.palatin.mercurial.BuildConfig
import com.palatin.mercurial.IFTPInterface

import com.palatin.mercurial.R
import com.palatin.mercurial.data.Resource
import com.palatin.mercurial.data.model.RemoteFile
import com.palatin.mercurial.domain.service.FTPSyncService
import com.palatin.mercurial.ui.image_viewer.ImageViewerFragment
import com.palatin.mercurial.ui.main.FtpActionListener
import com.palatin.mercurial.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_explore.*
import java.io.File
import java.lang.StringBuilder
import java.util.*


class ExploreFragment : Fragment(), FtpActionListener {



    private lateinit var vm: ExplorerVM
    private val controller: FilesController by lazy {
        FilesController(onFolderClicked = {
            vm.moveToFolder(it)
        }, onFileClicked = {
            getFile(it)
        })
    }

    private val onBackPressed = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            vm.moveBack()
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            vm.disconnect()
            initService()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            vm.initFTPInterface(IFTPInterface.Stub.asInterface(service ?: return).also {
                ftpInterface = it
            })
        }

    }

    private var ftpInterface: IFTPInterface? = null
    private var mPendingFile: RemoteFile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(this).get(ExplorerVM::class.java)
        initService()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireActivity() as? MainActivity)?.listener = this
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressed)
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        vm.state.observe(viewLifecycleOwner, Observer {
            if(it.folder.status == Resource.Status.LOADING) progress.show() else progress.hide()
            controller.setData(it.folder.data)
            onBackPressed.isEnabled = it.path.isNotEmpty()
            if(it.folder.status == Resource.Status.ERROR) {
                Snackbar.make(view, it.folder.message ?: "", Snackbar.LENGTH_INDEFINITE).setAction("Retry", {
                    vm.initFTPInterface(ftpInterface ?: return@setAction)
                }).show()
            }
            it.requestedFile.data?.let {
                openFile(it)
            }
            setPath(it.path)
            (requireActivity() as AppCompatActivity).supportActionBar!!.title = it.hostName
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            vm.getFile(mPendingFile ?: return)
        }
        mPendingFile = null
    }

    override fun newFolder(name: String) {
        vm.newFolder(name)
    }

    override fun addFile(path: Uri) {
        vm.newFile(path)
    }

    private fun setUi() {
        rv_files.setController(controller)
        rv_files.itemAnimator = null
        (requireActivity() as AppCompatActivity).let {
            it.setSupportActionBar(toolbar)
            it.supportActionBar!!.title = "Loading"
        }

    }

    private fun openFile(filePath: String) {
        var type: String? = null
        MimeTypeMap.getFileExtensionFromUrl(filePath)?.let {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(it)
        }
        if(type != null && type!!.split("/").getOrNull(0) == "image") {
            ImageViewerFragment().apply {
                arguments = bundleOf("image" to filePath)
            }.show(childFragmentManager, null)
            return
        }
        val file = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file), type)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if(intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun getFile(file: RemoteFile) {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            mPendingFile = file
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
        } else {
            vm.getFile(file)
        }
    }


    private fun initService() {
        requireActivity().bindService(Intent(requireContext(), FTPSyncService::class.java), connection, Service.BIND_AUTO_CREATE)
    }

    private fun setPath(path: Stack<String>) {
        var mPath = "/root"
        if(path.isNotEmpty())
            mPath += "/${path.joinToString(separator = "/")}"
        tv_path.text = mPath
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(connection)
        (requireActivity() as? MainActivity)?.listener = null
    }

}
