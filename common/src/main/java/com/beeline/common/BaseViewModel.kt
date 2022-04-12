package com.beeline.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext


open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val httpError = MutableLiveData<HttpException>()
    val apiError = MutableLiveData<ApiException>()
    val otherError = MutableLiveData<Throwable>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        otherError.postValue(throwable)
    }

    fun launch(
        context: CoroutineContext, catch: (e: Exception) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(context + exceptionHandler) {
            try {
                block.invoke(this)
            } catch (e: Exception) {
                e.printStackTrace()
                catch.invoke(e)
                when (e) {
                    is HttpException -> {
                        httpError.postValue(e)
                    }
                    is ApiException -> {
                        apiError.postValue(e)
                    }
                    else -> {
                        otherError.postValue(e)
                    }
                }
            }
        }
    }

    protected fun launchIO(
        catch: (e: Exception) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit,
    ) {
        launch(Dispatchers.IO, catch, block)
    }

    fun launchNetRequest(
        catch: (e: Exception) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        launch(Dispatchers.Main, catch, block)
    }
}