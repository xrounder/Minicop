import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GeneticProgramCreator {

    public static final int PROGRAM_COUNT = 100;
    public static final double CROSSOVER_PERCENT = 0.0;
    public static final double MUTATIONRATE = 0.2;
    public static final int PROGRAM_LENGTH = 1000;
    public static final int GENERATIONS_COUNT = 1000;

    public static void main(String[] args) {
        ArrayList<VM_neu> programs = initAll();
        int cycles = 1;
        int indexOfBestProgram;
        int fitnessOfBestProgram;
        VM_neu bestProgram;
        int lastBestFitness = 0;

        do {
            simulateAll(programs);
            ArrayList<Integer> fitness = fitnessAll(programs);
            indexOfBestProgram = getIndexOfBestProgram(fitness);
            bestProgram = programs.get(indexOfBestProgram);
            fitnessOfBestProgram = fitness(bestProgram);

            ArrayList<Double> probabilities = calculateProbabilities(fitness);
            ArrayList<VM_neu> newPrograms = new ArrayList<>();
            //selection
            selection(programs, newPrograms, probabilities, indexOfBestProgram);
            //mutation
            mutation(newPrograms);

            //print fitness and prime numbers
            if (fitnessOfBestProgram > lastBestFitness) {
                System.out.println("Generation " + cycles);
                System.out.println("Fitness: " + fitnessOfBestProgram);
                ArrayList primeNumbers = bestProgram.getPrimeNumbers();
                Collections.sort(primeNumbers);
                System.out.println(primeNumbers.toString());
                lastBestFitness = fitnessOfBestProgram;
            }

            //update
            programs = newPrograms;
            cycles++;
        } while(cycles < GENERATIONS_COUNT);

        System.out.println("------End of evolution------");
        writeToFile(bestProgram, fitnessOfBestProgram);
    }

    /**
     * select best program of the current generation
     * @param population list of programs
     * @param newPopulation new list of programs for new generation
     * @param probabilities list of probabilities for each fitness of each program
     * @param indexOfBestIndividuum index of best program that will be selected
     */
    public static void selection(ArrayList<VM_neu> population, ArrayList<VM_neu> newPopulation,
                                 ArrayList<Double> probabilities, int indexOfBestIndividuum) {
        boolean copiedBestIndividuum = false;
        //how many programs will be selected for next generation?
        int selectCount = (int) ((1-CROSSOVER_PERCENT) * PROGRAM_COUNT);
        int selectPopulationAtIndex = selectIndexOfPopulation(probabilities);
        for(int i = 0; i < selectCount; i++) {
            if(selectPopulationAtIndex == indexOfBestIndividuum) {
                copiedBestIndividuum = true;
            }
            newPopulation.add(population.get(selectPopulationAtIndex));
        }
        if(copiedBestIndividuum) {
            newPopulation.add(population.get(selectPopulationAtIndex));
        } else {
            newPopulation.add(population.get(indexOfBestIndividuum));
        }
    }

    /**
     * select index of Population by their probabilities
     * all programs above that index are selected later on
     * @param probabilities
     * @return index
     */
    public static int selectIndexOfPopulation(ArrayList<Double> probabilities) {
        int index = (int) (Math.random() * probabilities.size());
        double randNum = Math.random();
        double summe = 0;
        do {
            index++;
            index = index % probabilities.size();
            summe += probabilities.get(index);
        } while(summe < randNum);
        return index;
    }

    /**
     * calculate probabilities for all programs to be selected
     * @param fitness
     * @return list of probabilities for each fitness
     */
    public static ArrayList<Double> calculateProbabilities(ArrayList<Integer> fitness) {
        ArrayList<Double> probabilities = new ArrayList<>();
        int allFitness = calculateAllFitness(fitness);
        for(int i = 0; i < fitness.size(); i++) {
            probabilities.add(calculateProbability(fitness.get(i), allFitness));
        }
        return probabilities;
    }

    /**
     * processes the mutation for each selected program
     * @param newPrograms which contains partly normal, partly mutated programs
     */
    public static void mutation(ArrayList<VM_neu> newPrograms) {
        ArrayList<Integer> fitness = fitnessAll(newPrograms);
        int indexOfBestProgram = getIndexOfBestProgram(fitness);
        //decide how many programs should mutate for the next generation
        int selectCount = (int) (MUTATIONRATE * newPrograms.size());
        ArrayList<Integer> indices = new ArrayList<>();
        for(int i = 0; i < selectCount; i++) {
            int index = (int) (Math.random() * newPrograms.size());
            //only mutate if program has not been mutated already, otherwise mutate other program
            if(!indices.contains(index)) {
                indices.add(index);
                VM_neu mutatedProgram = mutateProgram(newPrograms.get(index));
                //check if best program will mutate
                if(index == indexOfBestProgram) {
                    int individuumFitness = fitness(newPrograms.get(indexOfBestProgram));
                    int mutatedFitness = fitness(mutatedProgram);
                    //only mutate best program if mutation is better than before
                    if(mutatedFitness > individuumFitness) {
                        newPrograms.set(index, mutatedProgram);
                    }
                } else {
                    newPrograms.set(index, mutatedProgram);
                }
            } else {
                i--;
            }
        }
    }

    /**
     * mutate the program by changing a random value at a random point
     * @param program
     * @return new program
     */
    public static VM_neu mutateProgram(VM_neu program) {
        VM_neu mutatedProgram = new VM_neu();
        mutatedProgram.mem = program.mem.clone();
        int index = (int) (Math.random() * mutatedProgram.mem.length);
        mutatedProgram.mem[index] = (int) (Math.random() * PROGRAM_LENGTH);
        return mutatedProgram;
    }

    /**
     *
     * @param fitness
     * @return
     */
    public static int calculateAllFitness(ArrayList<Integer> fitness) {
        int allFitness = 0;
        for(Integer fitnessValue : fitness) {
            allFitness += fitnessValue;
        }
        return allFitness;
    }

    /**
     * calculate probability for a program to be selected
     * @param fitness
     * @param allFitness
     * @return probability
     */
    public static double calculateProbability(int fitness, int allFitness) {
        return (fitness * 1. / allFitness);
    }

    /**
     * initiate vms with random programs
     * @return list of programs
     */
    private static ArrayList<VM_neu> initAll() {
        ArrayList<VM_neu> programs = new ArrayList<>();
        for(int i = 0; i < PROGRAM_COUNT; i++) {
            VM_neu vm = new VM_neu();
            init(vm);
            programs.add(vm);
        }
        return programs;
    }

    /**
     * initiate vm with random program
     * operators are normal integers
     * values are also normal integers
     * @param vm
     */
    private static void init(VM_neu vm) {
        int[] program = new int[PROGRAM_LENGTH];
        for (int i = 0; i < PROGRAM_LENGTH; i++) {
            program[i] = (int) (Math.random() * 8);
        }
        vm.setMemAndResizeMAX(program);
        int n = (int) (Math.random() * 100 + 1);
        for(int i = 0; i < PROGRAM_LENGTH; i++) {
            vm.stack[i] = n;
        }
    }

    /**
     * run simulation for all programs
     * @param vms
     */
    private static void simulateAll(ArrayList<VM_neu> vms) {
        for(VM_neu vm : vms) {
            resetcounters(vm);
            vm.simulate();
        }
    }

    /**
     * resets pc, reg and sp of vm
     * @param vm
     */
    private static void resetcounters(VM_neu vm) {
        vm.sp = 0;
        vm.pc = 0;
        vm.reg = 0;
        vm.primeNumbers = new ArrayList();
    }

    /**
     * calculate fitness for all programs
     * @param vms
     * @return fitness list for all the programs
     */
    private static ArrayList<Integer> fitnessAll(ArrayList<VM_neu> vms) {
        ArrayList<Integer> fitness = new ArrayList<>();
        for(VM_neu vm : vms) {
            fitness.add(fitness(vm));
        }
        return fitness;
    }

    /**
     * get fitness for one program
     * fitness is defined by the number of prime numbers written to the stack of the vm
     * @param vm
     * @return fitness
     */
    private static int fitness(VM_neu vm) {
        return vm.getPrimeNumbers().size();
    }

    /**
     * get best program of all programs by fitness
     * @param fitness
     * @return index of that program
     */
    private static int getIndexOfBestProgram(ArrayList<Integer> fitness) {
        int index = 0;
        int max = 0;
        for(int i = 0; i < fitness.size(); i++) {
            if(max < fitness.get(i)) {
                max = fitness.get(i);
                index = i;
            }
        }
        return index;
    }

    private static String getStack(VM_neu vm) {
        String s = "";
        for (int number : vm.stack) {
            s += number + " ";
        }
        return s;
    }

    /**
     * write best program to file
     * @param vm
     * @param fitness
     */
    private static void writeToFile(VM_neu vm, int fitness) {
        try {
            File file = new File("./program-" + fitness + ".txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            String program = "";
            for(int statement : vm.mem) {
                int value = statement >> 3;
                switch(statement&7) {
                    case 0:
                        program += "LOAD ";
                        break;
                    case 1:
                        program += "PUSH ";
                        break;
                    case 2:
                        program += "POP ";
                        break;
                    case 3:
                        program += "MUL ";
                        break;
                    case 4:
                        program += "DIV ";
                        break;
                    case 5:
                        program += "ADD ";
                        break;
                    case 6:
                        program += "SUB ";
                        break;
                    case 7:
                        program += "JIH ";
                        break;
                    default:
                        program += "ERROR";
                }
                program += value + System.getProperty("line.separator");
            }
            writer.write(program);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
