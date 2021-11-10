package co.kr.istn.imatedata

/**
 * Imate 인증 정보
 *
 * @param authId 식별자
 * @param authCode 인증코드
 * @param authType 인증유형
 * @param userData 사용자 데이터
 */
data class ImateAuthInfo (var authId: String, var authCode : String, var  authType : String, var userData : String)