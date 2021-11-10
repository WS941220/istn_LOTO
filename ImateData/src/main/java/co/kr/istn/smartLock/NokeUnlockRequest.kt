package co.kr.istn.smartLock

/**
 * Noke Unlock Request
 * @param mac The mac address of the lock
 * @param session Unique session generated by the lock and read by the phone when connecting. (see Nokē Mobile library documentation)
 * @param tracking_key An optional string used to associate to lock activity
 */
data class NokeUnlockRequest(var mac : String, var session: String, var tracking_key : String)
