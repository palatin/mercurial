// IFTPInterface.aidl
package com.palatin.mercurial;

// Declare any non-default types here with import statements
import com.palatin.mercurial.data.model.FTPRemoteConfig;
import com.palatin.mercurial.IFTPConnectionInterface;
import com.palatin.mercurial.data.model.RemoteFile;

interface IFTPInterface {

    void connect(in FTPRemoteConfig config, in IFTPConnectionInterface callback);
    void fetchData(in String parent, in IFTPConnectionInterface callback);
    void getFile(in String path, in RemoteFile file, in IFTPConnectionInterface callback);
    void newFolder(in String folderName, in String folderPath, in IFTPConnectionInterface callback);
    void addFile(in Uri path, in String folderPath, in IFTPConnectionInterface callback);
}
