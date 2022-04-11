import pickle
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

from keras.models import load_model


from config import MODEL_PATH, SCALER_PATH, LOOK_BACK

regressor = load_model(MODEL_PATH)
sc = pickle.load(open(SCALER_PATH, 'rb'))

def predict_series(inputs):
    inputs = np.array(inputs)
    inputs = inputs.reshape(-1,1)
    inputs = sc.transform(inputs)
    X_test = []


    testset_size = len(inputs)
    for i in range(LOOK_BACK, testset_size):
        X_test.append(inputs[i-LOOK_BACK:i, 0])
    X_test = np.array(X_test)

    X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))
    predicted_stock_price = regressor.predict(X_test)
    predicted_stock_price = sc.inverse_transform(predicted_stock_price)

    return predicted_stock_price.tolist()

def test():
    from config import TEST_SET, VALUE_KEY
    dataset_test = pd.read_csv(TEST_SET)
    real_stock_price = dataset_test.iloc[:, 1:2].values[LOOK_BACK:]

    '''
    |--LOOK_BACK---|
    |--------------test-------------------------|
    '''

    inputs = dataset_test[VALUE_KEY].values

    inputs = inputs.reshape(-1,1)
    inputs = sc.transform(inputs)
    X_test = []


    testset_size = len(inputs)
    for i in range(LOOK_BACK, testset_size):
        X_test.append(inputs[i-LOOK_BACK:i, 0])
    X_test = np.array(X_test)

    X_test = np.reshape(X_test, (X_test.shape[0], X_test.shape[1], 1))
    predicted_stock_price = regressor.predict(X_test)
    predicted_stock_price = sc.inverse_transform(predicted_stock_price)

    plt.plot(real_stock_price, marker='o', color = 'black', label = 'Real value')
    plt.plot(predicted_stock_price, marker='o', color = 'green', label = 'Predicted value')
    plt.title('Request Prediction')
    plt.xlabel('Time')
    plt.ylabel('Request')
    plt.legend()
    plt.show()

if __name__ == "__main__":
    test()