package co.kr.istn.imatedata

/**
 * 토근 ID 발급 정보
 *
 * @param id 식별 ID
 * @param email E-mail 주소
 * @param name 이름
 * @param role 롤
 * @param refdata1 참조 자료1
 * @param refdata2 참조 자료2
 * @param refdata3 참조 자료3
 * @param refdata4 참조 자료4
 * @param refdata5 참조 자료5
 */
data class TokenIdInfo(var id: String, var email: String, var name: String, var role: String,
                       var refdata1: String, var refdata2: String, var refdata3: String, var refdata4: String, var refdata5: String)