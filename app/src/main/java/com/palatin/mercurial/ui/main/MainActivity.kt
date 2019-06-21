package com.palatin.mercurial.ui.main

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.palatin.mercurial.R
import com.palatin.mercurial.domain.service.FTPSyncService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun initService() {
        bindService(Intent(this, FTPSyncService::class.java), object : ServiceConnection {

            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            }

        }, Service.BIND_AUTO_CREATE)
    }
}
