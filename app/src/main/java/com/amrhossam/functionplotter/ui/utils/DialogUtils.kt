package com.amrhossam.functionplotter.ui.utils

import android.R
import android.app.AlertDialog
import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DialogUtils {
    companion object {
        @DelicateCoroutinesApi
        fun showErrorDialog(ctx: Context, title: String, errorMessage: String) {
            //set icon
            GlobalScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(ctx)
//                    .setIcon(R.drawable.ic_dialog_alert) //set title
                    .setTitle(title) //set message
                    .setMessage(errorMessage) //set positive button
                    .setPositiveButton("Okay") { _, _ -> }.show()
            }

        }

    }

}