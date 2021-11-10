package co.kr.istn.imatelogistic

/**
 * 파일 파라미터
 *
 * @param paths 상대경로
 * @param filePaths 파일의 상대경로
 * @param fileDatas base64 인코딩된 파일데이터
 */
data class FileParameter (var paths : List<String>, var filePaths : List<String>, var fileDatas : List<String>)