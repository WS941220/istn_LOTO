package co.kr.istn.imatedata

import co.kr.istn.imatedata.dataset.*
import co.kr.istn.utility.getRandomString

/**
 * 데이터 어뎁터
 *
 * @param imateData 서버 인터페이스 개체
 */
class ImateDataAdapter(val imateData: ImateData) {

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
    constructor(baseUrl: String, userId: String = "", userPassword: String = "", sslIgnore: Boolean = false,
                connectTimeout : Long = 30, readTimeOut : Long = 1200, writeTimeout : Long = 300)
            : this(ImateData(baseUrl, userId, userPassword, sslIgnore, connectTimeout, readTimeOut, writeTimeout))

    //초가화
    init {
        lastApiResult = ""
        lastApiMessage = ""
        lastUserMessage = ""
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 커럼정보를 DataColums 집합으로 변경 한다.
     *
     * @param columnInfos 컬럼 정보보
     *
     */
    private fun columnInfoToDataColumn(columnInfos: Iterable<XNColumnInfo>): DataColumns {
        val dataColumns = DataColumns()
        return try {
            for ((ordinal, name, isKey, dataType) in columnInfos)
                dataColumns.add(DataColumn(ordinal, name, ordinal.toString(), isKey, dataType))

            dataColumns
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 쿼리 값을 DataTable로 변환 한다.
     *
     * @param queryValue 쿼리값
     */
    private fun queryValueToDataTable(queryValue: QueryValue): DataTable {
        val dataColumns = columnInfoToDataColumn(queryValue.columnInfos)
        val dataTable = DataTable(queryValue.queryName, dataColumns)
        for ((rowValue) in queryValue.rows) {
            val dataRow = DataRow(dataTable)
            val values = rowValue.toList()
            for ((odinal) in dataColumns) {
                dataRow.newValue(odinal, values[odinal])
            }
            dataTable.setDataRow(dataRow)
        }

        return dataTable
    }

    /**
     * 쿼리 결과를 DataSet으로 변환 한다.
     *
     * @param queryResult 쿼리 결과
     */
    private fun queryRunResultToDataSet(queryResult: QueryRunResult): DataSet {
        val dataSet = DataSet(queryResult.transactionId)

        for (queryValue in queryResult.results) {
            dataSet.tables[queryValue.queryName] = queryValueToDataTable(queryValue)
        }

        return dataSet
    }

    //----------------------------------------------------------------------------------------------
    val tokenAdapter : TokenServiceAdapter?
        get() = imateData.tokenServiceAdapter

    suspend fun otpAuth(authInfo: ImateAuthInfo) : Boolean
    {
        return try {
            val authResult =  imateData.otpAuthAsync(authInfo).await()

            if(authResult && imateData.lastErrorMessage.isNullOrEmpty())
            {
                lastApiResult = ""
                lastApiMessage = ""
            }
            else
            {
                lastApiResult = "ERROR"
                lastApiMessage = imateData.lastErrorMessage
            }

            lastUserMessage = ""

            return authResult;
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     *  비동기로 데이터베이스 자료를 가져와 DataSet으로 변환 하여 반환 한다.
     *
     *  @param transactionId 트랜잭션 ID
     *  @param queryMesssages 쿼리 메시지
     *  @param callback 실행 결과 반환 함수
     */
    fun dbSelectToDataSetAsync(transactionId: String, queryMesssages: List<QueryMessage>, callback: (result: DataSet?) -> Unit) {
        imateData.executeQueryBatchAsync(transactionId, queryMesssages) { result ->
            lastApiResult = result?.apiResult ?: ""
            lastApiMessage = result?.apiMessage ?: ""
            lastUserMessage = result?.userMessage ?: ""

            callback(if (result == null || result.apiResult != "OK") null
            else queryRunResultToDataSet(result))
        }
    }

    /**
     * 데이터베이스 자료를 가져와 DataSet으로 변환 하여 반환 한다.
     *
     * @param queryMesssages 쿼리 메시지
     */
    suspend fun dbSelectToDataSet(queryMesssages: List<QueryMessage>): DataSet? {
        return try {
            val transactionId: String = getRandomString(10)
            val queryResult = imateData.executeQueryBatchAsync(transactionId, queryMesssages).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            if (queryResult == null || queryResult.apiResult != "OK") null
            else queryRunResultToDataSet(queryResult)
        } catch (e: Exception) {
            throw e
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * 비동기로 데이터베이스 자료를 가져와 DataSet으로 변환 하여 반환 한다.
     *
     * @param transactionId 트랜잭션 ID
     * @param dataSource 데이터소스
     * @param query 쿼리
     * @param callback 실행 결과 반환 함수
     */
    fun dbSelectToDataSetAsync(transactionId: String, dataSource: String, query: String, callback: (result: DataSet?) -> Unit) {
        imateData.executeQueryAsync(transactionId, dataSource, query) { result ->
            lastApiResult = result?.apiResult ?: ""
            lastApiMessage = result?.apiMessage ?: ""
            lastUserMessage = result?.userMessage ?: ""

            callback(if (result == null || result.apiResult != "OK") null
            else queryRunResultToDataSet(result))
        }
    }

    /**
     * 데이터베이스 자료를 가져와 DataSet으로 변환 하여 반환 한다.
     *
     * @param dataSource 데이터 소스
     * @param query 쿼리
     */
    suspend fun dbSelectToDataSet(dataSource: String, query: String): DataSet? {
        return try {
            val transactionId: String = getRandomString(10)
            val queryResult = imateData.executeQueryAsync(transactionId, dataSource, query).await()

            lastApiResult = queryResult?.apiResult ?: ""
            lastApiMessage = queryResult?.apiMessage ?: ""
            lastUserMessage = queryResult?.userMessage ?: ""

            if (queryResult === null || queryResult.apiResult != "OK") null
            else queryRunResultToDataSet(queryResult)
        } catch (e: Exception) {
            throw e
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * 비동기로 쿼리를 실행 하고 으로 변경된 Row의 개수를 반환 받는다.
     *
     * @param transactionId 트랜잭션 ID
     * @param dataSource 데이터소스
     * @param query 쿼리
     * @param callback 실행 결과 반환 함수
     */
    fun dbExecuteAsync(transactionId: String, dataSource: String, query: String,
                       callback: (transactionId: String, apiResult: String, apiMessage: String, result: Int) -> Unit) {
        imateData.executeNoneQueryAsync(transactionId, dataSource, query) { tid: String, apiRes: String, apiMsg: String, res: Int ->
            callback(tid, apiRes, apiMsg, res)
        }
    }

    /**
     * 쿼리를 실행 하고 변경된 Row의 개수를 반환 받는다.
     *
     * @param dataSource 데이터 소스
     * @param query 쿼리
     */
    suspend fun dbExecute(dataSource: String, query: String): Int {
        return try {
            val transactionId: String = getRandomString(10)
            val result = imateData.executeNoneQueryAsync(transactionId, dataSource, query).await()

            if(result < 0)
                throw Exception(imateData.lastErrorMessage)

            result
        } catch (e: Exception) {
            throw e
        }
    }
}