package co.kr.istn.smartLock

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *  Smart Lock Interface
 */
interface SmartLockService {
    /**
     * Lock 정보를 거져 온다.
     *
     * @param lockInfo lock 정보
     * @return lock 정보
     */
    @POST( value = "api/SmartLockService/lockinfo")
    fun getLockInfo(@Body() lockInfo : LockInfo ) : Call<LockInfo>

    /**
     * Unlock Command를 가져온다.
     *
     * @param unlockRequest Unlock 요청
     * @return Unlock 요청에 대한 응답
     */
    @POST( value = "api/SmartLockService/unlock")
    fun Unlock(@Body() unlockRequest : NokeUnlockRequest ) : Call<NokeUnlockResponse>
}