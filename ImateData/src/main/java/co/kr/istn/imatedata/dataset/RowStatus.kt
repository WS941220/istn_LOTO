package co.kr.istn.imatedata.dataset

/**
 *  데이터 로의 상태
 */
enum class RowStatus {
    /**
     * 데이터 테이블에 부착 되지 않음
     */
    UnAttached,

    /**
     * 변경 안됨
     */
    UnChanged,

    /**
     * 변경 됨
     */
    Modified,

    /**
     * 삭제 됨
     */
    Deleted,

    /**
     * 추가 됨
     */
    Addnew
}