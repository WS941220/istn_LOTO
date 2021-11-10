package co.kr.istn.smartLock

/**
 * Lock 정보를 요청 한다.
 *
 * @param serial Serial 번호
 * @param userId  User Id
 * @param mac Mac Address
 * @param tracking_key Tracking Key
 */
data class LockInfo(var serial : String, var userId : String, var mac : String, var tracking_key: String)
