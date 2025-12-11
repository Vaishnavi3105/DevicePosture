
package com.example.deviceposture.model;

public class AgentStatus {
    private String version;
    private boolean isHealthy;

    public AgentStatus(String version, boolean isHealthy) {
        this.version = version;
        this.isHealthy = isHealthy;
    }

    public String getVersion() { return version; }
    public boolean isHealthy() { return isHealthy; }
}
