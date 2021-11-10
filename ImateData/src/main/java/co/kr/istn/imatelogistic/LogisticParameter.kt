package co.kr.istn.imatelogistic

import java.math.BigDecimal

/**
 *  Logistic 파라미터
 *
 * @param lotNos LotNo
 * @param moveType 이동 유형
 * @param moveReason 이동 사유
 * @param itemNos ItemNo
 * @param qtys 수량
 * @param userParams 사용자 파라미터
 * @param refDatas 참조 자료
 */
data class LogisticParameter(var lotNos : ArrayList<String>,
                             var userParams : ArrayList<String> = arrayListOf(),
                             var itemNos : ArrayList<String> = arrayListOf(),
                             var qtys : ArrayList<BigDecimal> = arrayListOf(),
                             var moveType : ArrayList<String> = arrayListOf(),
                             var moveReason : ArrayList<String> = arrayListOf(),
                             var refDatas : ArrayList<String> = arrayListOf())