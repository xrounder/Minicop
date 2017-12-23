import random
import copy
numberOfCities = 100
columns = numberOfCities
rows = numberOfCities
citymap = [[0 for j in range(columns)] for i in range(rows)]
#number of reasonable steps the climber should take
threshold = 200
#corresponds round trip order
currentHypothesis = [0 for i in range(rows)]
lastFitness = 0
bestHypothesis = []
currentBestHypothesis = []
savestate = []
lastHypothesisIndex = 0

def createRandomHypothesis():
    global currentHypothesis
    currentHypothesis = random.sample(range(numberOfCities), numberOfCities)


def resetValues():
    global currentHypothesis
    global lastFitness
    global savestate
    global lastHypothesisIndex
    global currentBestHypothesis

    currentHypothesis = [0 for i in range(rows)]
    lastFitness = 0
    savestate = []
    currentBestHypothesis = []
    lastHypothesisIndex = 0


def init():
    global currentHypothesis
    global lastFitness
    global savestate
    global lastHypothesisIndex
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
            currentFitness = lastFitness + fitness(currentHypothesis, savestate)
            if currentFitness > lastFitness:
                lastFitness = currentFitness
                lastHypothesisIndex += 1
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
    lastDistance = getDistance(savestate)
    currentDistance = getDistance(hypothesis)

    if currentDistance < lastDistance:
        fitness = 1
    else:
        fitness = 0
    return fitness


#switch travel points in round trip randomly
def moveOneStepAtRandom(hypothesis):
    global lastHypothesisIndex
    visitedCitiesIndices = range(lastHypothesisIndex)
    remainingCitiesIndices = range(lastHypothesisIndex, len(hypothesis) - 1)
    nextIndex = random.choice(remainingCitiesIndices)

    #swap
    temp = hypothesis[lastHypothesisIndex]
    hypothesis[lastHypothesisIndex] = hypothesis[nextIndex]
    hypothesis[nextIndex] = temp
    return hypothesis


if __name__ == '__main__':
    init()
