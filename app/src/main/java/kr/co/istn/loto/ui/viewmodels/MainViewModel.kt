package kr.co.istn.loto.ui.viewmodels


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.istn.loto.di.noke.NokeRepository
import kr.co.istn.loto.util.Event
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val nokeRepository: NokeRepository
) : ViewModel() {

    fun bindService(context: Context, intent: Intent) {
        context.bindService(intent, nokeRepository.mServiceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    private val _isError = MutableLiveData<Event<String?>>()
    val isError: LiveData<Event<String?>>
        get() = nokeRepository.isError

    private val _isState = MutableLiveData<Event<String?>>()
    val isState: LiveData<Event<String?>>
        get() = nokeRepository.isState

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isLocked = MutableLiveData<Boolean>().apply { value = false }
    val isLocked: LiveData<Boolean>
        get() = _isLocked

    fun setLocked(locked: Boolean) { _isLocked.value = !locked }


}
