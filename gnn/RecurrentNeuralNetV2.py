import math
import numpy

bias1 = -4
bias2 = 4
w11 = 6.0
w21 = 10.0
w12 = -10.0
w22 = 0.0
wMatrix = numpy.array([[w11, w12], [w21, w22]])
o1 = 0.770719
o2 = 0.00978897
epsilon = 0.001


def sigmoid(x):
    return 1 / (1 + math.exp(-x))


if __name__ == "__main__":

    input1 = 0
    input2 = 0

    x = 0
    scalefactor = 1.0

    copyInput1 = 1
    copyInput2 = 1
    eigenvalues = numpy.array(numpy.real(numpy.linalg.eigvals(wMatrix * scalefactor)))

    while math.fabs(copyInput1 - input1) > epsilon or math.fabs(copyInput2 - input2) > epsilon or math.fabs(
            eigenvalues[0] - 1) >= epsilon or math.fabs(
            eigenvalues[1] - 1) >= epsilon:

        input1 = sigmoid(o1 * wMatrix[0][0] + o2 * wMatrix[1][0] + bias1)
        input2 = sigmoid(o2 * wMatrix[1][1] + o1 * wMatrix[0][1] + bias2)

        while x < 100:
            copyInput1 = input1
            copyInput2 = input2

            input1 = sigmoid(copyInput1 * wMatrix[0][0] * scalefactor + copyInput2 * wMatrix[1][0] * scalefactor + bias1)
            input2 = sigmoid(copyInput2 * wMatrix[1][1] * scalefactor + copyInput1 * wMatrix[0][1] * scalefactor + bias2)

            # print(input1, input2)
            x += 1

        print(copyInput1, copyInput2)
        print(input1, input2)
        print("scalefactor: ", scalefactor)
        x = 0
        print("condition: ", math.fabs(copyInput1 - input1), math.fabs(copyInput2 - input2))
        print("eigenwert: ", eigenvalues)
        print("==============")
        eigenvalues = numpy.array(numpy.real(numpy.linalg.eigvals(wMatrix * scalefactor)))
        scalefactor -= 0.0001
    print("stop! Hammertime")
    print("Eigenwerte: ", eigenvalues)
    print("Gewichte: ", wMatrix * scalefactor)
