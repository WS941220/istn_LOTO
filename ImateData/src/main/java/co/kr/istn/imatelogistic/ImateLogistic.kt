package co.kr.istn.imatelogistic

import co.kr.istn.utility.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.sql.Date
import java.util.concurrent.TimeUnit


/**
 * 로지스틱 서비스
 */
class ImateLogistic(var baseUrl: String, var userId: String = "", var userPassword: String = "", sslIgnore : Boolean = false,
                    var connectTimeout : Long = 30, var readTimeOut : Long = 1200, var writeTimeout : Long = 300 ) {
    //Logistic 서비스
    private val logisticService: ImateLogisticService
    private val logisticServiceHttpClient: OkHttpClient.Builder
    //private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    /**
     * 마지막 오류 메시지
     */
    var lastErrorMessage: String

    init {
        lastErrorMessage = ""

        //https일 경우 테스트를 위해 안전하지 않은 SSL을 사용 한다.
        logisticServiceHttpClient = if (baseUrl.startsWith("https", true) && sslIgnore)
            UnsafeOkHttpClient.getUnsafeOkHttpClient() else OkHttpClient.Builder()

        //사용자 정보와 암호가 있으면 기본인증을 드록 한다.
        if (userId.trim().length > 0 && userPassword.trim().length > 0) {
            val authToken = getImateAuthData(userId, userPassword)
            val interceptor = BasicAuthenticationInterceptor(authToken)
            if (!logisticServiceHttpClient.interceptors().contains(interceptor))
                logisticServiceHttpClient.addInterceptor(interceptor)
        }

        //세션유지를 위해 쿠키를 보관 후 다시 서버로 보냄
        logisticServiceHttpClient.interceptors().add(AddCookiesInterceptor(logisticCookieSet));
        logisticServiceHttpClient.interceptors().add(ReceivedCookiesInterceptor(logisticCookieSet));
        logisticServiceHttpClient
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val gsonFactory = GsonConverterFactory.create(gson)

        //쿼리 서비스를 생성 한다.
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(logisticServiceHttpClient.build())
                .addConverterFactory(gsonFactory)
                .build()

        logisticService = retrofit.create(ImateLogisticService::class.java)
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
    fun setUserInfoAsync(callModule: String, callUnit: String, userInfo: String, userInfoQuery: String, postDate: Date): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        return scope.async {
            val result = try {
                val response = logisticService.setUserInfo(callModule, callUnit, userInfo, formatter.format(postDate), userInfoQuery).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun beginDocumentAsync(docType: String, parentDocId: String, refDocId: String, createDocMst: Boolean): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.beginDocument(docType,
                        if (parentDocId.isEmpty()) " " else parentDocId, if (refDocId.isEmpty()) " " else refDocId, createDocMst).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    /**
     * 문서처리 완료
     */
    fun completeDocumentAsync(): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.completeDocument().execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun createLotAsync(itemNo: String, vendItemCode: String, vendCode: String, mafDate: Date, qty: BigDecimal): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.createLot(itemNo, vendItemCode, vendCode, mafDate, qty).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun createReceiptVirtualLotAsync(movType: String, dest: String, lsDest: String, relId: String, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.createReceiptVirtualLot(movType, dest, lsDest, if (relId.isEmpty()) " " else relId, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun createReceiptVirtualLotWithOptionAsync(movType: String, dest: String, lsDest: String, relId: String, destItemNo: String, destItemType: String,
                                          state: String, stateType: String, mstStateChange: Boolean, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.createReceiptVirtualLotWithOption(movType, dest, lsDest, if (relId.isEmpty()) " " else relId, destItemNo, destItemType,
                        state, stateType, mstStateChange, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun receiptLotAsync(movType: String, dest: String, lsDest: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.receiptLot(movType, dest, lsDest, if (relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun multiReceiptLotAsync(dest: String, lsDest: String, relId: String, moveInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.multiReceiptLot(dest, lsDest, if (relId.isEmpty()) " " else relId, moveInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun receiptVirtualLotAsync(movType: String, dest: String, lsDest: String, relId: String, table: String, where: String, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        return scope.async {

            val result = try {
                val response = logisticService.receiptVirtualLot(movType, dest, lsDest, if (relId.isEmpty()) " " else relId, table, where, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf<LotResult>(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf<LotResult>(), "FAIL", e.message ?: "", e.localizedMessage
                        ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun receiptItemAsync(movType: String, itemNo: String, qty: BigDecimal, dest: String, lsDest: String, relId: String, paramInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {

            val result = try {
                val response = logisticService.receiptItem(movType, itemNo, qty, dest, lsDest, if (relId.isEmpty()) " " else relId, paramInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }
    //----------------------------------------------------------------------------------------------

    /**
     * LOT을 출고한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    fun issueLotAsync(movType: String, source: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.issueLot(movType, source, if (relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun multiIssueLotAsync(source: String, relId: String, moveInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {

            val result = try {
                val response = logisticService.multiIssueLot(source, if (relId.isEmpty()) " " else relId, moveInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun issueVirtualLotAsync(movType: String, source: String, lsDSource: String, lotMatchOrder: String, relId: String,
                        table: String, where: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {

            val result = try {
                val response = logisticService.issueVirtualLot(movType, source, lsDSource, lotMatchOrder, if (relId.isEmpty()) " " else relId, table, where, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun issueItemAsync(movType: String, itemNo: String, qty: BigDecimal, source: String, lsDSource: String, relId: String, paramInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.issueItem(movType, itemNo, qty, source, lsDSource, if(relId.isEmpty()) " " else relId, paramInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    //-----------------------------------------------------------------------------------------------

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
    fun moveLotAsync(movType: String, source: String, dest: String, lsDest: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.moveLot(movType, source, dest, lsDest, if (relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun multiMoveLotAsync(source: String, dest: String, lsDest: String, relId: String, moveInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {

            val result = try {
                val response = logisticService.multiMoveLot(source, dest, lsDest, if(relId.isEmpty()) " " else relId, moveInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun moveVirtualLotAsync(movType: String, source: String, lsSource: String, dest: String, lsDest: String, lotMatchOrder: String, relId: String,
                       table: String, where: String, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.moveVirtualLot(movType, source, lsSource, dest, lsDest, lotMatchOrder, if(relId.isEmpty()) " " else relId, table, where, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun moveItemAsync(movType: String, itemNo: String, qty: BigDecimal, source: String, lsSource: String, dest: String, lsDest: String,
                 relId: String, paramInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.moveItem(movType, itemNo, qty, source, lsSource, dest, lsDest, if(relId.isEmpty()) " " else relId, paramInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    //------------------------------------------------------------------------------------------------

    /**
     * LOT의 품목을 변경한다.
     *
     * @param movType 이동유형
     * @param source 원천 저장소
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    fun itemChangeLotAsync(movType: String, source: String, destItemNo: String, destItemType: String, relId: String,
                      lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.itemChangeLot(movType, source, destItemNo, destItemType, if(relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    /**
     * 여러이동 유형의 LOT의 품목을 변경한다.
     *
     * @param source 원천 저장소
     * @param destItemNo 목적지 자재번호
     * @param destItemType 목적지 자재유형
     * @param relId 참조ID
     * @param moveInfo 이동 정보
     */
    fun multiItemChangeLotAsync(source: String, destItemNo: String, destItemType: String, relId: String, moveInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.multiItemChangeLot(source, destItemNo, destItemType, if(relId.isEmpty()) " " else relId, moveInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun itemChangeVirtualLotAsync(movType: String, source: String, lsSource: String, lotMatchOrder: String, destItemNo: String, destItemType: String,
                             relId: String, table: String, where: String, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.itemChangeVirtualLot(movType, source, lsSource, lotMatchOrder, destItemNo, destItemType,
                        if (relId.isEmpty()) " " else relId, table, where, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun itemChangeAsync(movType: String, srcItemNo: String, qty: BigDecimal, source: String, lsSource: String, destItemNo: String, destItemType: String, relId: String,
                   pramInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.itemChange(movType, srcItemNo, qty, source, lsSource, destItemNo, destItemType,
                        if (relId.isEmpty()) " " else relId, pramInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun stateChangeLotAsync(movType: String, source: String, stateType: String, state: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.stateChangeLot(movType, source, stateType, state, if(relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun stateChangeVirtualLotAsync(movType: String, source: String, lsSource: String, lotMatchOrder: String,
                              stateType: String, state: String, relId: String, table: String, where: String, itemInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.stateChangeVirtualLot(movType, source, lsSource, lotMatchOrder, stateType, state,
                        if(relId.isEmpty()) " " else relId, table, where, itemInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun stateChangeAsync(movType: String, srcItemNo: String, qty: BigDecimal, source: String, lsSource: String, stateType: String,
                    state: String, relId: String, paramInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.stateChange(movType, srcItemNo, qty, source, lsSource, stateType, state,
                        if(relId.isEmpty()) " " else relId, paramInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 최종 물류 처리를 취소 한다.
     *
     * @param source 원천 저장소
     * @param lsSource 원천지 재고상태
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    fun cancelLogisticProcessAsync(source: String, lsSource: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.cancelLogisticProcess(source, lsSource, if (relId.isEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    /**
     * LOT을 Check한다.
     *
     * @param zipNo ZIP NO
     * @param locId 저장소ID
     * @param excludeLot 불포함 LOT번호
     */
    fun checkLotAsync(zipNo: String, locId: String, excludeLot: String): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.checkLot(zipNo, locId, excludeLot).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    /**
     * SAP 계약번호와 비교하여 LOT번호를 매치 한다
     *
     * @param conno 계약번호
     * @param lotInfo LOT 정보
     */
    fun checkConLotsAsync(conno: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.checkConLots(conno, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun swapLotAsync(swapType: String, scanLot: String, reqLot: String, isCheckLoc: Boolean): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.swapLot(swapType, scanLot, reqLot, isCheckLoc).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    /**
     * Custom Function을 실행 한다.
     *
     * @param movType 이동유형
     * @param relId 참조ID
     * @param lotInfo 물류 정보
     */
    fun customFuncExecuteAsync(movType: String, relId: String, lotInfo: LogisticParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.customFuncExecute(movType, if(relId.isNullOrEmpty()) " " else relId, lotInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 파일 서버의 디렉토리와 파일 정보를 반환 한다.
     *
     * @param serverAddress 서버주소
     * @param path 공유 폴더 경로
     * @param userID 공유 폴더 사용자ID
     * @param password 공유 폴더 암호
     */
    fun getDirectoryInfoAsync(serverAddress: String, path: String, userID: String, password: String): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.getDirectoryInfo(serverAddress,
                        if (path.isEmpty()) " " else path,
                        if (userID.isEmpty()) " " else userID, if (password.isEmpty()) " " else password).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun getFileDataAsync(serverAddress: String, path: String, filePath: String, userID: String, password: String): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.getFileData(serverAddress,
                        if(path.isEmpty()) " " else path, if(filePath.isEmpty()) " "  else filePath,
                        if(userID.isEmpty()) " " else userID, if(password.isEmpty()) " " else password).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
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
    fun setFileDataAsync(serverAddress: String, userID: String, password: String, fileInfo: FileParameter): Deferred<LogisticResult?> {
        val scope = CoroutineScope(Job() + Dispatchers.IO)

        return scope.async {
            val result = try {
                val response = logisticService.setFileData(serverAddress,
                        if(userID.isEmpty()) " " else userID, if(password.isEmpty()) "" else password, fileInfo).execute()

                if (response.isSuccessful) response.body()
                else LogisticResult(listOf(), "FAIL", response.code().toString(), response.errorBody()?.string()
                        ?: "")
            } catch (e: Exception) {
                LogisticResult(listOf(), "FAIL", e.message
                        ?: "", e.localizedMessage ?: "")
            }

            lastErrorMessage = result?.apiMessage ?: ""
            result
        }
    }
}