package com.palatin.mercurial.ui.explorer

import com.airbnb.epoxy.TypedEpoxyController
import com.palatin.mercurial.data.model.RemoteFile
import com.palatin.mercurial.ui.explorer.model.file
import com.palatin.mercurial.ui.explorer.model.folder

class FilesController(private val onFolderClicked: (String) -> Unit,
                      private val onFileClicked: (RemoteFile) -> Unit) : TypedEpoxyController<List<RemoteFile>>() {


    override fun buildModels(data: List<RemoteFile>?) {
        data?.forEach {
            if(it.isFolder) {
                folder {
                    id(it.name)
                    name(it.name)
                    onClick { model, parentView, clickedView, position ->
                        onFolderClicked.invoke(it.name)
                    }
                }
            } else {
                file {
                    id(it.name)
                    name(it.name)
                    date(it.getFormattedDate())
                    size(it.getFormattedSize())
                    onClick { model, parentView, clickedView, position ->
                        onFileClicked.invoke(it)
                    }
                }
            }
        }
    }
}