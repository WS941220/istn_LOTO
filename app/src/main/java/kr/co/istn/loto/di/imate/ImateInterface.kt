package kr.co.istn.loto.di.imate

import co.kr.istn.smartLock.LockInfo
import kr.co.istn.loto.di.Result

interface ImateInterface {

    suspend fun getLockInfo(info: LockInfo): Result<LockInfo>
}