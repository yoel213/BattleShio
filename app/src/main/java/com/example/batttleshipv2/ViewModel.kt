package com.example.batttleshipv2

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
    crossinline provider: () -> T
) = ViewModelProvider(this, object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        provider() as T
})[T::class.java]