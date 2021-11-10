package co.kr.istn.imatedata.dataset

import java.lang.Exception
import java.security.KeyException
import kotlin.reflect.KMutableProperty

/**
 * DataSet 클래스
 *
 * @param transactionId 트랜잭션 ID
 */
class DataSet(val transactionId : String) {
    //데이터 테이블 컬렉션
    val tables: HashMap<String, DataTable> = HashMap()

    /**
     * 데이터 Objet를 만든다.
     */
    inline fun <reified T> getDataObject(tableName: String): ArrayList<T> {
        try {
            if (!tables.containsKey(tableName))
                throw KeyException("Table '${tableName}' not found")

            val resultClass = T::class
            val resultList = arrayListOf<T>()

            val dataTable = tables[tableName]!!

            for (rowIndex in 0 until dataTable.getRowCount()) {
                val resultObject =
                    resultClass.constructors.firstOrNull { it.parameters.count() == 0 }?.call()

                resultClass.members.forEach { m ->
                    try {
                        if (m is KMutableProperty<*>) {
                            val dataCol = dataTable.dataColumns.getDataColumn(m.name)

                            if (dataCol != null) {
                                val dataVal = dataTable.getRowValue(rowIndex, dataCol.columnName, m.returnType)
                                m.setter.call(resultObject, dataVal)
                            }
                        }
                    } catch (e: Exception) {
                        throw e
                    }
                }
                resultList.add(resultObject as T)
            }

            return resultList
        } catch (e: Exception) {
            throw e
        }
    }
}