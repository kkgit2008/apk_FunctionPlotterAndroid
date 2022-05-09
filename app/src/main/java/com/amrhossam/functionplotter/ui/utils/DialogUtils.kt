package com.amrhossam.functionplotter.ui.utils

import android.app.AlertDialog
import android.content.Context
import com.amrhossam.functionplotter.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DialogUtils {
    companion object {
        @DelicateCoroutinesApi
        fun showErrorDialog(ctx: Context, title: String, errorMessage: String) {
            GlobalScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(ctx)
                    .setTitle(title)
                    .setMessage(errorMessage)
                    .setPositiveButton(ctx.getString(R.string.okay)) { _, _ -> }.show()
            }
        }
    }
}