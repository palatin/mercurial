package com.palatin.mercurial.ui.explorer.model

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.palatin.mercurial.R
import kotlinx.android.synthetic.main.layout_folder.view.*

@EpoxyModelClass(layout = R.layout.layout_folder)
abstract class FolderModel : EpoxyModelWithHolder<FolderModel.Holder>() {

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClick: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        holder.tvName.text = name
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