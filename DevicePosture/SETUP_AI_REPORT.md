# AI Risk Report Setup Guide

## Current Issues Found:

1. ❌ **GOOGLE_API_KEY not set** - Required for AI generation
2. ⚠️ **Python version warning** - Using 3.9.6 (past EOL), recommended 3.10+

## Quick Setup Steps:

### Step 1: Get Google API Key

1. Go to: https://aistudio.google.com/app/apikey
2. Click "Create API Key"
3. Copy the key

### Step 2: Set Environment Variable

**Option A - For current terminal session only:**
```bash
export GOOGLE_API_KEY="your-api-key-here"
```

**Option B - Permanent (add to ~/.zshrc):**
```bash
echo 'export GOOGLE_API_KEY="your-api-key-here"' >> ~/.zshrc
source ~/.zshrc
```

### Step 3: Restart Spring Boot Application

After setting the API key, restart the application:
```bash
# Stop current application
pkill -f spring-boot:run

# Start again
cd /Users/vaishnavi.lahoti/Desktop/DevicePosture
mvn spring-boot:run &
```

### Step 4: Test the Feature

1. Open dashboard: http://localhost:8080 or open index.html
2. Click "🤖 Generate AI Risk Report"
3. Wait 10-30 seconds
4. Report will download automatically

## Verification Commands:

Check if API key is set:
```bash
echo $GOOGLE_API_KEY | head -c 10
```

Test Python script manually:
```bash
cd /Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol
python3 script.py
```

## Alternative: Use Existing Report

If you don't want to set up AI generation, you can still:
- Click "📄 Download Current Risk Report" to get the existing report
- Export data as CSV or JSON files

## Troubleshooting:

**Error: "module 'importlib.metadata' has no attribute 'packages_distributions'"**
- This is due to Python 3.9.6 being outdated
- Solution: Use system Python or upgrade to Python 3.10+

**Error: "GOOGLE_API_KEY environment variable not set"**
- Make sure to export the variable BEFORE starting the Spring Boot app
- The Java process needs to inherit this environment variable

## Quick Test (Without Spring Boot):

```bash
# Set API key
export GOOGLE_API_KEY="your-key-here"

# Go to script directory
cd /Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol

# Run script
python3 script.py

# Check if report was generated
ls -lh risk_report.md
```

If this works, the Spring Boot integration will also work!
