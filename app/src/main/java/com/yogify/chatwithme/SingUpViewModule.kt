package com.yogify.chatwithme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SingUpViewModule:ViewModel() {
    var emailid = MutableLiveData<String>()
    var password = MutableLiveData<String>()

}