from flask import Flask
from flask import request
import os
import requests

BACKEND_HOST = os.getenv('BACKEND_HOST', 'http://localhost:80')
SERVER_PORT = os.getenv('SERVER_PORT', 8000)

# Create Flask app
app = Flask(__name__, static_url_path='/', static_folder='static')
@app.route('/')
def index():
    return app.send_static_file("index.html")

@app.route('/greeting', methods=['GET'])
def greeting():
    response = requests.get(BACKEND_HOST + '/greeting')
    return response.content

@app.route('/images', methods=['POST'])
def image():
    response = requests.post(BACKEND_HOST + '/images', data=request.data)
    return response.content

@app.route('/validate', methods=['POST'])
def validate():
    response = requests.post(BACKEND_HOST + '/validate', data = request.data)
    return response.content

if __name__ == '__main__': 
    app.run(host="0.0.0.0", port = SERVER_PORT, debug=True)
    