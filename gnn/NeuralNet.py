import numpy
import scipy.special
import random
import matplotlib.pyplot as plt
import math

#https://stackoverflow.com/questions/32105954/how-can-i-write-a-binary-array-as-an-image-in-python

class neuralNetwork:

    def __init__(self, inputNodes, hiddenNodes, outputNodes, learningRate, bias):
        self.inputNodes = inputNodes + 1
        self.hiddenNodes = hiddenNodes
        self.outputNodes = outputNodes
        self.learningRate = learningRate
        self.bias = bias

        #matrix with dimensions hiddenNodes*inputNodes, generating random values between -0.5 and 0.5
        #self.weightsHidden = (numpy.random.rand(self.hiddenNodes, self.inputNodes)-0.5)
        #self.weightsOutput = (numpy.random.rand(self.hiddenNodes, self.outputNodes)-0.5)

        #matrix with dimension hiddenNodes*inputNodes, generating random values between -0.1 and 0.1
        """
        self.weightsHidden = numpy.matrix([[1,1,0],
                                          [1,0,1],
                                          [1,-1,0],
                                          [1,0,-1]])

        """
        self.weightsHidden = (numpy.random.randint(15, size=(self.hiddenNodes,self.inputNodes))/100 - 0.05)
        #self.weightsHidden = (numpy.ones((self.hiddenNodes,self.inputNodes)))
        self.weightsOutput = (numpy.random.randint(15, size=(self.hiddenNodes,self.outputNodes))/100 - 0.05)


        self.activation_function = lambda x: scipy.special.expit(x)
        pass

    def train(self, input_list, target_list):

        input_list.append(self.bias)
        #ALWAYS MATRICIES
        inputs = numpy.array(input_list, ndmin=2).T
        targets =  numpy.array(target_list, ndmin=2).T

        #sum of all weights and inputs, before sigmoid
        hidden_inputs = numpy.dot(self.weightsHidden,inputs)
        #sum of all weights and input, after sigmoid
        hidden_outputs = self.activation_function(hidden_inputs)

        #output value before sigmoid
        final_inputs = numpy.dot(self.weightsOutput.T,hidden_outputs)
        #output value after sigmoid
        final_outputs = self.activation_function(final_inputs)

        output_errors = targets - final_outputs
        hidden_errors = numpy.dot(self.weightsOutput, output_errors)

        #print(inputs)
        #print(targets)
        #print(hidden_inputs)
        #print(hidden_outputs)
        #print(final_inputs)
        #print(final_outputs)

        #backpropagation
        self.weightsOutput += self.learningRate * numpy.dot((output_errors*final_outputs*(1.0 - final_outputs)), hidden_outputs.T).T
        self.weightsHidden += self.learningRate * numpy.dot(hidden_errors*hidden_outputs*(1.0 - hidden_outputs),inputs.T)
        #self.weightsHidden += self.learningRate * numpy.dot((output_errors*final_outputs*(1.0 - final_outputs))*(hidden_outputs*(1.0 - hidden_outputs)), inputs.T)

    def query(self, inputs_list):
        inputs_list.append(self.bias)

        inputs = numpy.array(inputs_list, ndmin=2).T
        hidden_inputs = numpy.dot(self.weightsHidden, inputs)
        hidden_outputs = self.activation_function(hidden_inputs)
        final_inputs = numpy.dot(self.weightsOutput.T,hidden_outputs)
        final_outputs = self.activation_function(final_inputs)
        return final_outputs

if __name__ == "__main__":
    n = neuralNetwork(2,4,1,0.1,1)

    max = 15

    #training
    print("training Net ... \n")
    for i in range(55000):

        #random value between -1.5 and 1.5
        valX = random.randint(-max,max)/10
        valY = random.randint(-max,max)/10

        #Pythagoras
        valZ = (valX*valX)+(valY*valY)
        result = math.sqrt(valZ)

        if result < 1:
            n.train([valX,valY],[0.8])
        elif result >= 1:
            n.train([valX,valY],[0.1])


    #Visualization
    for x in range(-max,max):
        for y in range(-max,max):

            result = n.query([x/10, y/10])
            print("X ",x/10," Y ",y/10,result)
            if (result >= 0.71 and result <= 0.89):
                plt.plot([x/10], [y/10], marker='o', markersize=2, color="red")
            else:
                plt.plot([x/10], [y/10], marker='o', markersize=2, color="lightgrey")

    plt.ylabel('y')
    plt.show()
