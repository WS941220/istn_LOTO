package co.kr.istn.imatelogistic

import retrofit2.Call
import retrofit2.http.*
import java.math.BigDecimal
import java.sql.Date

/**
 * Logistic 서비스 인터페이스
 */
interface ImateLogisticService {
    /**
     * 사용자 정보
     */
    @POST(value = "api/LogisticService/SetUserInfo/{callModule}/{callUnit}/{userInfo}/{postDate}")
    fun setUserInfo(@Path(value = "callModule") callModule: String, @Path(value = "callUnit") callUnit: String, @Path(value = "userInfo") userInfo: String,
                    @Path(value = "postDate") postDate: String, @Body userInfoQuery: String): Call<LogisticResult>

    /**
     * 문서처리 시작
     */
    @GET(value = "api/LogisticService/BeginDocument/{docType}/{parentDocId}/{refDocId}/{createDocMst}")
    fun beginDocument(@Path(value = "docType") docType: String, @Path(value = "parentDocId") parentDocId: String,
                      @Path(value = "refDocId") refDocId: String, @Path(value = "createDocMst") createDocMst: Boolean): Call<LogisticResult>

    /**
     * 문서처리 완료
     */
    @GET(value = "api/LogisticService/CompleteDocument")
    fun completeDocument(): Call<LogisticResult>

    /**
     * LOT을 생성 한다.
     */
    @GET(value = "api/LogisticService/CreateLot/{itemNo}/{vendItemCode}/{vendCode}/{mafDate}/{qty}")
    fun createLot(@Path(value = "itemNo") itemNo: String, @Path(value = "vendItemCode") vendItemCode: String,
                  @Path(value = "vendCode") vendCode: String, @Path(value = "mafDate") mafDate: Date,
                  @Path(value = "qty") qty: BigDecimal): Call<LogisticResult>

    /**
     * LOT을 입고받는다.(가상LOT)
     */
    @POST(value = "api/LogisticService/CreateReceiptVirtualLot/{movType}/{dest}/{lsDest}/{relId}/")
    fun createReceiptVirtualLot(@Path(value = "movType") movType: String, @Path(value = "dest") dest: String,
                                @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body itemInfo: LogisticParameter): Call<LogisticResult>


    /**
     * LOT을 입고받는다.(가상LOT)
     */
    @POST(value = "api/LogisticService/CreateReceiptVirtualLotWithOption/{movType}/{dest}/{lsDest}/{relId}/{destItemNo}/{destItemType}/{state}/{stateType}/{mstStateChange}")
    fun createReceiptVirtualLotWithOption(@Path(value = "movType") movType: String, @Path(value = "dest") dest: String, @Path(value = "lsDest") lsDest: String,
                                          @Path(value = "relId") relId: String, @Path(value = "destItemNo") destItemNo: String, @Path(value = "destItemType") destItemType: String,
                                          @Path(value = "state") state: String, @Path(value = "stateType") stateType: String, @Path(value = "mstStateChange") mstStateChange: Boolean,
                                          @Body itemInfo: LogisticParameter): Call<LogisticResult>

    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * LOT을 입고받는다.
     */
    @POST(value = "api/LogisticService/ReceiptLot/{movType}/{dest}/{lsDest}/{relId}/")
    fun receiptLot(@Path(value = "movType") movType: String, @Path(value = "dest") dest: String,
                   @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body lotInfo: LogisticParameter): Call<LogisticResult>


    /**
     * 여러 이동 유형의 LOT을 입고받는다.
     */
    @POST(value = "api/LogisticService/MultiReceiptLot/{dest}/{lsDest}/{relId}/")
    fun multiReceiptLot(@Path(value = "dest") dest: String, @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String,
                        @Body() moveInfo: LogisticParameter): Call<LogisticResult>


    /**
     * 품목을 입고받은 다음 가상LOT을 생성 한다.
     */
    @POST(value = "api/LogisticService/ReceiptVirtualLot/{movType}/{dest}/{lsDest}/{relId}/")
    fun receiptVirtualLot(@Path(value = "movType") movType: String, @Path(value = "dest") dest: String,
                          @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String,
                          @Query(value = "table") table: String, @Query(value = "where") where: String, @Body itemInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 품목을 입고 받는다.
     */
    @POST(value = "api/LogisticService/ReceiptItem/{movType}/{itemNo}/{qty}/{dest}/{lsDest}/{relId}/")
    fun receiptItem(@Path(value = "movType") movType: String, @Path(value = "itemNo") itemNo: String,
                    @Path(value = "qty") qty: BigDecimal, @Path(value = "dest") dest: String,
                    @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body paramInfo: LogisticParameter): Call<LogisticResult>

    //---------------------------------------------------------------------------------------------------

    /**
     * LOT을 출고한다.
     */
    @POST(value = "api/LogisticService/IssueLot/{movType}/{source}/{relId}/")
    fun issueLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String, @Path(value = "relId") relId: String,
                 @Body lotInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 여러이동 유형의 LOT을 출고한다
     */
    @POST(value = "api/LogisticService/MultiIssueLot/{source}/{relId}/")
    fun multiIssueLot(@Path(value = "source") source: String, @Path(value = "relId") relId: String, @Body moveInfo: LogisticParameter): Call<LogisticResult>

    /**
     *  가상 LOT을 출고 한다.
     */
    @POST(value = "api/LogisticService/IssueVirtualLot/{movType}/{source}/{lsDSource}/{lotMatchOrder}/{relId}/")
    fun issueVirtualLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String,
                        @Path(value = "lsDSource") lsDSource: String, @Path(value = "lotMatchOrder") lotMatchOrder: String, @Path(value = "relId") relId: String,
                        @Query(value = "table") table: String, @Query(value = "where") where: String, @Body lotInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 품목을 출고 한다.
     */
    @POST(value = "api/LogisticService/IssueItem/{movType}/{itemNo}/{qty}/{source}/{lsDSource}/{relId}/")
    fun issueItem(@Path(value = "movType") movType: String, @Path(value = "itemNo") itemNo: String,
                  @Path(value = "qty") qty: BigDecimal, @Path(value = "source") source: String, @Path(value = "lsDSource") lsDSource: String,
                  @Path(value = "relId") relId: String, @Body paramInfo: LogisticParameter): Call<LogisticResult>


    //-----------------------------------------------------------------------------------------------

    /**
     * LOT을 이동 처리 한다.(LOT이 현재 위치를 기억함)
     */
    @POST(value = "api/LogisticService/MoveLot/{movType}/{source}/{dest}/{lsDest}{relId}/")
    fun moveLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String,
                @Path(value = "dest") dest: String, @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body lotInfo: LogisticParameter): Call<LogisticResult>


    /**
     * LOT을 이동 처리 한다.(LOT이 현재 위치를 기억함)
     */
    @POST(value = "api/LogisticService/MultiMoveLot/{source}/{dest}/{lsDest}/{relId}/")
    fun multiMoveLot(@Path(value = "source") source: String, @Path(value = "dest") dest: String,
                     @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body moveInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 품목의 가상LOT을 이동 처리 한다.
     */
    @POST(value = "api/LogisticService/MoveVirtualLot/{movType}/{source}/{lsSource}/{dest}/{lsDest}/{lotMatchOrder}/{relId}/")
    fun moveVirtualLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String, @Path(value = "lsSource") lsSource: String,
                       @Path(value = "dest") dest: String, @Path(value = "lsDest") lsDest: String,
                       @Path(value = "lotMatchOrder") lotMatchOrder: String, @Path(value = "relId") relId: String,
                       @Query(value = "table") table: String, @Query(value = "where") where: String, @Body itemInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 품목을 이동처리 한다.
     */
    @POST(value = "api/LogisticService/MoveItem/{movType}/{itemNo}/{qty}/{source}/{lsSource}/{dest}/{lsDest}/{relId}/")
    fun moveItem(@Path(value = "movType") movType: String, @Path(value = "itemNo") itemNo: String,
                 @Path(value = "qty") qty: BigDecimal, @Path(value = "source") source: String, @Path(value = "lsSource") lsSource: String,
                 @Path(value = "dest") dest: String, @Path(value = "lsDest") lsDest: String, @Path(value = "relId") relId: String, @Body paramInfo: LogisticParameter): Call<LogisticResult>

    //------------------------------------------------------------------------------------------------------------

    /**
     * LOT의 품목을 변경한다.
     */
    @POST(value = "api/LogisticService/ItemChangeLot/{movType}/{source}/{destItemNo}/{destItemType}/{relId}/")
    fun itemChangeLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String,
                      @Path(value = "destItemNo") destItemNo: String, @Path(value = "destItemType") destItemType: String,
                      @Path(value = "relId") relId: String, @Body lotInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 여러이동 유형의 LOT의 품목을 변경한다.
     */
    @POST(value = "api/LogisticService/MultiItemChangeLot/{source}/{destItemNo}/{destItemType}/{relId}/")
    fun multiItemChangeLot(@Path(value = "source") source: String, @Path(value = "destItemNo") destItemNo: String,
                           @Path(value = "destItemType") destItemType: String, @Path(value = "relId") relId: String, @Body moveInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 가상LOT의 품목을 변경한다.
     */
    @POST(value = "api/LogisticService/ItemChangeVirtualLot/{movType}/{source}/{lsSource}/{destItemNo}/{destItemType}/{relId}/")
    fun itemChangeVirtualLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String,
                             @Path(value = "lsSource") lsSource: String, @Path(value = "lotMatchOrder") lotMatchOrder: String,
                             @Path(value = "destItemNo") destItemNo: String, @Path(value = "destItemType") destItemType: String, @Path(value = "relId") relId: String,
                             @Query(value = "table") table: String, @Query(value = "where") where: String, @Body itemInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 품목을 변경 한다.
     */
    @POST(value = "api/LogisticService/ItemChange/{movType}/{srcItemNo}/{qty}/{source}/{lsSource}/{destItemNo}/{destItemType}/{relId}/")
    fun itemChange(@Path(value = "movType") movType: String, @Path(value = "srcItemNo") srcItemNo: String,
                   @Path(value = "qty") qty: BigDecimal, @Path(value = "source") source: String, @Path(value = "lsSource") lsSource: String,
                   @Path(value = "destItemNo") destItemNo: String, @Path(value = "destItemType") destItemType: String, @Path(value = "relId") relId: String,
                   @Body pramInfo: LogisticParameter): Call<LogisticResult>

    /**
     * LOT의 상태를 변경한다.
     */
    @POST(value = "api/LogisticService/StateChangeLot/{movType}/{source}/{stateType}/{state}/{relId}/")
    fun stateChangeLot(@Path(value = "movType") movType: String, @Path(value = "source") source: String,
                       @Path(value = "stateType") stateType: String, @Path(value = "state") state: String, @Path(value = "relId") relId: String, @Body lotInfo: LogisticParameter): Call<LogisticResult>

    /**
     * 가상 LOT의 상태를 변경한다.
     */
    @POST(value = "api/LogisticService/StateChangeVirtualLot/{movType}/{source}/{lsSource}/{lotMatchOrder}/{stateType}/{state}/{relId}/")
    fun stateChangeVirtualLot(@Path(value = "movType") movType : String, @Path(value = "source") source : String,
                              @Path(value = "lsSource") lsSource : String, @Path(value = "lotMatchOrder") lotMatchOrder : String,
                              @Path(value = "stateType") stateType : String, @Path(value = "state") state : String, @Path(value = "relId") relId : String,
                              @Query(value = "table") table: String, @Query(value = "where") where: String, @Body itemInfo : LogisticParameter) : Call<LogisticResult>

    /**
     * 품목의 상태 변경
     */
    @POST(value = "api/LogisticService/StateChange/{movType}/{srcItemNo}/{qty}/{source}/{lsSource}/{stateType}/{state}/{relId}/")
    fun stateChange(@Path(value = "movType") movType : String, @Path(value = "srcItemNo") srcItemNo : String,
                    @Path(value = "qty") qty : BigDecimal, @Path(value = "source") source: String,
                    @Path(value = "lsSource") lsSource: String, @Path(value = "stateType") stateType: String,
                    @Path(value = "state") state : String, @Path(value = "relId") relId : String, @Body paramInfo: LogisticParameter) : Call<LogisticResult>

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 최종 물류 처리를 취소 한다.
     */
    @POST(value = "api/LogisticService/CancelLogisticProcess/{source}/{lsSource}/{relId}/")
    fun  cancelLogisticProcess(@Path(value = "source") source : String, @Path(value = "lsSource") lsSource : String, @Path(value = "relId") relId : String,
                               @Body lotInfo : LogisticParameter) : Call<LogisticResult>

    /**
     * LOT을 Check한다.
     */
    @GET(value = "api/LogisticService/CheckLot/{zipNo}/{locId}/{excludeLot}")
    fun  checkLot(@Path(value = "zipNo")  zipNo : String, @Path(value = "locId")  locId : String, @Path(value = "excludeLot")  excludeLot : String) : Call<LogisticResult>

    /**
     *  SAP 계약번호와 비교하여 LOT번호를 매치 한다
     */
    @POST(value = "api/LogisticService/CheckConLots/{conno}")
    fun checkConLots(@Path(value = "conno") conno : String, @Body lotInfo : LogisticParameter) : Call<LogisticResult>

    /**
     * 두개LOT의 정보를 SWAP한다.
     */
    @GET(value = "api/LogisticService/SwapLot/{swapType}/{scanLot}/{reqLot}/{isCheckLoc}")
    fun swapLot(@Path(value = "swapType") swapType : String, @Path(value = "scanLot") scanLot : String,
                @Path(value = "reqLot") reqLot : String, @Path(value = "isCheckLoc") isCheckLoc : Boolean): Call<LogisticResult>

    /**
     * Custom Function을 실행 한다.
     */
    @POST(value = "api/LogisticService/CustomFuncExecute/{movType}/{relId}/")
    fun customFuncExecute(@Path(value = "movType") movType : String, @Path(value = "relId") relId : String, @Body lotInfo : LogisticParameter) : Call<LogisticResult>

    //----------------------------------------------------------------------------------------------

    /**
     * 파일 서버의 디렉토리와 파일 정보를 반환 한다.
     */
    @GET(value = "api/LogisticService/GetDirectoryInfo/{serverAddress}/{path}/{userID}/{password}")
    fun getDirectoryInfo(@Path(value = "serverAddress") serverAddress : String, @Path(value = "path") path : String,
                         @Path(value = "userID") userID : String, @Path(value = "password") password : String) : Call<LogisticResult>

    /**
     * 파일의 자료를 Base64 Encoding하여 반환 한다.
     */
    @GET(value = "api/LogisticService/GetFileData/{serverAddress}/{path}/{filePath}/{userID}/{password}")
    fun getFileData(@Path(value = "serverAddress") serverAddress : String, @Path(value = "path") path : String,
                    @Path(value = "filePath") filePath : String, @Path(value = "userID") userID : String, @Path(value = "password") password : String) : Call<LogisticResult>

    /**
     * Base64 Encoding된 자료를 파일로 저장 한다.
     */
    @POST(value = "api/LogisticService/SetFileData/{serverAddress}/{path}/{filePath}/{userID}/{password}")
    fun setFileData(@Path(value = "serverAddress") serverAddress : String, @Path(value = "userID") userID : String,
                    @Path(value = "password") password : String, @Body fileInfo : FileParameter) : Call<LogisticResult>
}