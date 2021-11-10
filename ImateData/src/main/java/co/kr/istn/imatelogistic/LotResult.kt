package co.kr.istn.imatelogistic

/**
 * lot 처리 결과
 *
 * @param lotNo lot No
 * @param refData1 참조자료 1
 * @param refData2 참조자료 2
 * @param refData3 참조자료 3
 * @param refData4 참조자료 4
 * @param refData5 참조자료 5
 * @param result 결과
 * @param message 메시지
 */
data class LotResult (var lotNo : String, var refData1 : String, var refData2 : String, var refData3 : String, var refData4 : String, var refData5 : String,
                      var result : String, var message : String)