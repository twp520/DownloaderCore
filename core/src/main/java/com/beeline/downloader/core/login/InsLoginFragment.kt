package com.beeline.downloader.core.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.beeline.common.BaseFragment
import com.beeline.common.EmptyViewModel
import com.beeline.downloader.core.databinding.FragmentLoginBinding
import com.beeline.downloader.core.download.ins.InsCookieManager
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2022/1/29
 */
class InsLoginFragment : BaseFragment<FragmentLoginBinding, EmptyViewModel>() {

    override val viewModel: EmptyViewModel by viewModels()

    override fun createBinding(inflater: LayoutInflater): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupView() {

        binding.webview.loadUrl("https://www.instagram.com/accounts/login/")
        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.blockNetworkImage = false
        binding.webview.settings.allowFileAccess = true
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                //判断url，login接口和 register接口才存
                val cookie = CookieManager.getInstance().getCookie(url)
                LogUtils.d(url, cookie)
                if (url?.contains("https://www.instagram.com/accounts/registered") == true ||
                    url?.contains("https://www.instagram.com/accounts/onetap") == true
                ) {
                    InsCookieManager.setInsCookie(cookie)
                    //弹窗提示已经登录完成
                    lifecycleScope.launch(Dispatchers.Main) {
                        showDoneDialog()
                    }
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                val cookie = CookieManager.getInstance().getCookie(url)
                LogUtils.d(url, cookie)
            }
        }
    }

    override fun setupEvent() {
    }

    override fun setupData() {
    }

    private fun showDoneDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Congratulations")
            .setMessage("Linked to Instagram successfully, now you can download your favorite files")
            .setCancelable(false)
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
                activity?.onBackPressed()
            }
            .show()
    }
}