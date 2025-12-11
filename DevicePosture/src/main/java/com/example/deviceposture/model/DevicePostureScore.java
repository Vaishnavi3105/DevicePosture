package com.example.deviceposture.model;

/**
 * Model representing device posture score data from CSV file
 */
public class DevicePostureScore {
    private String hostName;
    private int s1;
    private int s2;
    private int s3;
    private int s4;
    private double postureScore;

    public DevicePostureScore() {
    }

    public DevicePostureScore(String hostName, int s1, int s2, int s3, int s4, double postureScore) {
        this.hostName = hostName;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.postureScore = postureScore;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getS1() {
        return s1;
    }

    public void setS1(int s1) {
        this.s1 = s1;
    }

    public int getS2() {
        return s2;
    }

    public void setS2(int s2) {
        this.s2 = s2;
    }

    public int getS3() {
        return s3;
    }

    public void setS3(int s3) {
        this.s3 = s3;
    }

    public int getS4() {
        return s4;
    }

    public void setS4(int s4) {
        this.s4 = s4;
    }

    public double getPostureScore() {
        return postureScore;
    }

    public void setPostureScore(double postureScore) {
        this.postureScore = postureScore;
    }

    @Override
    public String toString() {
        return "DevicePostureScore{" +
                "hostName='" + hostName + '\'' +
                ", s1=" + s1 +
                ", s2=" + s2 +
                ", s3=" + s3 +
                ", s4=" + s4 +
                ", postureScore=" + postureScore +
                '}';
    }
}
