package co.kr.istn.smartLock

/**
 * Unlock Response Message
 * @param result tring value representing the result of the call. Either success or failure
 * @param message Readable description of the error
 * @param error_code  Int value of the error thrown
 * @param data A string of commands sent to the lock by the Nokē Mobile library
 * @param token Tokne
 * @param request Name of the request
 * @param api_version Api Version
 */
data class NokeUnlockResponse(var result : String, var message : String,val error_code: Int, var data : NokeCommandsData?,var token: String?, var  request : String, var api_version : NokeApiVersion?)

