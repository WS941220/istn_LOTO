package co.kr.istn.imatedata

import kotlinx.serialization.Serializable

/**
 *  컬럼 정보
 *
 *  @param name 이름
 *  @param dataType 데이터 유형
 */
@Serializable
data class XNColumnInfo(
    var ordinal: Int,
    var name : String,
    var isKey : Boolean,
    var dataType: QueryDataType
)