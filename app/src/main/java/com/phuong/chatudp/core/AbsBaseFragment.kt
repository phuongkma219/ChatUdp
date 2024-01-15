package com.phuong.myspa.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class  AbsBaseFragment<V : ViewDataBinding>: Fragment() {
    protected lateinit var binding: V
    val isBindingInitialized get() = this::binding.isInitialized
    private var mView: View? = null
    protected val baseActivity by lazy {
        requireActivity() as BaseActivity<*>
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (mView != null) {
            mView
        }
        else {
            binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
            binding.lifecycleOwner = this
            mView = binding.root
            initView()
            initViewModel()
            binding.root
        }
    }

    abstract fun getLayout(): Int
    abstract fun initView()
    open fun initViewModel() {}

    override fun onDestroy() {
        super.onDestroy()
        if (isBindingInitialized)
            binding.unbind()
    }

}