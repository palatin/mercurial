package com.palatin.mercurial.data

import com.palatin.mercurial.data.model.FTPRemoteConfig
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.URI

class FTPClient {

    private var ftpClient: FTPClient? = null

    fun connect(ftpRemoteConfig: FTPRemoteConfig): Status {
        return try {
            dissconnect()
            ftpClient = FTPClient()
            ftpClient!!.defaultTimeout = 5000
            ftpClient!!.connect(URI(ftpRemoteConfig.link).host)
            ftpClient!!.enterLocalPassiveMode()
            if(!ftpClient!!.login(ftpRemoteConfig.username, ftpRemoteConfig.password))
                throw InvalidCredentialsException()
            Status.Connected
        } catch (ex: InvalidCredentialsException) {
            Status.InvalidCredentials
        } catch (ex: Exception) {
            Status.Unknown
        }
    }

    fun dissconnect() {
        try {
            ftpClient?.logout()
            ftpClient?.disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getFiles(parent: String): Resource<Array<FTPFile>> {
        return try {
            Resource.success(ftpClient?.listFiles(parent))
        } catch (ex: Exception) {
            Resource.error(ex.localizedMessage, null)
        }
    }

    fun getFile(fileName: String, outStream: OutputStream): Resource<Unit> {
        return try {
            if(ftpClient?.retrieveFile(fileName, outStream) == true)
                Resource.success(Unit)
            else Resource.error(null, Unit)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Resource.error(ex.localizedMessage, Unit)
        }

    }

    fun newFolder(folderName: String): Resource<Unit> {
        return try {
            if(ftpClient?.makeDirectory(folderName) == true)
                Resource.success(Unit)
            else Resource.error(null, Unit)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Resource.error(ex.localizedMessage, Unit)
        }
    }

    fun addFile(filePath: String, inputStream: InputStream): Resource<Unit> {
        return try {
            if(ftpClient?.storeFile(filePath, inputStream) == true)
                Resource.success(Unit)
            else Resource.error(null, Unit)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Resource.error(ex.localizedMessage, Unit)
        } finally {
            inputStream.close()
        }
    }

    sealed class Status {
        object Connected : Status()
        object InvalidCredentials : Status()
        object Unknown : Status()
    }
}

class InvalidCredentialsException : Exception()