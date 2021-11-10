package co.kr.istn.imatedata.dataset

/**
 * 데이터 커럼 배열
 */
class DataColumns: ArrayList<DataColumn>()
{
    /**
     * 컬럼이 존재 하는지 Check 한다.
     *
     * @param colName 컬럼 이름
     */
    fun existsColumn(colName : String) : Boolean
    {
        return this.asIterable().any{c->c.columnName == colName}
    }

    /**
     * 컬럼 정보를 반환 한다.
     *
     * @param colName 컬럼 이름
     */
    fun  getDataColumn(colName : String) : DataColumn?
    {
        return this.asIterable().firstOrNull { c->c.columnName == colName }
    }
}