from flask import Flask, request
from lstm_predictor import predict_series

from server_utils import Response

app = Flask(__name__)


@app.route('/predict', methods=['POST'])
def predict():
    content_type = request.headers.get('Content-Type')
    if (content_type != 'application/json'):
        return Response.bad_request()

    json = request.json
    inputs = json.get("data")
    if inputs == None or type(inputs) != list:
        return Response.bad_request()

    res = predict_series(inputs)
    return Response.success(res)
        


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)