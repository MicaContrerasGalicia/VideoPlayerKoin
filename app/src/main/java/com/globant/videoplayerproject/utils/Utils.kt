package com.globant.videoplayerproject.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.R
import java.text.SimpleDateFormat

class Utils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat(ACTUAL_DATE_FORMAT)
        val outputFormat = SimpleDateFormat(REQUIRED_DATE_FORMAT)
        val dateObject = inputFormat.parse(date)
        return outputFormat.format(dateObject)
    }

    fun adaptImageUrl(imageUrl: String): String{
        return imageUrl.replace(ACTUAL_WIDTH_HEIGHT_IMAGE, REQUIRED_WIDTH_HEIGHT)
    }

    fun adaptTypeToken(token: String): String{
        val type = token.replace(ACTUAL_AUTHENTICATION_TYPE, CORRECT_AUTHENTICATION_TYPE)
        return type.plus("")
    }

    interface OptionCallback<String> {
        fun onOption(option: String)
    }

    fun <String> selectOption(context: Context, options: List<String>, callback: OptionCallback<String>) {
        val itemsAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.select_dialog_item, options)
        AlertDialog.Builder(context)
            .setNegativeButton(R.string.cancel, null)
            .setAdapter(
                itemsAdapter
            ) { _: DialogInterface?, which: Int ->
                callback.onOption(
                    options[which]
                )
            }.show()
    }

    fun showDialog(context: Context, title: String, message: String?, positiveButton: String,  positiveAction: (() -> Unit)? = null, negativeButton:String?= null , negativeAction: (() -> Unit)? = null){
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButton) { _, _ -> positiveAction?.invoke() }
            setNegativeButton(negativeButton){_, _ -> negativeAction?.invoke() }
            create()
            show()
        }
    }
}