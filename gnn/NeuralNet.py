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
        self.w_i_j = (numpy.random.randint(1, size=(self.hiddenNodes,self.inputNodes))/100)
        self.w_j_k = (numpy.random.randint(15, size=(self.hiddenNodes,self.outputNodes))/100 - 0.05)

        self.activation_function = lambda x: scipy.special.expit(x)
        pass

    def train(self, input_list, target_value):

        input_list.append(self.bias)
        #ALWAYS MATRICIES
        o_i = numpy.array(input_list, ndmin=2).T
        target = numpy.array(target_value, ndmin=2).T

        #sum of all weights and o_i, before sigmoid
        sum_o_i = numpy.dot(self.w_i_j, o_i)
        #sum_o_i = numpy.dot(self.w_i_j, )
        #sum of all weights and input, after sigmoid
        o_j = self.activation_function(sum_o_i)

        #output value before sigmoid
        sum_o_k = numpy.dot(self.w_j_k.T, o_j)
        #output value after sigmoid
        o_k = self.activation_function(sum_o_k)

        #print(o_i)
        #print(target)
        #print(sum_o_i)
        #print("o_j: ",o_j)
        #print(sum_o_k)
        #print(o_k)

        #backpropagation
        #self.weightsOutput += self.learningRate * numpy.dot((output_errors*final_outputs*(1.0 - final_outputs)), hidden_outputs.T).T
        #self.weightsHidden += self.learningRate * numpy.dot(hidden_errors*hidden_outputs*(1.0 - hidden_outputs),inputs.T)
        #self.weightsHidden += self.learningRate * numpy.dot((output_errors*final_outputs*(1.0 - final_outputs))*(hidden_outputs*(1.0 - hidden_outputs)), inputs.T)
        output_errors = o_k - target
        e_k = numpy.dot(output_errors, (o_k * (1.0 - o_k)))
        hidden_errors = numpy.dot(e_k, self.w_j_k.T)
        e_j = numpy.dot(hidden_errors,(o_j * (1.0 - o_j.T)))
        #print(e_k)
        #print("hidden_errors: ", hidden_errors)
        #print("e_j: ", e_j)
        #self.weightsOutput += self.learningRate * numpy.dot((output_errors*o_k*(1.0 - o_k)), o_j.T).T
        self.w_j_k += -self.learningRate * numpy.dot(e_k, o_j.T).T
        self.w_i_j += -self.learningRate * (o_i * e_j).T
        #print(e_j)
        #print(o_i)
        #print((e_j * o_i).T)

    def query(self, inputs_list):
        inputs_list.append(self.bias)

        inputs = numpy.array(inputs_list, ndmin=2).T
        hidden_inputs = numpy.dot(self.w_i_j, inputs)
        hidden_outputs = self.activation_function(hidden_inputs)
        final_inputs = numpy.dot(self.w_j_k.T, hidden_outputs)
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
            n.train([valX,valY],[0.0001])


    #Visualization
    for x in range(-max,max):
        for y in range(-max,max):

            result = n.query([x/10, y/10])
            print("X ",x/10," Y ",y/10, result)
            if (result >= 0.71 and result <= 0.89):
                plt.plot([x/10], [y/10], marker='o', markersize=2, color="red")
            else:
                plt.plot([x/10], [y/10], marker='o', markersize=2, color="lightgrey")

    plt.ylabel('y')
    plt.show()
