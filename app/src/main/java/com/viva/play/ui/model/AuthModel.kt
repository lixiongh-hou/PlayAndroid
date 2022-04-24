package com.viva.play.ui.model

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.viva.play.base.BaseModel
import com.viva.play.db.entity.PoUserInfo
import com.viva.play.service.ApiError
import com.viva.play.service.doFailure
import com.viva.play.service.doSuccess
import com.viva.play.service.request.CommonRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author 李雄厚
 *
 *
 */
@HiltViewModel
class AuthModel @Inject constructor(
    private val commonRequest: CommonRequest
) : BaseModel() {

    val account = ObservableField("")
    val password = ObservableField("")

    private val _login = MutableLiveData<PoUserInfo>()
    val login: LiveData<PoUserInfo>
        get() = _login

    fun login() {
        if (account.get().isNullOrEmpty()) {
            error.value = ApiError(message = "用户名不能为空")
            return
        }
        if (password.get().isNullOrEmpty()) {
            error.value = ApiError(message = "密码不能为空")
            return
        }
        if (password.get()!!.length < 6) {
            error.value = ApiError(message = "密码不能小于6位数")
            return
        }
        commonRequest.login(viewModelScope, account.get()!!, password.get()!!) {
            it.doSuccess { success ->
                _login.postValue(success)
            }
            it.doFailure { apiError ->
                error.postValue(apiError)
            }
        }
    }

    fun deletePassword() {
        password.set("")
    }

//    fun getCoinRecordInfo() {
//        commonRequest.getCoinRecordInfo(viewModelScope) {
//            it.doSuccess { success ->
//                _login.postValue(PoUserInfo.parse(userInfo.value!!, success))
//            }
//            it.doFailure { apiError ->
//                error.postValue(apiError)
//            }
//        }
//    }
}