package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 * Query 결과
 *
 * @param transactionId 트랜잭션 ID
 * @param results 결과
 * @param apiResult API 결과
 * @param apiMessage API 메시지
 * @param userMessage 사용자 메시지
 */
@Serializable
data class QueryRunResult(
    var transactionId: String,
    var results: List<QueryValue>,
    var apiResult : String,
    var apiMessage : String,
    var userMessage: String
)