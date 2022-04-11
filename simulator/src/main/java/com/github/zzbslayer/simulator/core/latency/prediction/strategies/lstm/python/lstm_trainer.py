import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import pickle

from sklearn.preprocessing import StandardScaler, MinMaxScaler

from config import TRAIN_SET, TEST_SET, VALUE_KEY, LOOK_BACK, MODEL_PATH, SCALER_PATH


def main():
    dataset_train = pd.read_csv(TRAIN_SET)
    training_set = dataset_train.iloc[:, 1:2].values

    
    sc = MinMaxScaler(feature_range = (0, 1))
    training_set_scaled = sc.fit_transform(training_set)


    X_train = []
    y_train = []
    trainset_size = len(training_set)
    for i in range(LOOK_BACK, trainset_size):
        X_train.append(training_set_scaled[i-LOOK_BACK:i, 0])
        y_train.append(training_set_scaled[i, 0])
    X_train, y_train = np.array(X_train), np.array(y_train)

    X_train = np.reshape(X_train, (X_train.shape[0], X_train.shape[1], 1))

    from keras.models import Sequential
    from keras.layers import Dense
    from keras.layers import LSTM
    from keras.layers import Dropout

    regressor = Sequential()

    regressor.add(LSTM(units = 50, return_sequences = True, input_shape = (X_train.shape[1], 1)))
    regressor.add(Dropout(0.2))

    regressor.add(LSTM(units = 50, return_sequences = True))
    regressor.add(Dropout(0.2))

    regressor.add(LSTM(units = 50, return_sequences = True))
    regressor.add(Dropout(0.2))

    regressor.add(LSTM(units = 50))
    regressor.add(Dropout(0.2))

    regressor.add(Dense(units = 1))

    regressor.compile(optimizer = 'adam', loss = 'mean_squared_error')

    regressor.fit(X_train, y_train, epochs = 100, batch_size = 32)

    '''
    save model
    '''
    regressor.save(MODEL_PATH)
    pickle.dump(sc, open(SCALER_PATH, 'wb'))


    dataset_test = pd.read_csv(TEST_SET)
    real_stock_price = dataset_test.iloc[:, 1:2].values

    '''
                              |--LOOK_BACK---|
    |----------train-------------------------||----test----|
    '''
    dataset_total = pd.concat((dataset_train[VALUE_KEY], dataset_test[VALUE_KEY]), axis = 0)
    inputs = dataset_total[len(dataset_train) - LOOK_BACK:].values
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

    
    plt.plot(real_stock_price, color = 'black', label = 'Real value')
    plt.plot(predicted_stock_price, color = 'green', label = 'Predicted value')
    plt.title('Request Prediction')
    plt.xlabel('Time')
    plt.ylabel('Request')
    plt.legend()
    plt.show()

if __name__ == "__main__":
    main()