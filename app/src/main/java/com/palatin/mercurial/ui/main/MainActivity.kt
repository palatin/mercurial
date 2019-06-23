package com.palatin.mercurial.ui.main

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.palatin.mercurial.App
import com.palatin.mercurial.R
import com.palatin.mercurial.util.Theme
import com.palatin.mercurial.util.Util
import com.palatin.prettydialog.PrettyDialog
import com.palatin.prettydialog.PrettyDialogViewDelegate
import com.palatin.prettydialog.ext.DialogButtonCallback
import com.palatin.prettydialog.ext.button




class MainActivity : AppCompatActivity() {

    var listener: FtpActionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Util.getTheme(applicationContext))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.explore_menu, menu)
        menu?.findItem(R.id.dark_theme)?.isChecked = Util.getTheme(applicationContext) == R.style.AppThemeDark
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.new_folder -> {
                showNewFolderDialog()
            }
            R.id.upload_file -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, 2000)
            }
            R.id.dark_theme -> {
                Util.setTheme(applicationContext, if(item.isChecked) Theme.LIGHT else Theme.DARK)
                recreate()
            }
        }
        return true
    }

    private fun showNewFolderDialog() {
        PrettyDialog(this, PrettyDialogViewDelegate(theme =
        if(Util.getTheme(applicationContext) == R.style.AppThemeLight) PrettyDialogViewDelegate.Theme.LIGHT else PrettyDialogViewDelegate.Theme.DARK)
        ).message("Enter folder name")
            .input(tag = "folder_name", t = object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            .button(title = "Create", type = PrettyDialog.ButtonType.NEUTRAL, dismiss = true, callback = { d, v, t ->
                listener?.newFolder((d.getViewsByTag("folder_name").first() as TextInputLayout).editText!!.text.toString())
            })
            .button(title = "Cancel", type = PrettyDialog.ButtonType.NEGATIVE, dismiss = true).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 2000 && resultCode == Activity.RESULT_OK) {
            listener?.addFile(data?.data ?: return)
        }
    }
}

interface FtpActionListener {
    fun newFolder(name: String)
    fun addFile(path: Uri)
}
