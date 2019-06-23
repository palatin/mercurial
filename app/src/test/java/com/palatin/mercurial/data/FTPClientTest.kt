package com.palatin.mercurial.data

import com.palatin.mercurial.data.model.FTPRemoteConfig
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class FTPClientTest {

    private lateinit var ftpClient: FTPClient

    @Before
    fun setUp() {
        ftpClient = FTPClient()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun connectSuccessfully() {
        val res = ftpClient.connect(FTPRemoteConfig("ftp://ftp.dlptest.com/", "dlpuser@dlptest.com", "fLDScD4Ynth0p4OJ6bW6qCxjh"))
        assertTrue(res is FTPClient.Status.Connected)
    }

    @Test
    fun connectWithInvalidCredentials() {
        val res = ftpClient.connect(FTPRemoteConfig("ftp://ftp.dlptest.com/", "dlpuser@dlptest.com", "fLDScD4OJ6bW6qCxjh"))
        assertTrue(res is FTPClient.Status.InvalidCredentials)
    }


}