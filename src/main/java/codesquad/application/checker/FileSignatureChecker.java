package codesquad.application.checker;

import java.util.HashMap;
import java.util.Map;

public class FileSignatureChecker {

    private static final Map<String, String> FILE_SIGNATURES = new HashMap<>();

    static {
        FILE_SIGNATURES.put("FFD8FF", "jpg");
        FILE_SIGNATURES.put("89504E47", "png");
        FILE_SIGNATURES.put("47494638", "gif");
        FILE_SIGNATURES.put("25504446", "pdf");
        // 필요에 따라 더 많은 파일 서명을 추가할 수 있습니다.
    }

    public static String getFileExtension(byte[] data) {
        String fileSignature = bytesToHex(data, 5); // 처음 4바이트를 사용
        for (Map.Entry<String, String> entry : FILE_SIGNATURES.entrySet()) {
            if (fileSignature.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null; // 확장자를 찾을 수 없는 경우 null을 반환
    }

    private static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length && i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }
}