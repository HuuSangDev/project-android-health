package com.SelfCare.SelftCare.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok tự động tạo constructor cho biến final (cloudinary)
public class FileUploadsService {

    private final Cloudinary cloudinary;

    // ==========================
    // UPLOAD IMAGE
    // ==========================
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", UUID.randomUUID().toString(),
                        "resource_type", "image",
                        "folder", "images"   // thư mục cho ảnh
                )
        );

        return uploadResult.get("secure_url").toString();
    }


    // ==========================
    // UPLOAD VIDEO
    // ==========================
    public String uploadVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", UUID.randomUUID().toString(),
                        "resource_type", "video",   // QUAN TRỌNG
                        "folder", "videos"          // thư mục cho video
                )
        );

        return uploadResult.get("secure_url").toString();
    }
}