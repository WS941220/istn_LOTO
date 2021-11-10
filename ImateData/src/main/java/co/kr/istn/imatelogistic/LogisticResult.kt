package co.kr.istn.imatelogistic

/**
 * Logistic 초리 결과
 *
 * @param lotResult Lot별 처리 결과
 * @param apiResult api 처리 결과
 * @param apiMessage api 메시지
 * @param userMessage 사용자 메시지
 */
data class LogisticResult (var lotResult : List<LotResult>, var apiResult : String, var apiMessage : String, var userMessage : String )
