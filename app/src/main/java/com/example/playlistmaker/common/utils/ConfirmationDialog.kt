package com.example.playlistmaker.common.utils

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmationDialog(private val context: Context) {

    fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit,
        positiveColor: Int? = null,
        negativeColor: Int? = null
    ) {

        val posColor = positiveColor
            ?: context.getThemeColor(com.google.android.material.R.attr.colorOnPrimary)
        val likeColor = negativeColor ?: ContextCompat.getColor(context, R.color.like_color)

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                positiveAction()
            }
            .setNegativeButton(negativeButton) { dialog, which ->
                negativeAction()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(posColor)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(likeColor)
    }
}