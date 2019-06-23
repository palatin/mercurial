package com.palatin.mercurial.ui.explorer.model

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.palatin.mercurial.R
import kotlinx.android.synthetic.main.layout_folder.view.*

@EpoxyModelClass(layout = R.layout.layout_file)
abstract class FileModel : EpoxyModelWithHolder<FileModel.Holder>() {

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var date: String? = null

    @EpoxyAttribute
    var size: String? = null

    @EpoxyAttribute
    var onClick: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        holder.tvName.text = "$name\n$date $size"
        holder.container.setOnClickListener(onClick)
    }

    class Holder : EpoxyHolder() {

        lateinit var tvName: TextView
        lateinit var container: View

        override fun bindView(itemView: View) {
            container = itemView
            tvName = itemView.tv_file_name
        }

    }
}