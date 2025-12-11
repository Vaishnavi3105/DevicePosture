import json

def load_json_data(filepath):
    """Safely loads a JSON file from the given path."""
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