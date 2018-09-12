package me.smbduknow.transport.presentation.misc;

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissedAction(
        private val permission: String,
        private val onAccepted: () -> Unit,
        private val onDenied: () -> Unit
) {

    private val REQUEST_CODE_PERMISSION = 101

    fun invoke(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE_PERMISSION)
        } else {
            onAccepted()
        }
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onAccepted()
                } else {
                    onDenied()
                }
            }
        }
    }

}