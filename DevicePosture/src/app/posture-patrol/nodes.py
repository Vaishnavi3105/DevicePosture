import json
import re
import pandas as pd
from state import LLMState
from config import model
from utils import load_json_data

# Generate device based tabular report
def generate_tabular_report(state: LLMState) -> dict:
    input_filename = "/Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol/resources/alerting.json"
    try:
        data_string = json.dumps(load_json_data(input_filename), indent=2)
        
        prompt = f"""
            You are a data analyst. Your task is to aggregate the provided JSON data.
            Based on the data, calculate the count of threat events, 
            categorized by severity level (s1, s2, s3, s4), for each unique 'hostName'.

            The source data is here:
            ---DATA_START---
            {data_string}
            ---DATA_END---

            Your response MUST be ONLY the resulting data formatted as a single, valid JSON object.
            Do not include markdown, backticks (```json), or any explanatory text.

            The JSON object should use the 'hostName' as the primary key.
            The value for each 'hostName' key should be another dictionary 
            containing the counts for "s1", "s2", "s3", and "s4".

            Example of the required output format:
            {{
            "host_A": {{ "s1": 5, "s2": 2, "s3": 0, "s4": 1 }},
            "host_B": {{ "s1": 10, "s2": 0, "s3": 1, "s4": 8 }}
            }}
        """
        
        response  = model.generate_content(prompt).text
        response = re.sub(r"```(json)?", "", response, flags=re.IGNORECASE)
        response = response.strip()
        data_dict = json.loads(response)
        print("Generated tabular report (state): ", data_dict)
        return {"tabularReport": data_dict}
        
    except Exception as e:
        print(f"\nError during 'generate_tabular_report' GenAI call: {e}")
        return {"tabularReport": {}}

# Calculate Posture Score and create CSV
def calculate_posture_score(state: LLMState) -> dict:
    report = state['tabularReport']
    print("Calculating posture scores from: ", report)
    new_posture_scores = {}
    
    for key, item in report.items():
        if isinstance(item, dict):
            totalScore = 0
            sevScore = 0
            for sev, value in item.items():
                totalScore += value
                if sev == "s1":
                    sevScore = sevScore + value*10
                elif sev == "s2":
                    sevScore = sevScore + value*8
                elif sev == "s3":
                    sevScore = sevScore + value*6
                elif sev == "s4":
                    sevScore = sevScore + value*4
                                            
            if totalScore > 0:
                new_posture_scores[key] = (sevScore/(totalScore*10))*100
            else:
                new_posture_scores[key] = 0.0
                                
    print("Calculated posture scores: ", new_posture_scores)
    
    # --- Create combined CSV ---
    try:
        df_report = pd.DataFrame.from_dict(report, orient='index')
        df_score = pd.DataFrame.from_dict(new_posture_scores, orient='index', columns=['postureScore'])
        df_combined = df_report.join(df_score)
        df_combined.reset_index(inplace=True)
        df_combined.rename(columns={'index': 'hostName'}, inplace=True)
        combined_filename = "/Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol/resources/devices_posture_score.csv"
        df_combined.to_csv(combined_filename, index=False)
        
        print(f"Posture scores CSV saved successfully to: {combined_filename}")
    except Exception as e:
        print(f"Error saving CSV file: {e}")
    
    return {"postureScore": new_posture_scores}

# Generate Markdown Risk Report
def generate_risk_report(state: LLMState) -> dict:
    output_filename = state['riskReportFileName']
    input_filename = "/Users/vaishnavi.lahoti/Desktop/DevicePosture/src/app/posture-patrol/resources/alerting.json"
    try:
        data_string = json.dumps(load_json_data(input_filename), indent=2)
        
        prompt = f"""
            You are a senior cyber security analyst. Your task is to generate a clear, structured risk report based on the JSON threat data provided.
            The report must be formatted using markdown (headings, bold, lists).
            ---JSON DATA---
            {data_string}
            ---END DATA---
            
            Please structure your report exactly as follows:
            # Risk Report

            ## 1. Executive Summary
            (Provide a 2-3 sentence high-level overview of the most critical findings and affected hosts.)

            ## 2. Detailed Risk Analysis by Host
            (Create a separate sub-section for each unique `hostName` found in the data. For each host, provide the following three points based on the data.)

            ### Host: [hostname_1]
            * **Severity Breakdown:** (Summarize the count of threats by `severity`, e.g., s1: 2, s2: 5, s4: 1)
            * **Suspicious Processes:** (List the notable `processName` entries associated with these threats.)
            * **Key Detection Tags:** (Summarize the unique `detectionTags` observed on this host.)

            ### Host: [hostname_2]
            * **Severity Breakdown:** ...
            * **Suspicious Processes:** ...
            * **Key Detection Tags:** ...
            (Add a new section for every other host)

            ## 3. Possible Remediation Steps
            (Based *only* on the analysis above, provide a bulleted list of actionable remediation steps. These steps should be generic but reference the types of findings.)

            * **Example:** "Investigate and terminate suspicious processes (e.g., `[processName]`) on affected hosts."
            * **Example:** "Quarantine hosts (e.g., `[hostname]`) exhibiting high-severity threats."
            * **Example:** "Update security policies to block or alert on `[detectionTag]`."

            Generate the report based *only* on the data provided.
            """
        
        response  = model.generate_content(prompt)
        report_content = response.text

        # Save the Report
        with open(output_filename, 'w', encoding='utf-8') as f:
            f.write(report_content)      
        print(f"\nSuccess! Report saved to: {output_filename}")
        
    except Exception as e:
        print(f"\nError during 'generate_risk_report' GenAI call: {e}")
        
    return {}

def compile_results(state: LLMState) -> dict:
    return {"status": "Complete and Compiled"}
