package com.zerox.camera.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat


object Permission {


    val cameraPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


    private fun Context.checkPermissions(vararg permissions: String): Boolean =
        permissions.all { permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

    fun Context.launchTaskWithPermissions(
        task: () -> Unit,
        permissionsRequest: () -> Unit,
        permissions: Array<String>
    ) {
        val isPermissionsGranted = checkPermissions(permissions = permissions)
        if (isPermissionsGranted)
            task()
        else
            permissionsRequest()

    }

}