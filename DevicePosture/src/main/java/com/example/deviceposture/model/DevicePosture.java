
package com.example.deviceposture.model;

import java.util.List;

public class DevicePosture {
    private String deviceId;
    private int postureScore;
    private String healthStatus;
    private List<String> recommendations;
    private AgentStatus agentStatus;
    private TelemetryStatus telemetryStatus;

    public DevicePosture(String deviceId, int postureScore, String healthStatus, List<String> recommendations,
                         AgentStatus agentStatus, TelemetryStatus telemetryStatus) {
        this.deviceId = deviceId;
        this.postureScore = postureScore;
        this.healthStatus = healthStatus;
        this.recommendations = recommendations;
        this.agentStatus = agentStatus;
        this.telemetryStatus = telemetryStatus;
    }

    public String getDeviceId() { return deviceId; }
    public int getPostureScore() { return postureScore; }
    public String getHealthStatus() { return healthStatus; }
    public List<String> getRecommendations() { return recommendations; }
    public AgentStatus getAgentStatus() { return agentStatus; }
    public TelemetryStatus getTelemetryStatus() { return telemetryStatus; }
}
