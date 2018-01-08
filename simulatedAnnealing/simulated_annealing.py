import random
import copy
import math
#how many cities does the hill climber need to travel?
numberOfCities = 100
#number of columns for citymap
columns = numberOfCities
#number of rows for citymap
rows = numberOfCities
#map of cities with distances to one another
citymap = [[0 for j in range(columns)] for i in range(rows)]
#number of reasonable steps the climber should take
threshold = 200
#corresponds round trip order
currentHypothesis = [0 for i in range(rows)]
#fitness of last route; first one is terribly bad, because "If you don't even try, you already lost"
lastFitness = -math.inf
#best hypothesis that was calculated before running low on temperature
currentBestHypothesis = []
#savegame to try another route
savestate = []
#how hot shall it be?
temperature = 100
#how fast shall it cool down?
epsilon = 0.0001

#creates random first route
def createRandomHypothesis():
    currentHypothesis = random.sample(range(numberOfCities), numberOfCities)
    return currentHypothesis

#'Let the climb begin', the hill climber said, not knowing how long it will take...
def init():
    global lastFitness
    global savestate
    global currentBestHypothesis
    global temperature
    createDistanceMap()
    firstHypothesis = createRandomHypothesis()
    currentHypothesis = firstHypothesis
    currentBestHypothesis = copy.deepcopy(currentHypothesis)
    #climb, hill climber, climb! before the temperature runs too low
    while temperature > epsilon:
        #save game
        savestate = copy.deepcopy(currentHypothesis)
        #hill climber tries new route
        currentHypothesis = moveOneStepAtRandom(currentHypothesis)
        #how good is that route?
        currentFitness = fitness(currentHypothesis)

        #for conditional statement to move one step back
        randomValue = random.random()
        backStepAcceptance = math.exp((currentFitness - lastFitness) / temperature)

        #should hill climber climb?
        if currentFitness > lastFitness:
            lastFitness = currentFitness
            currentBestHypothesis = copy.deepcopy(currentHypothesis)
            print("Fitness: " + str(lastFitness) + "; Distance: " + str(getDistance(currentHypothesis)) + " Temperature: " + str(temperature))
        #move one step back?
        elif randomValue < backStepAcceptance:
            lastFitness = currentFitness
            #print("Move one step back. Fitness: " + str(lastFitness) + "; Distance: " + str(getDistance(currentHypothesis)) + " Temperature: " + str(temperature))
        #hillclimber not allowed to climb, he needs to try a different route
        else:
            currentHypothesis = copy.deepcopy(savestate)
        #temperature is cooling down
        temperature -= epsilon
    #print result
    print("Run result: current best roundtrip distance: " + str(getDistance(currentBestHypothesis)) + " and best route:")
    print(currentBestHypothesis)
    print("Initial distance: " + str(getDistance(firstHypothesis)))
    print("Optimized distance: " + str(getDistance(currentBestHypothesis)))
    return 0


#creates matrix of distances between cities
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

        #yes, also distance fromm Z to A is important
        totalDistance += citymap[hypothesis[rows-1]][hypothesis[0]]
    except IndexError:
        print("stop, hammer time!") #sorry, couldn't resist
    return totalDistance


#calculates high complex value of round trip distance
#just kidding, it only returns the negative distance of the given hypothesis
def fitness(hypothesis):
    currentDistance = getDistance(hypothesis)
    fitness = -currentDistance
    return fitness


#switch travel points in round trip randomly
def moveOneStepAtRandom(hypothesis):
    #choose random cities to swap them
    randomIndex1 = int(random.random() * numberOfCities)
    randomIndex2 = int(random.random() * numberOfCities)
    #indices have to be different, because otherwise they are not neighbours
    while randomIndex1 == randomIndex2:
        randomIndex2 = int(random.random()*numberOfCities)

    # swap
    temp = hypothesis[randomIndex1]
    hypothesis[randomIndex1] = hypothesis[randomIndex2]
    hypothesis[randomIndex2] = temp
    return hypothesis


if __name__ == '__main__':
    init()
