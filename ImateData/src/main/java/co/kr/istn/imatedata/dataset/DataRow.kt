package co.kr.istn.imatedata.dataset

import co.kr.istn.imatedata.QueryDataType
import co.kr.istn.utility.stringConvert
import kotlin.reflect.KType

/**
 * Data Row Class
 *
 * @param dataTable 데이터 테이블
 */
class DataRow(var dataTable: DataTable?) {
    private var rowValues: HashMap<Int, DataValue>
    private var rowStatus: RowStatus = RowStatus.UnChanged

    init {
        rowValues = HashMap()
        if(dataTable != null)
            attachDataTable(dataTable!!)
    }

    constructor() : this(null) {
        rowValues = HashMap()
        rowStatus = RowStatus.UnAttached
    }

    /**
     * Row가 부착된 DataTable
     *
     * @param dataTable 데이터테이블
     */
    internal fun attachDataTable(dataTable: DataTable) {
        this.dataTable = dataTable
        this.rowStatus = RowStatus.UnChanged
    }

    /**
     * SAP 숫자 체크
     *
     * @param colValue 컬럼의 값
     */
    private fun checkSAPNumber(colValue: String): String {
        var value = colValue
        value = value.trim { it <= ' ' }

        //SAP일 경우 "-" 가 마자막에 붙어 있음(숫자를 문자로 반환 됭경우)
        if (value.endsWith("-")) {
            val pos = value.indexOf("-")
            value = "-" + value.substring(0, pos)
        }
        return value
    }

    /**
     *  컬럼의 값을 새로 추가
     *  @param index 새로 추가할 컬럼 인덱스
     *  @param value 컬럼 값
     */
    fun newValue(index: Int, value: Any?)
    {
        try
        {
            //기존에 컬럼이 존재 하면 지운다.
            if (rowValues.containsKey(index))
                rowValues.remove(index)

            //새로운 컬럼에 값을 추가 한다.
            rowValues[index] = DataValue(value, if (dataTable == null) QueryDataType.String else dataTable!!.getDataColumn(index).dataType)
        }
        catch (ex: Exception)
        {
            throw ex
        }
    }

    /**
     * 인덱스 위치의 컬럼 값 설정
     *
     * @param index 인덱스
     * @param value 컬럼값
     */
    fun setValue(index: Int, value: Any?)
    {
        try
        {
            rowStatus = RowStatus.Modified
            rowValues[index]?.setValue(value)
        }
        catch (ex: Exception)
        {
            throw ex
        }
    }

    /**
     * 컬럼이름으로 컬럼 값 설정
     *
     * @param colName 컬럼이믈
     * @param value 값
     */
    fun setValue(colName: String, value: Any?)
    {
        try
        {
            val index: Int = dataTable!!.getColumnIndex(colName)
            if (index == -1)
                throw java.lang.Exception("Not found '$colName' Column")

            setValue(index, value)
        }
        catch (ex: Exception)
        {
            throw ex
        }
    }

    /**
     * Row 삭제
     */
    fun delete()
    {
        rowStatus = RowStatus.Deleted
    }

    /**
     * Row 변경사항 반영
     */
    fun acceptChaneged()
    {
        if (rowStatus != RowStatus.Deleted)
            rowStatus = RowStatus.UnChanged
    }

    /**
     * Row 상태
     */
    fun getRowStatus(): RowStatus
    {
        return rowStatus
    }

    /**
     * Row 상태 설정
     *
     * @param status 상태
     */
    fun setRowStatus(status: RowStatus)
    {
        rowStatus = status
    }

    //--------------------------------------------------------------------
    /**
     * 인덱스 위치의 컬럼의 값 반환
     *
     * @param  index 컬럼의 인덱스
     */
    fun getValueAny(index: Int): Any?
    {
        return getValue(index, RowVersion.Current)
    }

    /**
     * 컬럼이름으로 컬럼의 값 반환
     *
     * @param colName 컬럼이름
     */
    fun getValueAny(colName: String): Any?
    {
        return getValue(colName, RowVersion.Current)
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값 반환
     *
     * @param index 인덱스
     * @param rowVersion Row의 버전
     */
    fun getValueAny(index: Int, rowVersion: RowVersion?): Any?
    {
        return rowValues[index]?.getValue(rowVersion!!)
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값  반환
     *
     * @param colName 인덱스
     * @param rowVersion Row의 버전
     */
    fun getValueAny(colName: String, rowVersion: RowVersion?): Any?
    {
        return try {
            val index: Int = dataTable!!.getColumnIndex(colName)
            if (index == -1)
                throw java.lang.Exception("Not found '$colName' Column")

            getValueAny(index, rowVersion)
        }
        catch (ex: Exception)
        {
            throw ex
        }
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값 반환
     *
     * @param index 인덱스
     * @param rowVersion Row의 버전
     */
    fun getValueString(index: Int, rowVersion: RowVersion?): String?
    {
        return rowValues[index]?.getValue(rowVersion!!)?.toString()
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값  반환
     *
     * @param colName 인덱스
     * @param rowVersion Row의 버전
     */
    fun getValueString(colName: String, rowVersion: RowVersion?): String?
    {
        return try {
            val index: Int = dataTable!!.getColumnIndex(colName)
            if (index == -1)
                throw java.lang.Exception("Not found '$colName' Column")

            getValueString(index, rowVersion)
        }
        catch (ex: Exception)
        {
            throw ex
        }
    }

    /**
     * 인덱스 위치의 컬럼의 값 반환
     *
     * @param  index 컬럼의 인덱스
     */
    inline fun <reified  T> getValue(index: Int): T?
    {
        return stringConvert<T>(getValueString(index, RowVersion.Current))
    }

    /**
     * 컬럼이름으로 컬럼의 값 반환
     *
     * @param colName 컬럼이름
     */
    inline fun <reified  T> getValue(colName: String):T?
    {
        return stringConvert<T>(getValueString(colName, RowVersion.Current))
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값 반환
     *
     * @param index 인덱스
     * @param rowVersion Row의 버전
     */
    inline fun <reified  T> getValue(index: Int, rowVersion: RowVersion?): T?
    {
        return stringConvert<T>(getValueString(index, rowVersion))
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값  반환
     *
     * @param colName 인덱스
     * @param rowVersion Row의 버전
     */
    inline fun <reified  T> getValue(colName: String, rowVersion: RowVersion?): T?
    {
        return stringConvert<T>(getValueString(colName, rowVersion))
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값 반환
     *
     * @param index 인덱스
     * @param rowVersion Row의 버전
     * @param type Kotlin Type
     */
    fun getValue(index: Int, type: KType, rowVersion: RowVersion?): Any?
    {
        return stringConvert(getValueString(index, rowVersion), type)
    }

    /**
     * 인덱스 위치의 컬럼의 Row Version값  반환
     *
     * @param colName 인덱스
     * @param rowVersion Row의 버전
     * @param type Kotlin Type
     */
    fun getValue(colName: String, type: KType, rowVersion: RowVersion?): Any?
    {
        return stringConvert(getValueString(colName, rowVersion), type)
    }
}