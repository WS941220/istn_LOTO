package kr.co.istn.loto.di.imate

import co.kr.istn.imatedata.ImateDataAdapter
import co.kr.istn.smartLock.LockInfo
import co.kr.istn.smartLock.SmartLockAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.co.istn.loto.di.Result
import javax.inject.Inject

class ImateRepository  @Inject constructor(
    private var imateDataAdapter: ImateDataAdapter,
    private var smartLockAdapter: SmartLockAdapter,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ImateInterface {

    override suspend fun getLockInfo(info: LockInfo) : Result<LockInfo> = withContext(ioDispatcher) {
        try {
            val lockInfo = smartLockAdapter.getLockInfo(info).await()
            if(lockInfo.mac != "error")
                return@withContext Result.Success(lockInfo)
            else
                return@withContext Result.Error(Exception())
        }  catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

}