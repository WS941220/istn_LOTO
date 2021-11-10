package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 * 쿼리 파라미터
 *
 * @param name 파라미터 이름
 * @param dataType 파라미터 데이터 유형
 * @param value 파라미터 데이터 값
 * @param template 파라미터 템플릿
 * @param lineTerminateChar 라인 끝 문자
 * @param prefix 접두사
 * @param surfix 접미사
 */
@Serializable
data class QueryParameter(
    var name: String,
    var dataType: QueryDataType,
    var value: String,
    var template: String,
    var lineTerminateChar: String,
    var prefix : String = "",
    var surfix : String = "")