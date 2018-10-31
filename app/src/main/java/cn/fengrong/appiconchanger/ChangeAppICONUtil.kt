package cn.fengrong.appiconchanger

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 *
 * Created by jiangyongxing on 2018/10/26.
 * 描述：
 *
 */

/**
 * 更换桌面的ICON，
 */

fun changeIcon(context: Activity, currentComponentName: String, nextComponentName: String) {
    val pm = context.packageManager
    pm.setComponentEnabledSetting(ComponentName(context, currentComponentName), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
//    pm.setComponentEnabledSetting(context.componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)

    pm.setComponentEnabledSetting(ComponentName(context, nextComponentName), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0)
    // 刷新桌面
//    restartSystemLauncher(context, pm)
}

/**
 * 是否需要重启桌面
 */
fun restartSystemLauncher(context: Context, pm: PackageManager) {
    val am = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager?
    val i = Intent(Intent.ACTION_MAIN)
    i.addCategory(Intent.CATEGORY_HOME)
    i.addCategory(Intent.CATEGORY_DEFAULT)
    val resolves = pm.queryIntentActivities(i, 0)
    for (res in resolves) {
        if (res.activityInfo != null) {
            am!!.killBackgroundProcesses(res.activityInfo.packageName)
        }
    }
}