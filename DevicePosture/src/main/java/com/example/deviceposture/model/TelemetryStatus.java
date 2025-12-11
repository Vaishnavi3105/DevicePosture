
package com.example.deviceposture.model;

public class TelemetryStatus {
    private boolean process;
    private boolean network;
    private boolean file;

    public TelemetryStatus(boolean process, boolean network, boolean file) {
        this.process = process;
        this.network = network;
        this.file = file;
    }

    public boolean isProcess() { return process; }
    public boolean isNetwork() { return network; }
    public boolean isFile() { return file; }
}
