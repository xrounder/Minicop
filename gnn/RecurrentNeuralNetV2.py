import math

bias1 = -4
bias2 = 4
w11 = 6
w21 = 10
w12 = -10
w22 = 0
o1 = 0.770719
o2 = 0.00978897
epsilon = 0.0001

def sigmoid(x):
    return 1/(1+math.exp(-x))


if __name__ == "__main__":

    #input1 = sigmoid(o1*w11+o2*w21+bias1)
    #input2 = sigmoid(o2*w22+o1*w12+bias2)
    #print(input1,input2)
    input1 = 0
    input2 = 0

    x = 0
    scalefactor = 0.5

    copyInput1 = 1
    copyInput2 = 1

    #input1 = sigmoid(copyInput1 * w11 * scalefactor + copyInput2 * w21 * scalefactor + bias1)
    #input2 = sigmoid(copyInput2 * w22 * scalefactor + copyInput1 * w12 * scalefactor + bias2)

    while math.fabs(copyInput1 - input1) > epsilon and math.fabs(copyInput2 - input2) > epsilon:

        input1 = sigmoid(o1 * w11 + o2 * w21 + bias1)
        input2 = sigmoid(o2 * w22 + o1 * w12 + bias2)

        while x < 5:

            copyInput1 = input1
            copyInput2 = input2
            input1 = sigmoid(copyInput1*w11*scalefactor+copyInput2*w21*scalefactor+bias1)
            input2 = sigmoid(copyInput2*w22*scalefactor+copyInput1*w12*scalefactor+bias2)

            print(input1, input2)
            x+=1

        #print(input1, input2)
        x = 0
        print("==============")

        scalefactor += 0.01