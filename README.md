# KNN-classification
Implementation of K-nearest neighbours algorithm used for classification in data mining.  
Before running the application user should provide correct configuration settings inside *KNN.conf*.

##Algorithm steps

1. Reading source data set.
2. Splitting data into two sets: *training* and *test*.
3. Standardization step which is optional.
4. Executing *k-NN algorithm*. The number of algorithm’s loop is configured by the user (by specifying *k* range). In each loop application is calculating the distance between each test instance and all training elements (the Cartesian product). For each element in a test set, *k* closest elements are chosen. Among those *k* elements the most common class is determined to be the calculated class value.
5. All calculated values are compared with the source values and the accuracy is calculated as the percentage number of correctly forecasted elements in test dataset.
6. Points 2-6 are executed a specified number of times called *“cross validation loops”*.
7. Results are plotted as two dimensional chart.
