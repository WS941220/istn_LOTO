package co.kr.istn.imatelogistic

import co.kr.istn.imatedata.QueryMessage
import co.kr.istn.imatedata.QueryRunResult
import co.kr.istn.imatedata.dataset.DataSet
import co.kr.istn.utility.getRandomString
import kotlinx.coroutines.Deferred
import java.math.BigDecimal
import java.sql.Date

/**
 * 로지스틱 서비스 Adapter
 */
class ImateLogisticAdapter(val imateLogistic: ImateLogistic) {
    var lastApiResult: String
    var lastApiMessage: String
    var lastUserMessage: String

    /**
     * 생성자
     *
     * @param baseUrl 서버 Base Url
     * @param userId 사용자ID
     * @param userPassword 사용자 암호
     */
    constructor(baseUrl: String, userId: String = "", userPassword: String = "", sslIgnore : Boolean = false,
                connectTimeout : Long = 30, readTimeOut : Long = 1200, writeTimeout : Long = 300)
            : this(ImateLogistic(baseUrl, userId, userPassword, sslIgnore, connectTimeout, readTimeOut, writeTimeout))

    init {
        lastApiResult = ""
        lastApiMessage = ""
        lastUserMessage = ""
    }

    /**
     * 사용자 정보
     *
     * @param callModule 호출모듈
     * @param callUnit 호출유닛
     * @param userInfo 사용자 정보
     * @param userInfoQuery 사용자 정보 질의
     * @param postDate 전기일자
     */
    suspend fun setUserInfo(callModule: String, callUnit: String, userInfo: String, userInfoQuery: String, postDate: Date): LogisticResult? {
        try {
            val queryResult = imateLogistic.setUserInfoAsync(callModule, callUnit, userInfo, userInfoQuery, postDate).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 문서처리 시작
     *
     * @param docType 문서유형
     * @param parentDocId 문서ID
     * @param refDocId 참조 문서 번호
     * @param createDocMst 문서마스터 생성 여부
     */
    suspend fun beginDocument(docType: String, parentDocId: String, refDocId: String, createDocMst: Boolean): LogisticResult? {
        try {
            val queryResult = imateLogistic.beginDocumentAsync(docType, parentDocId, refDocId, createDocMst).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 문서처리 완료
     */
    suspend fun completeDocument(): LogisticResult? {
        try {
            val queryResult = imateLogistic.completeDocumentAsync().await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT을 생성 한다.
     *
     * @param itemNo 자재 번호
     * @param vendItemCode 밴드 자재 번호
     * @param vendCode 밴더코드
     * @param mafDate 제조일자
     * @param qty 수량
     */
    suspend fun createLot(itemNo: String, vendItemCode: String, vendCode: String, mafDate: Date, qty: BigDecimal): LogisticResult? {
        try {
            val queryResult = imateLogistic.createLotAsync(itemNo, vendItemCode, vendCode, mafDate, qty).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT을 입고받는다.(가상LOT)
     *
     * @param movType 이동유형
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param itemInfo 자재 정보
     */
    suspend fun createReceiptVirtualLot(movType: String, dest: String, lsDest: String, relId: String, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.createReceiptVirtualLotAsync(movType, dest, lsDest, relId, itemInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT을 입고받는다.(가상LOT)
     *
     * @param movType 이동유형
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param destItemNo 목적지 자재 번호
     * @param destItemType 목적지 자재 유형
     * @param state 자재상태
     * @param stateType 상태유형
     * @param mstStateChange LTMT 자재상태 변경
     * @param itemInfo 물류이동 정보
     */
    suspend fun createReceiptVirtualLotWithOption(movType: String, dest: String, lsDest: String, relId: String, destItemNo: String, destItemType: String,
                                                  state: String, stateType: String, mstStateChange: Boolean, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.createReceiptVirtualLotWithOptionAsync(movType, dest, lsDest, relId, destItemNo, destItemType,
                    state, stateType, mstStateChange, itemInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * LOT을 입고받는다.
     *
     * @param movType 이동유형
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param lotInfo LOT 정보
     */
    suspend fun receiptLot(movType: String, dest: String, lsDest: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.receiptLotAsync(movType, dest, lsDest, relId, lotInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 여러 이동 유형의 LOT을 입고받는다.
     *
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param moveInfo 이동 정보
     */
    suspend fun multiReceiptLot(dest: String, lsDest: String, relId: String, moveInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.multiReceiptLotAsync(dest, lsDest, relId, moveInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목을 입고받은 다음 가상LOT을 생성 한다.
     *
     * @param movType 이동유형
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param table 테이블명
     * @param where 검색 조건
     * @param itemInfo 물류이동 정보
     */
    suspend fun receiptVirtualLot(movType: String, dest: String, lsDest: String, relId: String, table: String, where: String, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.receiptVirtualLotAsync(movType, dest, lsDest, relId, table, where, itemInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목을 입고 받는다.
     *
     * @param movType 이동유형
     * @param itemNo 자재번호
     * @param qty 수량
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param paramInfo 파라미터 정보
     */
    suspend fun receiptItem(movType: String, itemNo: String, qty: BigDecimal, dest: String, lsDest: String, relId: String, paramInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.receiptItemAsync(movType, itemNo, qty, dest, lsDest, relId, paramInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //-----------------------------------------------------------------------------------------------

    /**
     * LOT을 출고한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun issueLot(movType: String, source: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.issueLotAsync(movType, source, relId, lotInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 여러이동 유형의 LOT을 출고한다
     *
     * @param source 원천 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param moveInfo 이동 정보
     */
    suspend fun multiIssueLot(source: String, relId: String, moveInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.multiIssueLotAsync(source, relId, moveInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 가상 LOT을 출고 한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param lsDSource 원천지 재고상태
     * @param lotMatchOrder Lot 매치 순서
     * @param relId 참조ID
     * @param table 테이블명
     * @param where 검색 조건
     * @param lotInfo 물류 정보
     */
    suspend fun issueVirtualLot(movType: String, source: String, lsDSource: String, lotMatchOrder: String, relId: String,
                                table: String, where: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.issueVirtualLotAsync(movType, source, lsDSource, lotMatchOrder, relId, table, where, lotInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목을 출고 한다.
     *
     * @param movType 이동유형
     * @param itemNo 자재번호
     * @param qty 수량
     * @param source 원천 저장소
     * @param lsDSource 원천지 재고상태
     * @param relId 참조ID
     * @param paramInfo 파라미터 정보
     */
    suspend fun IssueItem(movType: String, itemNo: String, qty: BigDecimal, source: String, lsDSource: String, relId: String, paramInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.issueItemAsync(movType, itemNo, qty, source, lsDSource, relId, paramInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //--------------------------------------------------------------------------------------------------

    /**
     * LOT을 이동 처리 한다.(LOT이 현재 위치를 기억함)
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun MoveLot(movType: String, source: String, dest: String, lsDest: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.moveLotAsync(movType, source, dest, lsDest, relId, lotInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT을 이동 처리 한다.(LOT이 현재 위치를 기억함)
     *
     * @param source 원천 저장소
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param moveInfo 이동 정보
     */
    suspend fun multiMoveLot(source: String, dest: String, lsDest: String, relId: String, moveInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.multiMoveLotAsync(source, dest, lsDest, relId, moveInfo).await()
            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목의 가상LOT을 이동 처리 한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param lotMatchOrder Lot 매치 순서
     * @param relId 참조ID
     * @param table 테이블명
     * @param where 검색 조건
     * @param lotInfo 물류 정보
     */
    suspend fun MoveVirtualLot(movType: String, source: String, lsSource: String, dest: String, lsDest: String, lotMatchOrder: String, relId: String,
                               table: String, where: String, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.moveVirtualLotAsync(movType, source, lsSource, dest, lsDest, lotMatchOrder, relId, table, where, itemInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목을 이동처리 한다.
     *
     * @param movType 이동유형
     * @param itemNo 자재번호
     * @param qty 수량
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param dest 목적지 저장소
     * @param lsDest 목적지 재고상태
     * @param relId 참조ID
     * @param paramInfo 파라미터 정보
     */
    suspend fun MoveItem(movType: String, itemNo: String, qty: BigDecimal, source: String, lsSource: String, dest: String, lsDest: String,
                         relId: String, paramInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.moveItemAsync(movType, itemNo, qty, source, lsSource, dest, lsDest, relId, paramInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //------------------------------------------------------------------------------------------------

    /**
     *  LOT의 품목을 변경한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun ItemChangeLot(movType: String, source: String, destItemNo: String, destItemType: String, relId: String,
                              lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.itemChangeLotAsync(movType, source, destItemNo, destItemType, relId, lotInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     *  여러이동 유형의 LOT의 품목을 변경한다
     *
     * @param source 원천 저장소
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param moveInfo 이동 정보
     */
    suspend fun MultiItemChangeLot(source: String, destItemNo: String, destItemType: String, relId: String, moveInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.multiItemChangeLotAsync(source, destItemNo, destItemType, relId, moveInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 가상LOT의 품목을 변경한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param lotMatchOrder Lot 매치 순서
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param table 테이블명
     * @param where 검색 조건
     * @param lotInfo 물류 정보
     */
    suspend fun ItemChangeVirtualLot(movType: String, source: String, lsSource: String, lotMatchOrder: String, destItemNo: String, destItemType: String,
                                     relId: String, table: String, where: String, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.itemChangeVirtualLotAsync(movType, source, lsSource, lotMatchOrder, destItemNo, destItemType,
                    relId, table, where, itemInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목을 변경 한다.
     *
     * @param movType 이동유형
     * @param srcItemNo 원천 자재번호
     * @param qty 수량
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param paramInfo 파라미터 정보
     */
    suspend fun ItemChange(movType: String, srcItemNo: String, qty: BigDecimal, source: String, lsSource: String, destItemNo: String, destItemType: String, relId: String,
                           pramInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.itemChangeAsync(movType, srcItemNo, qty, source, lsSource, destItemNo, destItemType, relId, pramInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT의 상태를 변경한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param state 자재상태
     * @param stateType 상태유형
     * @param lsSource 원천지 재고상태
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun StateChangeLot(movType: String, source: String, stateType: String, state: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.stateChangeLotAsync(movType, source, stateType, state, relId, lotInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 가상 LOT의 상태를 변경한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param lotMatchOrder Lot 매치 순서
     * @param state 자재상태
     * @param stateType 상태유형
     * @param relId 참조ID
     * @param table 테이블명
     * @param where 검색 조건
     * @param itemInfo 자재 변경 정보
     */
    suspend fun StateChangeVirtualLot(movType: String, source: String, lsSource: String, lotMatchOrder: String,
                                      stateType: String, state: String, relId: String, table: String, where: String, itemInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.stateChangeVirtualLotAsync(movType, source, lsSource, lotMatchOrder, stateType, state, relId, table, where, itemInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 품목의 상태 변경
     *
     * @param movType 이동유형
     * @param srcItemNo 원천 자재번호
     * @param qty 수량
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param state 자재상태
     * @param stateType 상태유형
     * @param relId 참조ID
     * @param paramInfo 파라미터 정보
     */
    suspend fun StateChange(movType: String, srcItemNo: String, qty: BigDecimal, source: String, lsSource: String, stateType: String,
                            state: String, relId: String, paramInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.stateChangeAsync(movType, srcItemNo, qty, source, lsSource, stateType, state, relId, paramInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //------------------------------------------------------------------------------------------------

    /**
     * 최종 물류 처리를 취소 한다.
     *
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun CancelLogisticProcess(source: String, lsSource: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.cancelLogisticProcessAsync(source, lsSource, relId, lotInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * LOT을 Check한다.
     *
     * @param zipNo ZIP NO
     * @param locId 저장소ID
     * @param excludeLot 불포함 LOT번호
     */
    suspend fun CheckLot(zipNo: String, locId: String, excludeLot: String): LogisticResult? {
        try {
            val queryResult = imateLogistic.checkLotAsync(zipNo, locId, excludeLot).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * SAP 계약번호와 비교하여 LOT번호를 매치 한다
     *
     * @param conno 계약번호
     * @param lotInfo LOT 정보
     */
    suspend fun CheckConLots(conno: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.checkConLotsAsync(conno, lotInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 두개LOT의 정보를 SWAP한다.
     *
     * @param swapType SWAP 유형
     * @param scanLot SCAN한 LOT
     * @param reqLot 요청 LOT
     * @param isCheckLoc 저장소 점검여부
     */
    suspend fun SwapLot(swapType: String, scanLot: String, reqLot: String, isCheckLoc: Boolean): LogisticResult? {
        try {
            val queryResult = imateLogistic.swapLotAsync(swapType, scanLot, reqLot, isCheckLoc).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Custom Function을 실행 한다.
     *
     * @param movType 이동유형
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    suspend fun CustomFuncExecute(movType: String, relId: String, lotInfo: LogisticParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.customFuncExecuteAsync(movType, relId, lotInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //------------------------------------------------------------------------------------------------

    /**
     * 파일 서버의 디렉토리와 파일 정보를 반환 한다.
     *
     * @param serverAddress 서버주소
     * @param path 공유 폴더 경로
     * @param userID 공유 폴더 사용자ID
     * @param password 공유 폴더 암호
     */
    suspend fun GetDirectoryInfo(serverAddress: String, path: String, userID: String, password: String): LogisticResult? {
        try {
            val queryResult = imateLogistic.getDirectoryInfoAsync(serverAddress, path, userID, password).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * 파일의 자료를 Base64 Encoding하여 반환 한다.
     *
     * @param serverAddress 서버주소
     * @param path 서버 경로
     * @param filePath 파일 결로
     * @param userID 공유 폴더 사용자ID
     * @param password 공유 폴더 암호
     */
    suspend fun GetFileData(serverAddress: String, path: String, filePath: String, userID: String, password: String): LogisticResult? {
        try {
            val queryResult = imateLogistic.getFileDataAsync(serverAddress, path, filePath, userID, password).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Base64 Encoding된 자료를 파일로 저장 한다.
     *
     * @param serverAddress 서버주소
     * @param userID 공유 폴더 사용자ID
     * @param password 공유 폴더 암호
     * @param fileInfo 파일정보
     */
    suspend fun SetFileData(serverAddress: String, userID: String, password: String, fileInfo: FileParameter): LogisticResult? {
        try {
            val queryResult = imateLogistic.setFileDataAsync(serverAddress, userID, password, fileInfo).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            return queryResult;
        } catch (e: Exception) {
            throw e
        }
    }

    //------------------------------------------------------------------------------------------------
}