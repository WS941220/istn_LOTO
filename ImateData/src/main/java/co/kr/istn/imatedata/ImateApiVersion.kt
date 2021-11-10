package co.kr.istn.imatedata

enum class ImateApiVersion {
    /**
     * V1 API 기본 인증으로 처리함
     */
    V1,

    /**
     * V2 API JwtToken 인증으로 API 인증을 처리함
     */
    V2
}