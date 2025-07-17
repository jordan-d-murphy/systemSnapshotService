package com.example.snapshot.controller;

import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class SnapshotController {
    @GetMapping("/api/snapshot")
    public Map<String, Object> getSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();
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

    public String bytesToMB(long bytes) {
        return (bytes / 1024 /1024) + " MB";
    }

}
