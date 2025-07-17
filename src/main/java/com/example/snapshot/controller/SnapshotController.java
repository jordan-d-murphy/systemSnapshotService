package com.example.snapshot.controller;

import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class SnapshotController {

    @Autowired
    private StringRedisTemplate redis;

    @GetMapping("/api/snapshot")
    public Map<String, Object> getSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();
        try {
            redis.opsForValue().increment("snapshot:counter");
        } catch (Exception e) {
            snapshot.put("redisError", e.getMessage());
        }
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            snapshot.put("hostname", localHost.getHostName());
            snapshot.put("ip", localHost.getHostAddress());
        } catch (Exception e) {
            snapshot.put("hostname", "unkown");
            snapshot.put("ip", "unknown");
        }
        snapshot.put("javaVersion", System.getProperty("java.version"));
        snapshot.put("os", System.getProperty("os.name"));
        snapshot.put("availableProcessors", Runtime.getRuntime().availableProcessors());

        Map<String, String> memory = new HashMap<>();
        memory.put("free", bytesToMB(Runtime.getRuntime().freeMemory()));
        memory.put("total", bytesToMB(Runtime.getRuntime().totalMemory()));
        memory.put("max", bytesToMB(Runtime.getRuntime().maxMemory()));
        snapshot.put("memory", memory);

        snapshot.put("timestamp", Instant.now().toString());
        UUID uuid = UUID.randomUUID();
        snapshot.put("snapshotId", uuid.toString());

        return snapshot;
    }

    @GetMapping("/api/stats")
    public Map<String, Object> getStats() {
        int total = 0;
        try {
            String count = redis.opsForValue().get("snapshot:counter");
            total = (count != null) ? Integer.parseInt(count) : 0;
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
        return Map.of("totalCalls", total);
    }

    public String bytesToMB(long bytes) {
        return (bytes / 1024 /1024) + " MB";
    }

}
