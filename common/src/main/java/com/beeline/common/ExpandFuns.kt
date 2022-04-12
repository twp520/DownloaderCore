package com.beeline.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun Context.dp2px(value: Float): Int {
    val mDisplayMetrics: DisplayMetrics = resources.displayMetrics
    return (value * mDisplayMetrics.density).toInt()
}

fun AppCompatActivity.launchActivity(
    target: Class<*>,
    args: Bundle? = null,
    finish: Boolean = false,
    opt: Bundle? = null
) {
    val intent = Intent(this, target)
    args?.let {
        intent.putExtras(args)
    }
    startActivity(intent, opt)
    if (finish) finish()
}

fun AppCompatActivity.checkPermission(p: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.launchActivity(
    target: Class<*>,
    args: Bundle? = null,
    finish: Boolean = false,
    opt: Bundle? = null
) {
    val intent = Intent(requireActivity(), target)
    args?.let {
        intent.putExtras(args)
    }
    startActivity(intent, opt)
    if (finish) requireActivity().finish()
}

fun <T> BaseActivity<*, *>.getActionDataObserver(content: (data: T?) -> Unit): Observer<UIResult<T>> {
    return Observer { uiResult ->
        when (uiResult.status) {
            UI_STATUS_LOADING -> {
                showLoading()
            }
            UI_STATUS_CONTENT -> {
                dismissLoading()
                content.invoke(uiResult.content)
            }
            UI_STATUS_ERROR -> {
                dismissLoading()
                showActionError(uiResult.code, uiResult.errorMsg)
            }
        }
    }
}

// fun <T> BaseFragment<*, *>.getActionDataObserver(content: (data: T?) -> Unit): Observer<UIResult<T>> {
//     return (activity as BaseActivity<*, *>).getActionDataObserver(content)
// }

fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun ComponentActivity.getContent(callback: ActivityResultCallback<Uri?>): ActivityResultLauncher<String> {
    return registerForActivityResult(ActivityResultContracts.GetContent(), callback)
}

fun formatTime(time: Long): String {
    return SimpleDateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.ENGLISH)
        .format(Date(time * 1000))
}

fun AppCompatActivity.jumpUrl(url: String) {
    val webpage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

