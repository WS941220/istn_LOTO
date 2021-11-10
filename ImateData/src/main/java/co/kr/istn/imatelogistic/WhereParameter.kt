package co.kr.istn.imatelogistic

/**
 * 검색 조건
 *
 * @param table 테이블명 NULL 또는 EMPTY이면 LTMT
 * @param where 추가 조건
 */
data class WhereParameter (var table : String, var where : String)