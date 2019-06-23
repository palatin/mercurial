// IFTPConnectionInterface.aidl
package com.palatin.mercurial;


import com.palatin.mercurial.data.model.RemoteFile;

interface IFTPConnectionInterface {

    void complete(boolean result, String message);

    void onCatalogFetched(in List<RemoteFile> files, String errorMessage);

    void onFileDownloaded(in String filePath, String errorMessage);
}
