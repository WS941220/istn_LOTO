package co.kr.istn.imatedata

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *  Query Service Interface
 */
interface ImateQueryService {
    /**
     * 데이터 쿼리 배치 처리
     *
     * @param transactionId 트랜잭션ID
     * @param queryMesssages 쿼리 메세지 집합
     * @return 쿼리 실행 결과
     */
    @POST( value = "api/QueryService/ExecuteQueryBatch/{transactionId}")
    fun executeQueryBatch(@Path(value = "transactionId") transactionId: String, @Body() queryMesssages : List<QueryMessage> ) : Call<QueryRunResult>

    /**
     * 데이터 조회 쿼리
     *
     * @param transactionId 트랜잭션ID
     * @param dataSource 데이터소스
     * @param query 쿼리 문자열
     * @return 쿼리 실행 결과
     */
    @POST( value = "api/QueryService/ExecuteQuery/{transactionId}/{dataSource}")
    fun executeQuery(@Path(value = "transactionId") transactionId: String, @Path(value = "dataSource") dataSource: String, @Body() query : String ) : Call<QueryRunResult>

    /**
     * 데이터 수정 쿼리
     *
     *  @param transactionId 트랜잭션ID
     *  @param dataSource 데이터소스
     *  @param query 쿼리 문자열
     *  @return 변경된 자료의 개수
     */
    @POST( value = "api/QueryService/ExecuteNoneQuery/{transactionId}/{dataSource}")
    fun executeNoneQuery(@Path(value = "transactionId") transactionId: String, @Path(value = "dataSource") dataSource: String, @Body() query : String ) : Call<QueryRunResult>
}