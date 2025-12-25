package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;

    @DeleteMapping("/clear-all")
    public ApiResponse<String> clearAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        return ApiResponse.<String>builder()
                .code(200)
                .message("Đã xóa tất cả cache")
                .result("Cache cleared: " + cacheManager.getCacheNames())
                .build();
    }
}
