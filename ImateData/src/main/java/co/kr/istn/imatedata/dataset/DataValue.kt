package co.kr.istn.imatedata.dataset

import co.kr.istn.imatedata.QueryDataType

/**
 * 데이터의 값
 *
 * @param value 값
 * @param type 깂의 데이터 유형
 */
class DataValue(value: Any?, type: QueryDataType) {
    //원래 값
    private var originalValue: Any? = null
    //현재 값
    private var currentValue: Any? = null
    //값의 데이터 유형
    private var dataType: QueryDataType

    //초기화
    init {
        currentValue = value
        originalValue = value
        dataType = type
    }

    /**
     * 값을 가져온다
     *
     * @param rowVersion row의 버전
     */
    fun getValue(rowVersion: RowVersion): Any? {
        return if (rowVersion == RowVersion.Current) currentValue else originalValue
    }

    /**
     * 값을 가져온다
     */
    fun getValue(): Any? {
        return getValue(RowVersion.Current)
    }

    /**
     * 값을 설정 한다
     *
     * @param value 값
     */
    fun setValue(value: Any?) {
        currentValue = value
    }

    /**
     * 깂을 변경 했음
     */
    fun acceptChanged() {
        originalValue = currentValue
    }

    /**
     * 값 수정 여부
     */
    fun isModifed(): Boolean {
        return if (currentValue == null) originalValue != null else currentValue != originalValue
    }
}