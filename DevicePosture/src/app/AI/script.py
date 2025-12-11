import os
import sys
import json
import google.generativeai as genai

# --- Initialize Google GenAI Client ---
try:
    api_key = os.environ.get("GOOGLE_API_KEY")
    if not api_key:
        raise ValueError("Error: GOOGLE_API_KEY environment variable not set.")
    genai.configure(api_key=api_key)
except Exception as e:
    print(f"Error: Unable to initialize Google GenAI client.")
    print(e)
    sys.exit(1)

def load_json_data(filepath):
    """Safely loads a JSON file from the given path."""
    # This function is unchanged.
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)
            return data
    except FileNotFoundError:
        print(f"Error: JSON file not found at path: {filepath}")
        return None
    except json.JSONDecodeError:
        print(f"Error: Could not decode JSON. Check file for formatting errors: {filepath}")
        return None
    except Exception as e:
        print(f"An unexpected error occurred while reading the file: {e}")
        return None

def generate_report_from_data(json_data, user_prompt, output_filename):
    """
    Sends data and a prompt to the Google GenAI API and saves the report.
    """
    print("Connecting to Google GenAI to generate report...")

    # Convert the Python dictionary back to a string for the prompt
    data_string = json.dumps(json_data, indent=2)

    # --- 1. Create the System and User Prompts ---
    # The system prompt sets the AI's role.
    system_prompt = """
    You are a professional data analyst and report writer. 
    Your task is to generate a clear and well-structured report based on 
    the JSON data and user instructions provided. 
    Use markdown for all formatting (headings, bold, lists).
    """

    # The user prompt combines the data and the user's instructions.
    final_user_prompt = f"""
    Here is the data I want you to analyze, in JSON format:
    ---DATA_START---
    {data_string}
    ---DATA_END---

    Here are my instructions for the report:
    "{user_prompt}"

    Please generate the report based *only* on the data provided.
    """


    # Combine system and user prompts into a single string.
    combined_prompt = f"{system_prompt}\n\n{final_user_prompt}"

    # --- Call the Google GenAI API ---
    try:
        safety_settings = {
            'HARM_CATEGORY_HARASSMENT': 'BLOCK_NONE',
            'HARM_CATEGORY_HATE_SPEECH': 'BLOCK_NONE',
            'HARM_CATEGORY_SEXUALLY_EXPLICIT': 'BLOCK_NONE',
            'HARM_CATEGORY_DANGEROUS_CONTENT': 'BLOCK_NONE',
        }
        
        model = genai.GenerativeModel(
            'gemini-2.5-pro',
            safety_settings=safety_settings
        )

        # Call the API using model.generate_content()
        response = model.generate_content(combined_prompt)
        
        # Extract the text from the response.
        # It's good practice to check if the response was blocked.
        if not response.parts:
             print("\nError: The response was blocked by safety settings.")
             if response.prompt_feedback:
                 print(f"Reason: {response.prompt_feedback}")
             return # Exit the function

        report_content = response.text

        # --- Save the Report ---
        with open(output_filename, 'w', encoding='utf-8') as f:
            f.write(report_content)
            
        print(f"\nSuccess! Report saved to: {output_filename}")

    except Exception as e:
        print(f"\nError during Google GenAI API call: {e}")

# --- Main execution ---
if __name__ == "__main__":
    print("--- AI Report Generator (from JSON) ---")
    
    # Get inputs
    json_file_path = "alerting.json"
    user_report_prompt = "Generate a risk report based on hostname. Consider severity, processName and detectionTags."
    output_file_name = "risk_report"

    # Add .md extension if not provided
    if not output_file_name.endswith((".md", ".txt")):
        output_file_name += ".md"

    # Load data
    data = load_json_data(json_file_path)
    
    # If data loaded successfully, generate the report
    if data:
        generate_report_from_data(data, user_report_prompt, output_file_name)