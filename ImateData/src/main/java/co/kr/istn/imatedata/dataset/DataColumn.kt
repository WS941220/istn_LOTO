package co.kr.istn.imatedata.dataset

import co.kr.istn.imatedata.QueryDataType

/**
 * 데이터 커럼
 */
data class DataColumn(var valueIndex: Int, var columnName: String, var ordinal: String, var isKey: Boolean, var dataType: QueryDataType)