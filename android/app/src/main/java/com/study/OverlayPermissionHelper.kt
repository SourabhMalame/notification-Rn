package com.study

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings

object OverlayPermissionHelper {

    private const val REQUEST_CODE_OVERLAY = 1002
    private const val PREFS_NAME = "permission_prefs"
    private const val KEY_OVERLAY_ASKED = "overlay_permission_asked"

    fun startPermissionFlow(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (!hasOverlayPermission(activity)) {
            askOverlayPermission(activity, prefs)
        } else if (hasOverlayPermission(activity)) {
            askBackgroundLaunchPermission(activity)
        }
    }

    private fun askOverlayPermission(activity: Activity, prefs: android.content.SharedPreferences) {
        val dialog = AlertDialog.Builder(activity).create()
        dialog.setTitle("Allow Display Over Other Apps")
        dialog.setMessage("This app needs to display over other apps to work correctly.")
        dialog.setIcon(android.R.drawable.ic_dialog_alert)

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow") { _, _ ->
            prefs.edit().putBoolean(KEY_OVERLAY_ASKED, true).apply()
            openOverlaySettings(activity)
        }

       

        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLUE)
    }

private var backgroundPermissionAsked = false

    private fun askBackgroundLaunchPermission(activity: Activity) {
        if (backgroundPermissionAsked) {
            // Already asked during this app run; skip dialog
            return
        }

        backgroundPermissionAsked = true

        val dialog = AlertDialog.Builder(activity).create()
        dialog.setTitle("Allow Display While Running in Background")
        dialog.setMessage("We recommend enabling this so modals and overlays work reliably even in the background.")
        dialog.setIcon(android.R.drawable.ic_dialog_info)

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Open Settings") { _, _ ->
            dialog.dismiss()
            openBackgroundLaunchSettings(activity)
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Later") { _, _ ->
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLUE)
    }


    private fun hasOverlayPermission(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else true
    }

    private fun openOverlaySettings(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY)
    }

    private fun openBackgroundLaunchSettings(activity: Activity) {
        val manufacturer = Build.MANUFACTURER.lowercase()
        try {
            when {
                manufacturer.contains("xiaomi") -> {
                    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity"
                    )
                    intent.putExtra("extra_pkgname", activity.packageName)
                    activity.startActivity(intent)
                }
                manufacturer.contains("oppo") -> {
                    val intent = Intent()
                    intent.setClassName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                    activity.startActivity(intent)
                }
                manufacturer.contains("vivo") -> {
                    val intent = Intent()
                    intent.setClassName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                    activity.startActivity(intent)
                }
                else -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${activity.packageName}")
                    activity.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivity(intent)
        }
    }

    fun checkPermissionsAfterReturn(activity: Activity) {
        if (hasOverlayPermission(activity)) {
            // User denied, but don't ask again unless manually reset prefs
            return
        }
    }
}

