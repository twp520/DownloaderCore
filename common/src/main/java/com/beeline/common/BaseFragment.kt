package com.beeline.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<B : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var binding: B

    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = createBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupEvent()
        setupData()
        viewModel.httpError.observe(viewLifecycleOwner) {
            val act = activity as BaseActivity<*, *>
            act.dismissLoading()
            act.showActionError(it.code(), it.message())
            customHandleHttpExp()
        }
        viewModel.otherError.observe(viewLifecycleOwner) {
            val act = activity as BaseActivity<*, *>
            act.showActionError(CODE_NET_ERROR, null)
            customHandleHttpExp()
        }
        viewModel.apiError.observe(viewLifecycleOwner) {
            val act = activity as BaseActivity<*, *>
            act.showActionError(it.code, it.msg)
        }
    }

    abstract fun createBinding(inflater: LayoutInflater): B

    abstract fun setupView()

    abstract fun setupEvent()

    abstract fun setupData()

    open fun customHandleHttpExp() {

    }
}