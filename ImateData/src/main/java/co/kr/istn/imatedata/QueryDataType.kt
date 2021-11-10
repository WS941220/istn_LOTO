package co.kr.istn.imatedata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 질의 데이터 유형
 */
@Serializable
enum class QueryDataType {
    /**
     * 문자열
     */
    @SerialName(value = "String")
    String,

    /**
     * 숫자
     */
    @SerialName(value ="Number")
    Number,

    /**
     * 일자 또는 일자시간
     */
    @SerialName(value ="Date")
    Date,

    /**
     * 시간
     */
    @SerialName(value ="Time")
    Time
}