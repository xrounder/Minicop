import random
import copy
import math
numberOfCities = 100
columns = numberOfCities
rows = numberOfCities
citymap = [[0 for j in range(columns)] for i in range(rows)]
#number of reasonable steps the climber should take
threshold = -900
#corresponds round trip order
currentHypothesis = [0 for i in range(rows)]
lastFitness = -math.inf
bestHypothesis = []
currentBestHypothesis = []
savestate = []

def createRandomHypothesis():
    global currentHypothesis
    currentHypothesis = random.sample(range(numberOfCities), numberOfCities)


def resetValues():
    global currentHypothesis
    global lastFitness
    global savestate
    global currentBestHypothesis

    currentHypothesis = [0 for i in range(rows)]
    lastFitness = -math.inf
    savestate = []
    currentBestHypothesis = []


def init():
    global currentHypothesis
    global lastFitness
    global savestate
    global bestHypothesis
    global currentBestHypothesis
    numberOfRuns = 0
    createDistanceMap()
    while numberOfRuns < 3:
        resetValues()
        createRandomHypothesis()
        tries = 0
        if numberOfRuns == 0:
            bestHypothesis = currentHypothesis
        print("--------------- ROUND " + str(numberOfRuns) + " ----------------")

        currentBestHypothesis = copy.deepcopy(currentHypothesis)
        while lastFitness < threshold and tries < 30000:
            savestate = copy.deepcopy(currentHypothesis)
            currentHypothesis = moveOneStepAtRandom(currentHypothesis)
            currentFitness = fitness(currentHypothesis, savestate)
            if currentFitness > lastFitness:
                lastFitness = currentFitness
                currentBestHypothesis = copy.deepcopy(currentHypothesis)
                print("Fitness: " + str(lastFitness) + "; Distance: " + str(getDistance(currentHypothesis)))
                tries = 0
                #print(currentBestHypothesis)
                #print(savestate)
            else:
                currentHypothesis = copy.deepcopy(savestate)
                tries += 1
                #print("NOT total Distance: " + str(lastFitness))

        if getDistance(currentBestHypothesis) < getDistance(bestHypothesis):
            bestHypothesis = copy.deepcopy(currentBestHypothesis)
        print("Run result: current best roundtrip distance: " + str(getDistance(currentBestHypothesis)) + " and best route:")
        print(currentBestHypothesis)
        numberOfRuns += 1

    print("Program finished with best roundtrip distance: " + str(getDistance(bestHypothesis)) + " and best route:")
    print(bestHypothesis)
    return 0


#creates matrix  of distances between travel points
def createDistanceMap():
    for i in range(rows):
        for j in range(columns):
            if i == j:
                citymap[i][j] = 0
            else:
                distance = int(random.random()*numberOfCities+1)
                citymap[i][j] = distance
                citymap[j][i] = distance


#returns the distance of round trip
def getDistance(hypothesis):
    try:
        totalDistance = 0
        for i in range(0, rows-2):
            totalDistance += citymap[hypothesis[i]][hypothesis[i+1]]
        totalDistance += citymap[hypothesis[rows-1]][hypothesis[0]]
    except IndexError:
        print("stop")
    return totalDistance


#calculates value of round trip distance
def fitness(hypothesis, savestate):

    currentDistance = getDistance(hypothesis)
    fitness = -currentDistance
    return fitness


#switch travel points in round trip randomly
def moveOneStepAtRandom(hypothesis):
    randomIndex1 = int(random.random()*numberOfCities)
    randomIndex2 = int(random.random()*numberOfCities)
    #indices have to be different, because otherwise they are not neighbours
    while randomIndex1 == randomIndex2:
        randomIndex2 = int(random.random()*numberOfCities)

    #swap
    temp = hypothesis[randomIndex1]
    hypothesis[randomIndex1] = hypothesis[randomIndex2]
    hypothesis[randomIndex2] = temp
    return hypothesis


if __name__ == '__main__':
    init()
