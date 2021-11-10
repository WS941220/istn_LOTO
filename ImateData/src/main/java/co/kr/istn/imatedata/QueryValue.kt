package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 * 쿼리 값
 *
 * @param queryName 쿼리 이름
 * @param columnInfos 쿼리 정보
 * @param rows Row의 집합
 */
@Serializable
data class QueryValue(
    var queryName : String,
    var columnInfos : List<XNColumnInfo>,
    var rows : List<XNRowValue>)