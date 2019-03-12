package lab3.camera2you.dialogs

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import lab3.camera2you.R

class ConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(activity!!)
            .setMessage(R.string.permission_request)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                parentFragment?.requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    ), 1
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                parentFragment?.activity?.finish()
            }
            .create()
}