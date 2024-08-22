package main;

import data_structures.ArrayList;
import interfaces.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

public class Election {
	
	private List<Candidate> candidateList = new ArrayList<>();
	private List<Ballot> ballotList = new ArrayList<>();
	private List<Ballot> validBallots = new ArrayList<>();
	private List<Ballot> blankBallots = new ArrayList<>();
	private List<Ballot> invalidBallots = new ArrayList<>();
	private List<String> elimCandidates = new ArrayList<>();
	private String winner;
	private List<List<Integer>> election;
	
	

	
	/**
	 * Constructor default que implementa toda la logica de Election usando los fies "candidates.csv" y "ballots.csv"
	 */
	public Election() {
		this("candidates.csv", "ballots.csv");
	}
	
	/**Constructor con parametros que implementa toda la logica de Election utilizando los archivos que se revicen.
	 * Los archivos deben encontrarse en el directorio inputFiles.
	 * 
	 * @param candidates_filename - nombre del archivo de candidatos a leer
	 * @param ballot_filename - nombre del archivo de ballots a leer
	 */
	public Election(String candidates_filename, String ballot_filename) {
		
		// Lee el archivo de los candidatos y crea objetos de candidatos para luego guardarlos en una lista de tipo Candidate
		try {
			BufferedReader candidateReader = new BufferedReader(new FileReader("inputFiles/" + candidates_filename));
					
			String candidateLine = candidateReader.readLine();
			while(candidateLine != null) {
				candidateList.add(new Candidate(candidateLine));
				candidateLine = candidateReader.readLine();
			}
			// Inicializa la lista 2d con el tamaño de la candidad de candidatos
			election = new ArrayList<>(candidateList.size());
					
					
			candidateReader.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		// Lee el archivo de los ballots y crea objetos de ballots junto con las ya creadas listas de candidatos para luego guardarlos en una lista tipo Ballot
		try {
			BufferedReader ballotReader = new BufferedReader(new FileReader("inputFiles/" + ballot_filename));
					
			String ballotLine = ballotReader.readLine();
			while(ballotLine != null) {
				ballotList.add(new Ballot(ballotLine, candidateList));
				ballotLine = ballotReader.readLine();
			}
			// Divide cada ballot en si respectiva lista si es: valido, invalido o en blanco
			for(int i = 0; i < ballotList.size(); i++) {
				if(ballotList.get(i).getBallotType() == 0) {
					validBallots.add(ballotList.get(i));
				} else if(ballotList.get(i).getBallotType() == 1) {
					blankBallots.add(ballotList.get(i));
				} else {
					invalidBallots.add(ballotList.get(i));
				}
			}
			ballotReader.close();
					
		} catch(IOException e){
			e.printStackTrace();
		}
				
		// Comienza la logica de Election para encontrar el ganador y obtener una lista de los eliminados
		boolean foundWinner = false;
		out: while(countActive(candidateList) > 1 || !foundWinner) {
			//se reinicia la lista por ronda
			election.clear();
			
			//se inicializan las listas de tipo Integer dentro de la lista mayor
			for(int i = 0; i < candidateList.size(); i++) {
				election.add(new ArrayList<Integer>());
			}
			
			//Si el candidato esta activo, guarda sus votos en las listas interiores y se almazeman en su respectivo index de la lista mayor
			// Ej. todos los votos de Pepe estarian en index 0 pa que Pepe es (#id - 1) index
			for(int i = 0; i < candidateList.size(); i++) {
				
				if(candidateList.get(i).isActive()) {
					for(int j = 0; j < validBallots.size(); j++) {
						
						election.get(i).add(validBallots.get(j).getRankByCandidate(i + 1));
						
					}
				}
			}
			
			//inicia la logica para contar los 1's de cada candidato en el caso que esten activos 
			//de no estarlo se ignora y se cambia al aiguiente index
			List<Integer> ones = new ArrayList<Integer>(candidateList.size());
			
			for(int i = 0; i < election.size(); i++) {
				int count_1s = 0;
				
				if(candidateList.get(i).isActive()) {
					
					for(int j = 0; j < election.get(i).size(); j++) {
						
						if(election.get(i).get(j) == 1) {
							count_1s++;
						}
					}
					ones.add(count_1s);
				} else {
					ones.add(null);
				}
			}
			
			//Revisa si algun voto es mas del 50%, de no serlo revisa cual es la menor candidad de 1's
			int min = 1000000000;
			for(int i = 0; i < candidateList.size(); i++){
				
				if(ones.get(i) != null) {
					
					if(ones.get(i) > getTotalValidBallots() * 0.50) {
						winner = candidateList.get(i).getName() + "-" + ones.get(i);
						foundWinner = true;
						break out;
					} else if(ones.get(i) < min) {
						min = ones.get(i);
					}
				}
			}
			
			//Busca que candidatos tienen la misma candiad de 1's menores y añade sus id's a la lista de elimination
			List<Integer> elimination = new ArrayList<>();
			
			for(int i = 0; i < candidateList.size(); i++) {
				
				if(ones.get(i) != null) {
					
					if(ones.get(i) == min) {
						elimination.add(candidateList.get(i).getId());
					}
				}
				
			}
			
			//Si la lista tiene mas de un candidato (empate) se implementa la logica de contar 
			//los siguientes votos hasta que se desempaten o lleguen al final de la candidad de candidatos
			int next = 2;
			while(elimination.size() > 1) {
				
				if(next > candidateList.size()) {
					break;
				}
				
				rankSums(next, elimination);
				next++;
			}
			
			//si nunca se llega a un desempate, el candidato con menor id es removido de la lista de elimination (pasa a la siguente ronda)
			if(next > candidateList.size()) {
				int minID = elimination.get(0);
				
				for(int i = 0; i < elimination.size(); i++) {
					if(minID < elimination.get(i)) {
						minID = elimination.get(i);
					}
				}
				elimination.remove(Integer.valueOf(minID));
			}
			
			//Elimina los votos de todos los ballots del candidato dentro de elimination y añade su nombre junto con los 1's que tenia al ser eliminado a una lista elimCandidates
			for(int i = 0; i < validBallots.size(); i++) {
				
				validBallots.get(i).eliminate(elimination.get(0));		
			}
			elimCandidates.add(candidateList.get(elimination.get(0) - 1).getName() + "-" + ones.get(elimination.get(0) - 1));
			
		}
		//crea los archivos del resultado de la eleccion
		ElectionResults();
		
		// primer lambda bono
		printCandidates((c)-> c.getId() + " " + c.getName() + "" + ((c.isActive()) ? "": "+"));  
		
	}
	
	/**Crea un archivo .txt con los resultados de la eleccion
	 * lo guarda en el directorio outputFiles con el formato [candidato_ganador][numero de 1's al ganar]
	 * 
	 * @author Jonael J Garcia
	 */
	public void ElectionResults() {
		
		String winnerFile = "outputFiles/" + winner.substring(0, winner.indexOf("-")).toLowerCase().replace(" ", "_") + winner.substring(winner.indexOf("-") + 1) + ".txt";
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(winnerFile));
			
			writer.write("Number of ballots: " + getTotalBallots() + "\n");
			
			writer.write("Number of blank ballots: " + getTotalBlankBallots() + "\n");
			
			writer.write("Number of invalid ballots: " + getTotalInvalidBallots() + "\n");
			
			for(int i = 0; i < elimCandidates.size(); i++) {
				String name = elimCandidates.get(i).substring(0, elimCandidates.get(i).indexOf("-"));
				String ones = elimCandidates.get(i).substring(elimCandidates.get(i).indexOf("-") + 1);
				
				writer.write("Round " + (i + 1) + ": " + name + " was eliminated with " + ones + " #1's" + "\n");
			}
			
			writer.write("Winner: " + winner.substring(0, winner.indexOf("-")) + " wins with " + winner.substring(winner.indexOf("-") + 1) + " #1's");
			
			writer.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Itera por cada candidato e imprime cada candidato, si no estan activos se le añade "+" al final del nombre
	 * 
	 * @param func - lambda function
	 */
	public void printCandidates(Function<Candidate, String> func) {
		
		for(Candidate c : candidateList) {
			String stringCand = func.apply(c);
			System.out.println(stringCand);
		}
	}
	
	/**Itera por todos los candidatos y cuenta cuandos candidatos en la lista siguen activos
	 * 
	 * @param candidatesList - lista con los candidatos
	 * @return count - numero de candidatos activos
	 * @author Jonael J Garcia
	 */
	public int countActive(List<Candidate> candidatesList) {
		int count = 0;
		
		for(int i = 0; i < candidatesList.size(); i++) {
			if(candidatesList.get(i).isActive()) {
				count++;
			}
		}
		return count;
	}
	
	/**Recibe una lista de candidatos que esten empate y el numero del rango que se quiere comparar
	 * hace la logica de la suma del dicho rango y se busca la menor cantidad de ese rango
	 * todo candidato que no tenga una suma igual a la candidad minima se remueve de la lista y se devuelve la lista actualizada
	 * 
	 * @param rank - rango que se busca comparar
	 * @param eliminationsID - lista de candidatos en empate
	 * @return eliminationsID - lista actualizada
	 */
	public List<Integer> rankSums(int rank, List<Integer> eliminationsID){
		List<Integer> sums = new ArrayList<>();
		
		for(int i = 0; i < eliminationsID.size(); i++) {
			int counter = 0;
			for(int j = 0; j < election.size(); j++) {
				
				if(j == eliminationsID.get(i) - 1) {
					
					for(int k = 0; k < election.get(j).size(); k++) {
						
						if(election.get(j).get(k) == rank) {
							counter++;
						}
					}
				}
			}
			sums.add(counter);
		}
		int min = sums.get(0);
		
		for(int i = 0; i < sums.size(); i++) {
			if(sums.get(i) < min) {
				min = sums.get(i);
			}
		}
		
		for(int j = 0; j < sums.size(); j++) {
			if(sums.get(j) != min) {
				eliminationsID.remove(j);
			}
		}
		return eliminationsID;
	}
	
	/** Getter que devuelve la lista de los candidatos
	 * 
	 * @return candidateList - lista de los candidatos
	 */
	public List<Candidate> getCandidates(){
		return candidateList;
	}
	
	/** Getter que devuelve la lista de los ballots validos
	 * 
	 * @return validBallots - Lista de votos validos
	 */
	public List<Ballot> getBallots(){
		
		return validBallots;
	}
	
	/**Getter que devuelve el nombre del ganador de la eleccion
	 * 
	 * @return winner - String
	 */
	public String getWinner() {
		
		return winner.substring(0, winner.indexOf("-"));
	}
	
	/** Getter que devuelve el tamaño de la lista del total de ballots
	 * 
	 * @return size de la lista de ballots
	 */
	public int getTotalBallots() {
		
		return ballotList.size();
	}
	
	/** Getter que devuelve el tamaño de la lista de los ballots invalidos
	 * 
	 * @return size de la lista de ballots invalidos
	 */
	public int getTotalInvalidBallots() {
		
		return invalidBallots.size();
	}
	
	/** Getter que devuelve el tamaño de la lista de los ballots en blanco
	 * 
	 * @return size de la lista de ballots en blanco
	 */
	public int getTotalBlankBallots() {
		return blankBallots.size();
	}
	
	/** Getter que devuelve el tamaño de la lista de los ballots validos
	 * 
	 * @return size de la lista de ballots validos
	 */
	public int getTotalValidBallots(){
		
		return validBallots.size();
	}
	
	/**Getter que devuelve una lista con los nombres de los candidatos eliminados con el formato [nombre][cantidad de 1's cuando se eliminaron]
	 * su index + 1 es la ronda en la que fueron eliminados
	 * 
	 * @return elimCandidates - lista de candidatos eliminados
	 */
	public List<String> getEliminatedCandidates(){
		
		return elimCandidates;
	}
	/**
	* Prints all the general information about the election as well as a 
	* table with the vote distribution.
	* Meant for helping in the debugging process.
	*/
	public void printBallotDistribution() {
	 System.out.println("Total ballots:" + getTotalBallots());
	 System.out.println("Total blank ballots:" + getTotalBlankBallots());
	 System.out.println("Total invalid ballots:" + getTotalInvalidBallots());
	 System.out.println("Total valid ballots:" + getTotalValidBallots());
	 System.out.println(getEliminatedCandidates());
	 for(Candidate c: this.getCandidates()) {
	 System.out.print(c.getName().substring(0, c.getName().indexOf(" ")) + "\t");
	 for(Ballot b: this.getBallots()) {
	 int rank = b.getRankByCandidate(c.getId());
	 String tableline = "| " + ((rank != -1) ? rank: " ") + " ";
	 System.out.print(tableline); 
	 }
	 System.out.println("|");
	 }
	}

}
