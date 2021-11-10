package co.kr.istn.imatedata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 쿼리 실행 방법
 */
@Serializable
enum class QueryRunMethod {
    /**
     * 다른 쿼리와 연관없이 독립되어 실행
     */
    @SerialName(value = "Alone")
    Alone,

    /**
     * 연관 쿼리의 Row의 개수 만큼 실행함
     */
    @SerialName(value = "Depend")
    Depend,

    /**
     * 연관 쿼리의 자료로 파라미터를 만든 다음 1번 실행함
     */
    @SerialName(value = "Bound")
    Bound
}