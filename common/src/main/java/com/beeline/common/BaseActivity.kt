package com.beeline.common

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<B : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var binding: B

    protected abstract val mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<*>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as B
            setContentView(binding.root)
        }

        setupView()
        setupEvent()
        setupData()

    }

    abstract fun setupView()

    abstract fun setupEvent()

    abstract fun setupData()


    open fun showLoading() {

    }

    open fun dismissLoading() {

    }

    open fun showActionError(code: Int, msg: String?) {
        if (code == CODE_TOKEN_EXP) {
            AlertDialog.Builder(this)
                .setMessage("Login")
                .setPositiveButton("longin") { dialog, witch ->
                    dialog.dismiss()

                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setCancelable(false)
                .create()
                .apply {
                    setCanceledOnTouchOutside(false)
                }
                .show()
            return
        }
        if (msg == null || code == CODE_NET_ERROR) {
            showToast(getString(R.string.toast_net_error))
        } else {
            showToast(msg)
        }
    }

}