package com.api.readinglog.common.image;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtil {

    // 파일의 확장자를 반환해주는 메서드
    public static String getExt(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                .orElse("");
    }

    // 이미지 존재 여부 확인
    public static boolean isEmptyProfileImg(MultipartFile file) {
        return (file == null || file.isEmpty());
    }

}
