// src/main/java/com/example/restaurant_management/service/impl/TableTokenServiceImpl.java
package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.TableTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TableTokenServiceImpl implements TableTokenService {

    private final TableRepository tableRepository;

    @Value("${qr.table.secret}")          // Bắt buộc: secret mạnh (base64 hoặc chuỗi raw)
    private String secret;

    @Value("${qr.table.token.prefix:t1_}") // Prefix version để dễ rotate sau này
    private String prefix;

    // Cache 2 chiều token<->id để resolve O(1) sau lần đầu
    private final Map<Long, String> idToToken = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenToId = new ConcurrentHashMap<>();
    private volatile long lastRefreshMillis = 0L;
    private final long ttlMillis = Duration.ofMinutes(10).toMillis();

    @Override
    public String hashTableId(Long tableId) {
        String normalized = prefix + base64Url(hmacSha256(("table:" + tableId).getBytes(StandardCharsets.UTF_8)));
        // Rút gọn còn 16 bytes đầu cho gọn (đủ 128-bit entropy sau HMAC)
        // Bởi vì base64Url của 16 bytes ~ 22 ký tự.
        // Để giữ cố định độ dài, ta cắt digest trước khi base64:
        byte[] full = hmacSha256(("table:" + tableId).getBytes(StandardCharsets.UTF_8));
        byte[] first16 = Arrays.copyOf(full, 16);
        return prefix + base64Url(first16);
    }

    @Override
    public boolean matches(Long tableId, String token) {
        return constantTimeEquals(hashTableId(tableId), normalize(token));
    }

    @Override
    public Long resolveTableId(String token) {
        String tk = normalize(token);

        refreshCacheIfNeeded();

        // Cache hit?
        Long cached = tokenToId.get(tk);
        if (cached != null) return cached;

        // Lần đầu / token mới → duyệt ID bàn (số lượng nhỏ nên OK), map vào cache
        // Ưu tiên không load entity nặng: chỉ lấy list ID
        List<Long> ids = tableRepository.findAllIds();

        for (Long id : ids) {
            String t = idToToken.computeIfAbsent(id, this::hashTableId);
            tokenToId.putIfAbsent(t, id);
            if (constantTimeEquals(t, tk)) {
                return id;
            }
        }
        throw new NoSuchElementException("Invalid or unknown table token");
    }

    // ------------- helpers -------------

    private String normalize(String token) {
        if (token == null) return null;
        return token.startsWith(prefix) ? token : prefix + token;
    }

    private void refreshCacheIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastRefreshMillis > ttlMillis) {
            synchronized (this) {
                if (now - lastRefreshMillis > ttlMillis) {
                    idToToken.clear();
                    tokenToId.clear();
                    lastRefreshMillis = now;
                }
            }
        }
    }

    private byte[] hmacSha256(byte[] message) {
        try {
            byte[] keyBytes = secretLooksBase64(secret)
                    ? Base64.getDecoder().decode(secret)
                    : secret.getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
            return mac.doFinal(message);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compute table token", e);
        }
    }

    private boolean secretLooksBase64(String s) {
        // heuristics đơn giản
        return s != null && s.matches("^[A-Za-z0-9+/=_-]{16,}$");
    }

    private String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }
}
