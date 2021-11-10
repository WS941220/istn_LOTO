package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 * Query 메시지
 *
 * @param queryMethod 쿼리 실행 방법
 * @param queryName 쿼리 이름
 * @param dataSource 데이터 소스
 * @param queryTemplate 쿼리 템플릿
 * @param dependQuery 의존 쿼리 집합
 * @param parameters 파라미터
 */
@Serializable
data class QueryMessage(
    var queryMethod : QueryRunMethod,
    var queryName: String,
    var dataSource: String,
    var queryTemplate: String,
    var dependQuery : List<String>,
    var parameters : List<QueryParameter>)
