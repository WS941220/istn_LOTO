package co.kr.istn.smartLock

/**
 * API Version
 * @param Response The version of this response
 * @param Available A list of known API versions keyed by version number (versions are semantic in nature)
 * @param Default This is the version returned if a specific API version is not requested
 */
data class NokeApiVersion(var Response : String, var Available: String, var Default: String)
