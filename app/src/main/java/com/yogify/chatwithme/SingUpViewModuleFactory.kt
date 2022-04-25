package com.yogify.chatwithme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SingUpViewModuleFactory(var data:Int):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingUpViewModule()as T
    }
}