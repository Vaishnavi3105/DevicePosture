package com.example.deviceposture.service;

import com.example.deviceposture.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/*Key Features:
Automatically groups alerts by hostname
Calculates average posture score from alert scores
Determines health status based on severity distribution
Generates actionable recommendations based on threat levels
All JSON properties properly mapped with unknown fields ignored
 */
@Service
public class PostureService {

    private Map<String, DevicePosture> deviceDB = new HashMap<>();
    private List<AlertEvent> alertEvents = new ArrayList<>();

    @PostConstruct
    public void loadAlertingData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("alerting.json").getInputStream();
            AlertingData alertingData = mapper.readValue(inputStream, AlertingData.class);
            alertEvents = alertingData.getEvents();
            
            // Group events by hostname and calculate device posture
            Map<String, List<AlertEvent>> eventsByHost = alertEvents.stream()
                    .collect(Collectors.groupingBy(AlertEvent::getHostName));
            
            eventsByHost.forEach((hostname, events) -> {
                DevicePosture posture = calculatePostureFromEvents(hostname, events);
                deviceDB.put(hostname, posture);
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to some default data if file not found
            loadFallbackData();
        }
    }

    private DevicePosture calculatePostureFromEvents(String hostname, List<AlertEvent> events) {
        // Calculate average score
        int avgScore = (int) events.stream()
                .mapToInt(AlertEvent::getScore)
                .average()
                .orElse(0);
        
        // Count severity levels
        long criticalCount = events.stream().filter(e -> "s4".equals(e.getSeverity())).count();
        long highCount = events.stream().filter(e -> "s3".equals(e.getSeverity())).count();
        long mediumCount = events.stream().filter(e -> "s2".equals(e.getSeverity())).count();
        
        // Determine health status based on severity and score
        String healthStatus;
        List<String> recommendations = new ArrayList<>();
        
        if (criticalCount > 0) {
            healthStatus = "Critical";
            recommendations.add("Immediate action required - " + criticalCount + " critical alerts detected");
            recommendations.add("Investigate high-risk threats immediately");
        } else if (highCount > 0 || avgScore < 50) {
            healthStatus = "At Risk";
            recommendations.add("Review " + highCount + " high severity alerts");
            recommendations.add("Enhance security monitoring");
        } else if (mediumCount > 0 || avgScore < 70) {
            healthStatus = "Moderate";
            recommendations.add("Address " + mediumCount + " medium severity alerts");
            recommendations.add("Perform routine security check");
        } else {
            healthStatus = "Good";
            recommendations.add("Device security is satisfactory");
        }
        
        recommendations.add("Total alerts: " + events.size());
        
        // Create agent and telemetry status based on alerts
        boolean hasRecentAlerts = events.size() > 0;
        AgentStatus agentStatus = new AgentStatus("6.0.0", !hasRecentAlerts || criticalCount == 0);
        TelemetryStatus telemetryStatus = new TelemetryStatus(true, criticalCount == 0, highCount == 0);
        
        return new DevicePosture(
            hostname,
            avgScore,
            healthStatus,
            recommendations,
            agentStatus,
            telemetryStatus
        );
    }

    private void loadFallbackData() {
        deviceDB.put("dev-001", new DevicePosture("dev-001", 85, "Good",
                List.of("No action needed"),
                new AgentStatus("5.2.1", true),
                new TelemetryStatus(true, true, true)));
    }

    public DevicePosture getMockPosture(String deviceId) {
        return deviceDB.getOrDefault(
            deviceId,
            new DevicePosture(deviceId, 0, "Unknown", List.of("No data available"),
                    new AgentStatus("unknown", false),
                    new TelemetryStatus(false, false, false))
        );
    }

    public List<DevicePosture> getAllMockDevices() {
        return new ArrayList<>(deviceDB.values());
    }

    public List<AlertEvent> getAllAlertEvents() {
        return new ArrayList<>(alertEvents);
    }

    public List<AlertEvent> getAlertEventsByHost(String hostname) {
        return alertEvents.stream()
                .filter(event -> hostname.equals(event.getHostName()))
                .collect(Collectors.toList());
    }

    public String getRiskReportContent() throws IOException {
        try {
            // Try to read from resources first
            InputStream inputStream = new ClassPathResource("risk_report.md").getInputStream();
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            // If not in resources, try the posture-patrol resources directory
            try {
                java.nio.file.Path path = java.nio.file.Paths.get("src/app/posture-patrol/resources/risk_report.md");
                return java.nio.file.Files.readString(path);
            } catch (IOException ex) {
                throw new IOException("Risk report not found");
            }
        }
    }

    public String generateRiskReport() throws IOException, InterruptedException {
        // Path to the Python script - using main.py from posture-patrol
        String scriptPath = "main.py";
        String workingDir = "src/app/posture-patrol";
        
        // Get the GOOGLE_API_KEY from environment or use a hardcoded value
        String apiKey = System.getenv("GOOGLE_API_KEY");
        
        // If API key is not set in Java environment, try to read from .env file or set a default
        if (apiKey == null || apiKey.isEmpty()) {
            // Try to read from .env file in the posture-patrol directory
            java.nio.file.Path envPath = java.nio.file.Paths.get(workingDir, ".env");
            if (java.nio.file.Files.exists(envPath)) {
                String envContent = java.nio.file.Files.readString(envPath);
                for (String line : envContent.split("\n")) {
                    if (line.startsWith("GOOGLE_API_KEY=")) {
                        apiKey = line.substring("GOOGLE_API_KEY=".length()).trim();
                        break;
                    }
                }
            }
        }
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IOException("GOOGLE_API_KEY is not set. Please set it in environment variables or create a .env file in src/app/posture-patrol/");
        }
        
        // Execute the Python script with environment variable
        ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/python3", "main.py");
        processBuilder.directory(new java.io.File(workingDir));
        processBuilder.redirectErrorStream(true);
        
        // Set the GOOGLE_API_KEY environment variable for the Python process
        Map<String, String> env = processBuilder.environment();
        env.put("GOOGLE_API_KEY", apiKey);
        
        Process process = processBuilder.start();
        
        // Read the output
        StringBuilder output = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        int exitCode = process.waitFor();
        
        // Log the output for debugging
        System.out.println("Python script output:\n" + output.toString());
        
        if (exitCode != 0) {
            throw new IOException("Failed to generate risk report. Python script exited with code: " + exitCode + "\nOutput: " + output.toString());
        }
        
        // Check if the report was generated/updated
        java.nio.file.Path reportPath = java.nio.file.Paths.get("src/app/posture-patrol/resources/risk_report.md");
        if (!java.nio.file.Files.exists(reportPath)) {
            throw new IOException("Risk report file not found after script execution. Output:\n" + output.toString());
        }
        
        // Check if output contains API quota errors
        String outputStr = output.toString();
        if (outputStr.contains("429") || outputStr.contains("quota exceeded") || outputStr.contains("Quota exceeded")) {
            // API quota exceeded, but we can still return the existing report
            System.out.println("Warning: API quota exceeded. Returning existing report.");
            String existingReport = java.nio.file.Files.readString(reportPath);
            // Add a note about the quota issue
            return "**Note: API quota exceeded. Showing most recent report.**\n\n" + existingReport;
        }
        
        // Read the newly generated/updated report
        return java.nio.file.Files.readString(reportPath);
    }

    /**
     * Load device posture scores from CSV file
     * @return List of DevicePostureScore objects
     */
    public List<DevicePostureScore> loadDevicePostureScores() {
        List<DevicePostureScore> scores = new ArrayList<>();
        
        try {
            // Try to load from the posture-patrol resources directory
            java.nio.file.Path csvPath = java.nio.file.Paths.get("src/app/posture-patrol/resources/devices_posture_score.csv");
            
            if (java.nio.file.Files.exists(csvPath)) {
                try (BufferedReader reader = java.nio.file.Files.newBufferedReader(csvPath)) {
                    String line;
                    boolean isFirstLine = true;
                    
                    while ((line = reader.readLine()) != null) {
                        // Skip header line
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }
                        
                        // Parse CSV line
                        String[] values = line.split(",");
                        if (values.length >= 6) {
                            String hostName = values[0].trim();
                            int s1 = Integer.parseInt(values[1].trim());
                            int s2 = Integer.parseInt(values[2].trim());
                            int s3 = Integer.parseInt(values[3].trim());
                            int s4 = Integer.parseInt(values[4].trim());
                            double postureScore = Double.parseDouble(values[5].trim());
                            
                            DevicePostureScore score = new DevicePostureScore(hostName, s1, s2, s3, s4, postureScore);
                            scores.add(score);
                        }
                    }
                }
            } else {
                System.err.println("CSV file not found at: " + csvPath);
            }
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading device posture scores from CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return scores;
    }
}

