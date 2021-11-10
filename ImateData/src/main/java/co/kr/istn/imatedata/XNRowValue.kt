package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 * Row 값 클래스
 *
 * @param rowValue row 값 집합
 */
@Serializable
data class XNRowValue (var rowValue : List<String>)