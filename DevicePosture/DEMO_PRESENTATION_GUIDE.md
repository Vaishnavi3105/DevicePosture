# Device Posture Dashboard - Demo Presentation Guide

## 🎯 Project Overview

**Project Name:** Device Posture Dashboard  
**Purpose:** Security monitoring and risk assessment platform that analyzes device security alerts and provides actionable insights  
**Tech Stack:** Java Spring Boot (Backend) + HTML/JavaScript + Chart.js (Frontend) + Python AI (Report Generation)

---

## 📋 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Backend Components](#backend-components)
3. [Frontend Features](#frontend-features)
4. [Data Flow](#data-flow)
5. [API Endpoints](#api-endpoints)
6. [Demo Walkthrough](#demo-walkthrough)
7. [Key Features Highlight](#key-features-highlight)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    DEVICE POSTURE DASHBOARD                  │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐      ┌──────────────┐     ┌─────────────┐│
│  │   Frontend   │◄────►│   Backend    │◄───►│   Python    ││
│  │  (HTML/JS)   │      │ (Spring Boot)│     │  AI Script  ││
│  │  Chart.js    │      │   REST API   │     │   (Gemini)  ││
│  └──────────────┘      └──────────────┘     └─────────────┘│
│         │                      │                     │       │
│         │                      │                     │       │
│         ▼                      ▼                     ▼       │
│  ┌──────────────┐      ┌──────────────┐     ┌─────────────┐│
│  │ Visualizations│      │   Business   │     │  AI Risk    ││
│  │ - Pie Charts │      │    Logic     │     │  Report     ││
│  │ - Tables     │      │ - Posture    │     │ Generation  ││
│  │ - Stats      │      │   Calculation│     │             ││
│  └──────────────┘      └──────────────┘     └─────────────┘│
│                               │                              │
│                        ┌──────▼──────┐                      │
│                        │  Data Layer  │                      │
│                        │ alerting.json│                      │
│                        │ (61 events)  │                      │
│                        └─────────────┘                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 Backend Components

### 1. **Model Classes** (Data Structure)

#### `AlertEvent.java`
```java
// Represents a single security alert/threat event
- traceId: Unique identifier
- hostName: Device name (e.g., "7242K25")
- severity: s1 (low), s2 (medium), s4 (critical)
- score: Risk score (0-100)
- eventType: "Threat Detected"
- processName: e.g., "powershell.exe"
- user: Username
- eventDate: When it occurred
```

**Purpose:** Captures each security threat detection from the JSON data

#### `AlertingData.java`
```java
// Wrapper for the entire JSON structure
- total: Total number of events
- items: Number of items
- events: List<AlertEvent>
```

**Purpose:** Deserializes the alerting.json file structure

#### `DevicePosture.java`
```java
// Calculated security posture for each device
- deviceId: Hostname
- postureScore: Average score (0-100)
- healthStatus: "Critical", "At Risk", "Moderate", "Good"
- recommendations: List of actionable suggestions
- agentStatus: Security agent information
- telemetryStatus: Monitoring capabilities
```

**Purpose:** Represents the computed security state of a device

#### `AgentStatus.java` & `TelemetryStatus.java`
```java
// Supporting classes for device monitoring status
AgentStatus: version, healthy (boolean)
TelemetryStatus: process, network, file (monitoring flags)
```

### 2. **Service Layer** (`PostureService.java`)

**Key Methods:**

```java
@PostConstruct
public void loadAlertingData()
```
- **When:** Runs when application starts
- **What:** Loads alerting.json, groups alerts by hostname
- **Output:** Populates deviceDB with calculated postures

```java
private DevicePosture calculatePostureFromEvents(hostname, events)
```
- **Input:** Hostname and its alert events
- **Logic:** 
  - Calculates average score
  - Counts severity levels (s1, s2, s4)
  - Determines health status based on critical alerts
  - Generates recommendations
- **Output:** DevicePosture object

**Example Logic:**
```
IF critical alerts (s4) > 0 → Status = "Critical"
ELSE IF high alerts OR avg score < 50 → Status = "At Risk"
ELSE IF medium alerts OR avg score < 70 → Status = "Moderate"
ELSE → Status = "Good"
```

```java
public String generateRiskReport()
```
- **What:** Executes Python script to generate AI risk report
- **How:** Uses ProcessBuilder to run `python3 script.py`
- **Output:** Markdown report content

### 3. **Controller Layer** (`PostureController.java`)

**REST API Endpoints:**

```java
@GetMapping("/posture/devices")
// Returns: List of all devices with calculated postures
// Use case: Dashboard initialization

@GetMapping("/posture/device/{deviceId}")
// Returns: Single device posture
// Use case: Device detail view

@GetMapping("/posture/alerts")
// Returns: All 61 alert events
// Use case: Alerts table, CSV export

@GetMapping("/posture/alerts/{hostname}")
// Returns: Alerts filtered by hostname
// Use case: Device-specific alert analysis

@GetMapping("/posture/risk-report")
// Returns: Current risk report (JSON with markdown content)
// Use case: Download existing report

@PostMapping("/posture/risk-report/generate")
// Triggers: Python AI script execution
// Returns: Newly generated risk report
// Use case: Fresh AI analysis
```

---

## 🎨 Frontend Features

### 1. **Dashboard Layout** (`index.html`)

**Visual Components (Top to Bottom):**

```
┌─────────────────────────────────────┐
│   🛡️ Device Posture Dashboard      │
├─────────────────────────────────────┤
│  📊 Statistics Cards                │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌─────┐│
│  │  2   │ │  61  │ │  20  │ │ 54  ││
│  │Devices│ │Alerts│ │Crit. │ │Score││
│  └──────┘ └──────┘ └──────┘ └─────┘│
├─────────────────────────────────────┤
│  🎨 Pie Charts                      │
│  ┌─────┐  ┌─────┐  ┌─────┐        │
│  │Health│  │Sever│  │Alerts│        │
│  │Dist. │  │ity  │  │/Host │        │
│  └─────┘  └─────┘  └─────┘        │
├─────────────────────────────────────┤
│  📥 Export Buttons                  │
│  [AI Report] [Download] [CSV]...   │
├─────────────────────────────────────┤
│  📋 Device Details Table            │
│  Hostname | Score | Status | ...   │
├─────────────────────────────────────┤
│  🚨 Recent Alerts Table             │
│  Latest 20 threat detections        │
└─────────────────────────────────────┘
```

### 2. **JavaScript Data Flow**

```javascript
async function loadDashboard() {
    // 1. Fetch data from both endpoints in parallel
    const [devicesResponse, alertsResponse] = await Promise.all([
        fetch("/posture/devices"),
        fetch("/posture/alerts")
    ]);
    
    // 2. Parse JSON
    allDevices = await devicesResponse.json();
    allAlerts = await alertsResponse.json();
    
    // 3. Process data
    alertsByHost = groupAlertsByHostname(allAlerts);
    
    // 4. Populate UI
    populateStats();           // Stats cards
    populateDeviceTable();     // Device table
    populateAlertsTable();     // Alerts table
    createHealthPieChart();    // Chart 1
    createSeverityPieChart();  // Chart 2
    createAlertsByHostChart(); // Chart 3
}
```

### 3. **Export Functions**

```javascript
// 1. Download existing risk report
async function downloadRiskReport()
// - Fetches from /posture/risk-report
// - Downloads as risk_report.md
// - NO API key needed

// 2. Generate NEW AI risk report
async function generateAndDownloadRiskReport()
// - POSTs to /posture/risk-report/generate
// - Executes Python script
// - Takes 10-30 seconds
// - Requires GOOGLE_API_KEY

// 3. Export devices to CSV
function exportDevicesCSV()
// - Converts device data to CSV format
// - Includes: scores, alerts, recommendations
// - Downloads as devices_report.csv

// 4. Export alerts to CSV
function exportAlertsCSV()
// - Converts all alerts to CSV
// - Includes: hostname, severity, score, process
// - Downloads as alerts_report.csv

// 5. Export complete JSON report
function exportFullReport()
// - Creates comprehensive JSON with:
//   - Summary statistics
//   - All devices
//   - All alerts
//   - Alerts grouped by host
// - Downloads as full_report_YYYY-MM-DD.json
```

---

## 🔄 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     APPLICATION STARTUP                      │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Load alerting.json (61 events) │
         └──────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Group alerts by hostname       │
         │   - 7242K25: 40 alerts          │
         │   - 724W1124H2: 21 alerts       │
         └──────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Calculate Device Posture       │
         │   For each hostname:             │
         │   - Average score                │
         │   - Count severities             │
         │   - Determine health status      │
         │   - Generate recommendations     │
         └──────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Store in deviceDB (HashMap)    │
         │   Ready to serve API requests    │
         └──────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     USER OPENS DASHBOARD                     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Browser loads index.html       │
         │   Executes loadDashboard()       │
         └──────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Parallel API Calls:            │
         │   GET /posture/devices           │
         │   GET /posture/alerts            │
         └──────────────────────────────────┘
                            │
                            ▼
         ┌──────────────────────────────────┐
         │   Process & Display:             │
         │   - Statistics (2, 61, 20, 54)  │
         │   - Pie Charts (3 charts)       │
         │   - Device Table (2 rows)       │
         │   - Alerts Table (20 rows)      │
         └──────────────────────────────────┘
```

---

## 📡 API Endpoints Summary

| Method | Endpoint | Purpose | Response |
|--------|----------|---------|----------|
| GET | `/posture/devices` | Get all devices | List<DevicePosture> |
| GET | `/posture/device/{id}` | Get single device | DevicePosture |
| GET | `/posture/alerts` | Get all alerts | List<AlertEvent> |
| GET | `/posture/alerts/{hostname}` | Alerts by host | List<AlertEvent> |
| GET | `/posture/risk-report` | Get current report | JSON {content, format} |
| POST | `/posture/risk-report/generate` | Generate new AI report | JSON {content, message} |

---

## 🎬 Demo Walkthrough Script

### **Introduction (30 seconds)**
> "Today I'm presenting a Device Posture Dashboard that helps security teams monitor and assess device security in real-time. This system analyzes threat detection events, calculates risk scores, and provides actionable insights through an intuitive web interface."

### **Part 1: Data Source (1 minute)**
> "Our system ingests security alert data from a JSON file containing 61 threat detection events. These are real security events captured from devices including:
> - Process executions (like PowerShell scripts)
> - Severity levels from s1 (low) to s4 (critical)
> - Threat categories like credential access, privilege escalation
> - Multiple affected hosts"

**Show:** `alerting.json` file briefly

### **Part 2: Backend Processing (2 minutes)**
> "The Spring Boot backend automatically processes this data on startup:
> 
> 1. **Data Loading**: Reads the JSON using Jackson ObjectMapper
> 2. **Grouping**: Organizes alerts by hostname (device)
> 3. **Calculation**: For each device, we calculate:
>    - Average risk score from all alerts
>    - Count of critical (s4), medium (s2), and low (s1) severity alerts
>    - Overall health status based on severity distribution
> 4. **Recommendation Engine**: Generates actionable suggestions based on the analysis"

**Show Code:** Open `PostureService.java` → `calculatePostureFromEvents()` method

> "For example, if a device has ANY critical (s4) alerts, it's marked as 'Critical' status and gets immediate action recommendations."

### **Part 3: Frontend Dashboard (3 minutes)**
> "Now let's look at the user interface..."

**Open Dashboard in Browser**

> **Statistics Cards:**
> "At the top, users immediately see key metrics:
> - 2 devices being monitored
> - 61 total security alerts
> - 20 critical alerts requiring immediate attention
> - Average posture score of 54 (out of 100)"

> **Pie Charts:**
> "Three visualizations provide instant insights:
> 1. **Health Distribution**: Shows both devices are in Critical status
> 2. **Severity Breakdown**: 34 low, 7 medium, 20 critical alerts
> 3. **Alerts by Host**: Device 7242K25 has 40 alerts, showing it's the primary concern"

> **Export Options:**
> "After viewing insights, users can export data in multiple formats:
> - Download the existing AI-generated risk report
> - Generate a NEW AI report using Google's Gemini AI
> - Export device data or alerts as CSV for spreadsheet analysis
> - Export complete dataset as JSON for integration with other tools"

> **Device Table:**
> "The detailed table shows:
> - Each device's posture score
> - Health status (color-coded: red for critical)
> - Breakdown of alert types
> - Security agent version
> - Specific recommendations like 'Immediate action required - 14 critical alerts detected'"

> **Alerts Table:**
> "Finally, the 20 most recent alerts with:
> - Which device/hostname
> - Severity level
> - Risk score
> - What process triggered it (mostly PowerShell)
> - When it occurred"

### **Part 4: AI Risk Report Generation (2 minutes)**
> "One of the most powerful features is AI-powered risk report generation..."

**Click "Generate AI Risk Report" button** (or explain if not set up)

> "When a user clicks this:
> 1. Frontend sends POST request to `/posture/risk-report/generate`
> 2. Backend executes our Python script
> 3. Script sends the alert data to Google's Gemini AI
> 4. AI analyzes the data and generates a comprehensive markdown report including:
>    - Executive summary
>    - Risk analysis by host
>    - Detailed threat breakdowns
>    - Prioritized recommendations
> 5. Report downloads automatically to the user's machine"

**Show existing risk_report.md file**

> "Here's an example of the generated report. Notice how the AI provides:
> - Professional analysis of the 61 events
> - Groups threats by affected host
> - Highlights high-severity events like Mimikatz credential dumping
> - Provides context about attack patterns"

### **Part 5: Export Features Demo (1 minute)**

**Click "Export Devices CSV"**
> "CSV exports are instant and can be opened in Excel for further analysis or reporting to management."

**Click "Export Full Report JSON"**
> "The JSON export includes everything - perfect for feeding into SIEM systems or custom analysis tools."

### **Part 6: Technical Highlights (1 minute)**
> "From a technical perspective, here are the key accomplishments:
> 
> **Backend:**
> - RESTful API using Spring Boot
> - Automatic JSON deserialization with Jackson
> - Dynamic posture calculation algorithm
> - Python integration for AI report generation
> 
> **Frontend:**
> - Responsive design with Chart.js visualizations
> - Asynchronous data loading with fetch API
> - Multiple export formats (MD, CSV, JSON)
> - Real-time status updates for long-running operations
> 
> **Data Processing:**
> - Intelligent grouping and aggregation
> - Severity-based risk scoring
> - Automated recommendation generation"

### **Conclusion (30 seconds)**
> "This Device Posture Dashboard transforms raw security alert data into actionable insights. Security teams can:
> - Quickly identify at-risk devices
> - Understand threat distribution
> - Generate professional reports for stakeholders
> - Export data for compliance or further analysis
> 
> All with a clean, intuitive interface that requires no manual configuration."

---

## 🎯 Key Features to Highlight

### 1. **Intelligent Data Processing**
- Automatic JSON parsing
- Alert grouping by device
- Dynamic risk calculation
- Severity-based health assessment

### 2. **Rich Visualizations**
- Real-time statistics
- Interactive pie charts
- Color-coded severity indicators
- Comprehensive data tables

### 3. **AI Integration**
- Google Gemini AI for report generation
- Natural language analysis
- Professional markdown reports
- Context-aware recommendations

### 4. **Multiple Export Options**
- Risk reports (Markdown)
- Device data (CSV)
- Alert data (CSV)
- Complete dataset (JSON)

### 5. **User Experience**
- Clean, intuitive interface
- Logical information flow
- No-click data refresh
- Status indicators for long operations
- Mobile-responsive design

---

## 💡 Demo Tips

### **What to Emphasize:**
1. **Problem Solving**: "Managing 61 security alerts across multiple devices manually is overwhelming"
2. **Automation**: "Our system automatically analyzes and prioritizes threats"
3. **Actionability**: "Users get specific recommendations, not just data"
4. **Flexibility**: "Multiple export formats for different use cases"
5. **AI Integration**: "Leveraging cutting-edge AI for professional reporting"

### **Potential Questions & Answers:**

**Q: How does the posture score work?**
> A: "It's the average of all alert scores for that device. Each alert has a risk score (0-100), and we calculate the mean. Lower is better."

**Q: Why are both devices marked Critical?**
> A: "Both have critical severity (s4) alerts. In cybersecurity, even one critical alert requires immediate attention, so the system flags them prominently."

**Q: Can this work with live data?**
> A: "Absolutely! Currently it reads from a JSON file, but the same endpoints could be connected to a real-time alert feed from a SIEM or EDR system."

**Q: What happens if the AI generation fails?**
> A: "Users can still download the existing report and use all other export features. The system gracefully handles errors and shows clear status messages."

**Q: How scalable is this?**
> A: "The current implementation handles 61 events efficiently. For production with thousands of events, we'd add database persistence and pagination, but the architecture supports that easily."

---

## 📊 Code Statistics

```
Backend (Java):
- 5 Model Classes
- 1 Service Class (150+ lines)
- 1 Controller Class (90+ lines)
- 6 API Endpoints

Frontend (HTML/JS):
- 1 HTML Dashboard
- 8 JavaScript Functions
- 3 Chart.js Visualizations
- 2 Data Tables
- 5 Export Functions

Data:
- 61 Security Events
- 2 Monitored Devices
- 3 Severity Levels
- 20 Critical Alerts
```

---

## 🚀 Running the Demo

### Prerequisites:
```bash
# Java 21
java -version

# Maven
mvn -version

# Python 3 (for AI reports)
python3 --version
```

### Startup:
```bash
# 1. Navigate to project
cd /Users/vaishnavi.lahoti/Desktop/DevicePosture

# 2. Start backend
mvn spring-boot:run

# 3. Open dashboard
open devicePostureFrontendMain/index.html
```

### Verification:
- Backend: http://localhost:8080/posture/devices
- Frontend: Open index.html in browser
- Check: Statistics cards load with data
- Check: All 3 pie charts render
- Check: Tables populate with data

---

## 🎓 Learning Outcomes Demonstrated

1. **Full-Stack Development**: Integration of backend API with frontend visualization
2. **RESTful API Design**: Clean, resource-based endpoints
3. **Data Processing**: JSON parsing, grouping, aggregation algorithms
4. **AI Integration**: External API integration (Google Gemini)
5. **UX Design**: Logical information architecture and user flow
6. **Security Domain Knowledge**: Understanding of threat severity, posture assessment
7. **Modern Web Technologies**: Chart.js, Fetch API, async/await
8. **Export Functionality**: Multiple data format support

---

**Good luck with your demo! 🎉**
