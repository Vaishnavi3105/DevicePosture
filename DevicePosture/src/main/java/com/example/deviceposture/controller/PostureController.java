
package com.example.deviceposture.controller;

import com.example.deviceposture.model.AlertEvent;
import com.example.deviceposture.model.DevicePosture;
import com.example.deviceposture.service.PostureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/*
 * GET /posture/devices - Returns all 2 devices with posture calculated from the 61 alerts in alerting.json
GET /posture/device/{deviceId} - Get specific device (e.g., 7242K25)
GET /posture/alerts - Returns all 61 alert events
GET /posture/alerts/{hostname} - Returns alerts filtered by hostname
GET /posture/risk-report - Returns the AI-generated risk report
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/posture")
public class PostureController {

    @Autowired
    private PostureService postureService;

    //all devices
    @GetMapping("/devices")
    public List<DevicePosture> getAllDevices() {
        return postureService.getAllMockDevices();
    }

    //single device
    @GetMapping("/device/{deviceId}")
    public DevicePosture getDevicePosture(@PathVariable String deviceId) {
        return postureService.getMockPosture(deviceId);
    }

    //all alerts
    @GetMapping("/alerts")
    public List<AlertEvent> getAllAlerts() {
        return postureService.getAllAlertEvents();
    }

    //alerts by device
    @GetMapping("/alerts/{hostname}")
    public List<AlertEvent> getAlertsByHost(@PathVariable String hostname) {
        return postureService.getAlertEventsByHost(hostname);
    }

    //current risk report
    @GetMapping("/risk-report")
    public ResponseEntity<Map<String, String>> getRiskReport() {
        try {
            String content = postureService.getRiskReportContent();
            Map<String, String> response = new HashMap<>();
            response.put("content", content);
            response.put("format", "markdown");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Risk report not found");
            return ResponseEntity.status(404).body(error);
        }
    }

    //generate AI risk report
    @PostMapping("/risk-report/generate")
    public ResponseEntity<Map<String, String>> generateRiskReport() {
        try {
            String content = postureService.generateRiskReport();
            Map<String, String> response = new HashMap<>();
            response.put("content", content);
            response.put("format", "markdown");
            response.put("message", "Risk report generated successfully");
            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate risk report: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    //device posture scores from CSV
    @GetMapping("/scores")
    public List<com.example.deviceposture.model.DevicePostureScore> getDevicePostureScores() {
        return postureService.loadDevicePostureScores();
    }
}
