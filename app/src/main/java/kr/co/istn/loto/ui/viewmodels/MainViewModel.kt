package kr.co.istn.loto.ui.viewmodels


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.kr.istn.smartLock.LockInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.istn.loto.di.imate.ImateRepository
import kr.co.istn.loto.di.noke.NokeRepository
import kr.co.istn.loto.util.Event
import javax.inject.Inject
import kr.co.istn.loto.di.Result

@HiltViewModel
class MainViewModel @Inject constructor(
    private val nokeRepository: NokeRepository,
    private val imateRepository: ImateRepository
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

    fun getLockInfo(lockInfo: LockInfo) = viewModelScope.launch {
        imateRepository.getLockInfo(LockInfo("", "01089247994", "", "")).let { result ->
            if(result is Result.Success) {
                val test = result.data
            } else if(result is Result.Error) {

            }
        }
    }


}
