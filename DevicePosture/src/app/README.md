# Risk Report

This Python script uses the Google Gemini API to read data from a local JSON file, analyze it based on a user-defined prompt, and generate a professional, markdown-formatted report.

## 📖 Description

The script is designed to automate data analysis and reporting. It loads a specified JSON file, sends its contents along with a system prompt (defining the AI's role) and a user prompt (defining the task) to the Google GenAI API. It then saves the AI-generated report to a local `.md` file.

## ✨ Features

* Loads local JSON data.
* Connects to the Google GenAI API (using `gemini-2.5-pro`).
* Uses a system prompt to define the AI's persona as a data analyst.
* Generates a markdown-formatted report based on the data and a user's instructions.
* Includes error handling for API keys, file not found, and JSON formatting.
* Disables all 4 harm categories (safety settings) to allow for processing complex/raw data without being blocked.

## Project Structure
```
.
├── posture-patrol
│   ├── config.py
│   ├── diagram
│   │   └── posture_score_workflow.png
│   ├── llm_workflow.ipynb
│   ├── main.py
│   ├── nodes.py
│   ├── resources
│   │   ├── alerting.json
│   │   ├── devices_posture_score.csv
│   │   └── risk_report.md
│   ├── state.py
│   └── utils.py
├── pyproject.toml
├── README.md
└── requirements.txt
```

## 📋 Prerequisites

Before you run this script, you will need:
* Python 3.7+
* A Google API Key for the Gemini API. You can get one from [Google AI Studio](https://aistudio.google.com/).

## ⚙️ Installation & Setup

1.  **Clone or Download:**
    Get the `script.py` file onto your local machine.

2.  **Create a Virtual Environment (Recommended):**
    ```bash
    # For Unix/macOS
    python3 -m venv .venv
    source .venv/bin/activate

    # For Windows
    python -m venv .venv
    .venv\Scripts\activate.bat
    ```

3.  **Install Dependencies:**
    This script requires the `google-genai` library.
    ```bash
    pip install google-genai
    ```

4.  **Set Your API Key:**
    The script reads your API key from an environment variable named `GOOGLE_API_KEY`.

    **On macOS/Linux:**
    ```bash
    export GOOGLE_API_KEY="YOUR_API_KEY_HERE"
    ```

    **On Windows (Command Prompt):**
    ```cmd
    set GOOGLE_API_KEY="YOUR_API_KEY_HERE"
    ```
    *(Note: For PowerShell, the command is `$env:GOOGLE_API_KEY="YOUR_API_KEY_HERE"`)*

## 🏃 Usage

1.  **Prepare Your Data:**
    The script is hardcoded to look for a file named `alerting.json`. Create this file in the same directory as the script and populate it with your JSON data.

2.  **Run the Script:**
    With your virtual environment active and your API key set, simply run the script:
    ```bash
    python3 script.py
    ```

3.  **Check the Output:**
    The script will print its progress to the console. If successful, it will create a file named `risk_report.md` in the same directory.

## 🔧 Customization

To change the input file, output file, or the report instructions, you can modify the variables in the `if __name__ == "__main__":` block at the bottom of `script.py`:
