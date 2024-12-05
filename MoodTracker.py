# Install necessary libraries
!pip install flask flask-cors pandas scikit-learn numpy pyngrok

# Import necessary libraries
from flask import Flask, request, jsonify
from flask_cors import CORS
from pyngrok import ngrok
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import numpy as np

# Create Flask app
app = Flask(__name__)
CORS(app)  # Enable CORS for cross-origin requests

# Load dataset from CSV file
CSV_FILE = "/content/drive/MyDrive/mental_state_dataset.csv"  # Replace with your dataset's path

# Load and prepare the dataset
try:
    df = pd.read_csv(CSV_FILE)
    print(f"Dataset loaded successfully from {CSV_FILE}")
except Exception as e:
    print(f"Error loading dataset: {e}")
    exit()

# Prepare data
X = df[['sleep_hours', 'work_hours', 'social_interactions', 'stress_level', 'screen_time']]
y = df['mental_state']

# Encode target labels
label_mapping = {'Sad': 0, 'Neutral': 1, 'Happy': 2}
y = y.map(label_mapping)

# Train the Random Forest model
model = RandomForestClassifier(random_state=42)
model.fit(X, y)

# Reverse mapping for labels
reverse_label_mapping = {v: k for k, v in label_mapping.items()}

@app.route("/")
def home():
    return "Mental State Prediction API is running!"

@app.route("/predict", methods=["POST"])
def predict():
    data = request.json
    try:
        # Extract input values
        sleep_hours = float(data["sleep_hours"])
        work_hours = float(data["work_hours"])
        social_interactions = float(data["social_interactions"])
        stress_level = float(data["stress_level"])
        screen_time = float(data["screen_time"])

        # Prepare input data
        input_data = np.array([[sleep_hours, work_hours, social_interactions, stress_level, screen_time]])

        # Make prediction
        predicted_label = model.predict(input_data)[0]
        predicted_state = reverse_label_mapping[predicted_label]

        return jsonify({"mental_state": predicted_state})

    except KeyError as e:
        return jsonify({"error": f"Missing key: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# Start Flask app and expose it via Ngrok
if __name__ == "__main__":
    # Open an ngrok tunnel to the Flask app
    public_url = ngrok.connect(5000).public_url
    print(f"Public URL: {public_url}")

    # Run the Flask app
    app.run(port=5000)
