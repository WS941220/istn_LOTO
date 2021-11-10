package co.kr.istn.imatedata.dataset

import kotlin.reflect.KType

/**
 * 데이트 테이블 Class
 */
class DataTable(var tableName: String, var dataColumns: DataColumns) {
    //테이블의 데이터 Row의 컬렉션
    private var dataRows: DataRows = DataRows()

    //Class 초기화
    init {
        dataRows = DataRows()
    }

    //생성자
    constructor() : this("Table1", DataColumns())

    /**
     * 현재의 Row를 가져온다.
     *
     * @param rowIndex Row Index
     */
    fun getCurrentDataRow(rowIndex: Int): DataRow {
          if (dataRows.size < rowIndex)
            throw IndexOutOfBoundsException("Out Of Row index $rowIndex / ${dataRows.size}")

        var row = dataRows[rowIndex]

        //삭제된 row이면 다음 삭제안된 row를 반환 한다.
        if (row.getRowStatus() == RowStatus.Deleted)
            row = dataRows.asIterable().drop(rowIndex).dropWhile { r -> r.getRowStatus() == RowStatus.Deleted }.first()

        return row
    }

    /**
     * 데이커 컬럼의 크기
     */
    fun getDataColumnCount(): Int {
        return dataColumns.size
    }

    /**
     * 컬럼이름의 컬럼 인덱스를 가져 온다.
     *
     * @param colName 컬럼이름
     */
    fun getColumnIndex(colName: String): Int {
        return try {
            for ((valueIndex, columnName) in dataColumns) {
                if (colName == columnName)
                    return valueIndex
            }
            -1
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 컬럼의 이름의 리스트를 가져 온다.
     */
    fun getDataColumnNames(): ArrayList<String> {
        val columns = ArrayList<String>()
        return try {
            for ((_, columnName) in dataColumns) {
                columns.add(columnName)
            }
            columns
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     *  인덱스의 데이터 컬럼을 가져 온다.
     *
     *  @param index 인덱스
     */
    fun getDataColumn(index: Int): DataColumn {
        return try {
            dataColumns[index]
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 컬럼이름으로 데이터 컬럼을 가져온다.
     *
     * @param colName 컬럼이름
     */
    fun getDataColumn(colName: String): DataColumn {
        return dataColumns[getColumnIndex(colName)]
    }

    /**
     * DataRow를 추가 한다.
     *
     * @param dataRow Data Row
     */
    fun setDataRow(dataRow: DataRow) {
        try {
            dataRows.add(dataRow)
            dataRow.attachDataTable(this)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 비어 았는 새로운 Data Row를 추가 한다.
     */
    fun newDataRow(): DataRow {
        return try {
            val newRow = DataRow()
            for ((valueIndex) in dataColumns) {
                newRow.newValue(valueIndex, "")
            }

            newRow.attachDataTable(this)
            newRow.setRowStatus(RowStatus.Addnew)
            newRow
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 삭제안된 Row의 개수를 반환 한다.
     */
    fun getRowCount(): Int {
        try {
            return dataRows.asIterable().count { r -> r.getRowStatus() != RowStatus.Deleted }
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 지정된 상태의 Row의 개수를 반환 한다.
     *
     * @param rowStatus Row 상태
     */
    fun getRowCount(rowStatus: RowStatus): Int {
        try {
            return dataRows.asIterable().count { r -> r.getRowStatus() == rowStatus }
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 인덱스의 Row를 가져온다.
     *
     * @param rowIndex Row 인덱스
     */
    fun getDataRow(rowIndex: Int): DataRow {
        try {
            return getCurrentDataRow(rowIndex)
        } catch (ex: Exception) {
            throw ex
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * Row의 컬럼 값을 설정 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex 컬럼 인덱스
     * @param value 컬럼의 값
     */
     fun setRowValue(rowIndex: Int, colIndex: Int, value: Any?) {
        try {
            getCurrentDataRow(rowIndex).setValue(colIndex, value)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * Row의 컬럼 값을 설정 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colName 컬럼 이름
     * @param value 컬럼의 값
     */
    fun setRowValue(rowIndex: Int, colName: String, value: Any?) {
        try {
            getCurrentDataRow(rowIndex).setValue(colName, value)
        } catch (ex: Exception) {
            throw ex
        }
    }


    /**
     * 지정된 상태의 Data Row의 컬렉션을 반환 한다.
     *
     * @param rowStatus Row 상태
     */
    fun getDataRows(rowStatus: RowStatus): DataRows {
        val dataRows = DataRows()

        val rows = dataRows.asIterable().takeWhile { r -> r.getRowStatus() == rowStatus }
        dataRows.addAll(rows);

        return dataRows
    }

    /**
     * Row를 삭제 한다.
     *
     * @param rowIndex Row 인덱스
     */
    fun deleteRow(rowIndex: Int) {
        getCurrentDataRow(rowIndex).delete()
    }
    //---------------------------------------------------------------------------------------------

    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex Column 인덱스
     * @param rowVersion Row 버전
     */
    fun getRowValue(rowIndex: Int, colIndex: Int, rowVersion: RowVersion): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colIndex, rowVersion)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colName Column 이름
     * @param rowVersion Row 버전
     */
    fun getRowValue(rowIndex: Int, colName: String, rowVersion: RowVersion): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colName, rowVersion)
        } catch (ex: Exception) {
            throw ex
        }
    }

    //------------------------------------------------------------------------------------------
    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex Column 인덱스
     */
    fun getRowValueAny(rowIndex: Int, colIndex: Int): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colIndex, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colName Column 이름
     */
    fun getRowValueAny(rowIndex: Int, colName: String): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colName, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex Column 인덱스
     */
    fun getRowValueString(rowIndex: Int, colIndex: Int): String? {
        return try {
            getCurrentDataRow(rowIndex).getValueString(colIndex, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * Row의 컬럼값을 가져온다.
     *
     * @param rowIndex Row 인덱스
     * @param colName Column 이름
     */
    fun getRowValueString(rowIndex: Int, colName: String): String? {
        return try {
            getCurrentDataRow(rowIndex).getValueString(colName, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     * 로의 컬럼 자료를 반환 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex Column 인덱스
     */
    inline fun <reified T> getRowValue(rowIndex: Int, colIndex: Int): T? {
        return try {
            getCurrentDataRow(rowIndex).getValue<T>(colIndex, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     *로의 컬럼 자료를 반환 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colName Column 이름
     */
    inline fun <reified T> getRowValue(rowIndex: Int, colName: String): T? {
        return try {
            getCurrentDataRow(rowIndex).getValue<T>(colName, RowVersion.Current)
        }catch (ex: Exception) {
            throw ex
        }
     }

    /**
     * 로의 컬럼 자료를 반환 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colIndex Column 인덱스
     * @param type Kotlin Type
     */
    fun getRowValue(rowIndex: Int, colIndex: Int, type: KType): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colIndex, type, RowVersion.Current)
        } catch (ex: Exception) {
            throw ex
        }
    }

    /**
     *로의 컬럼 자료를 반환 한다.
     *
     * @param rowIndex Row 인덱스
     * @param colName Column 이름
     * @param type Kotlin Type
     */
    fun getRowValue(rowIndex: Int, colName: String, type: KType): Any? {
        return try {
            getCurrentDataRow(rowIndex).getValue(colName, type, RowVersion.Current)
        }catch (ex: Exception) {
            throw ex
        }
    }
}