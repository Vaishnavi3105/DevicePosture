import os
import sys
import google.generativeai as genai

def initialize_model():
    """Initializes and returns the Gemini model."""
    try:
        api_key = os.environ.get("GOOGLE_API_KEY")
        if not api_key:
            raise ValueError("Error: GOOGLE_API_KEY environment variable not set.")
        genai.configure(api_key=api_key)
    except Exception as e:
        print(f"Error: Unable to initialize Google GenAI client.")
        print(e)
        sys.exit(1)

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
    return model

# Initialize the model
model = initialize_model()