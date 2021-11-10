package kr.co.istn.loto.di.noke

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.L
import com.noke.nokemobilelibrary.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kr.co.istn.loto.R
import kr.co.istn.loto.util.Event
import javax.inject.Inject

class NokeRepository @Inject constructor(
    private var nokeService: NokeDeviceManagerService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    var isError = MutableLiveData<Event<String?>>()
    var isState= MutableLiveData<Event<String?>>()

    val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.w("NOKE_SERVICE", "ON SERVICE CONNECTED")
            val binder = service as NokeDeviceManagerService.LocalBinder
            nokeService = binder.getService(NokeDefines.NOKE_LIBRARY_SANDBOX)
            nokeService.setAllowAllDevices(true)
            nokeService.registerNokeListener(mNokeServiceListener)
            nokeService.startScanningForNokeDevices()
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.w("NOKE_SERVICE", "ON SERVICE DISCONNECTED")
        }
    }

    val mNokeServiceListener: NokeServiceListener = object : NokeServiceListener {
        override fun onNokeDiscovered(noke: NokeDevice?) {
            nokeService.connectToNoke(noke)
        }

        override fun onNokeConnecting(noke: NokeDevice?) { }


        override fun onNokeConnected(noke: NokeDevice?) {
            noke?.lockState.apply {
                when (this) {
                    NokeDefines.NOKE_LOCK_STATE_LOCKED -> {
                    }
                    NokeDefines.NOKE_LOCK_STATE_UNLOCKED -> {
                    }
                    NokeDefines.NOKE_LOCK_STATE_UNKNOWN -> {
                    }
                }
            }
        }

        override fun onNokeSyncing(noke: NokeDevice?) {

        }

        override fun onNokeUnlocked(noke: NokeDevice?) {

        }

        override fun onNokeShutdown(noke: NokeDevice?, isLocked: Boolean?, didTimeout: Boolean?) {

        }

        override fun onNokeDisconnected(noke: NokeDevice?) {

        }

        override fun onDataUploaded(result: Int, message: String?) {

        }

        override fun onBluetoothStatusChanged(bluetoothStatus: Int) {

        }

        override fun onError(noke: NokeDevice?, error: Int, message: String?) {
            when (error) {
                NokeMobileError.ERROR_LOCATION_PERMISSIONS_NEEDED -> {
                }
                NokeMobileError.ERROR_LOCATION_SERVICES_DISABLED -> {
                    isError.postValue(Event("위치 권한 설정 필요!"))
                    isState.postValue(Event("테스트트"))
                }
                NokeMobileError.ERROR_BLUETOOTH_DISABLED -> {
                    isError.postValue(Event("블루투스 권한 설정 필요!"))
                }
                NokeMobileError.ERROR_BLUETOOTH_GATT -> {
                }
                NokeMobileError.DEVICE_ERROR_INVALID_KEY -> {
                }
            }
        }
    }


        suspend fun connectDevice(device: NokeDevice) = withContext(ioDispatcher) {
            nokeService.addNokeDevice(device)
        }
}