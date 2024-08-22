package main;

import data_structures.ArrayList;
import interfaces.List;

public class Ballot {

	private int ballotnum;
	private List<String>votes = new ArrayList<String>();
	private List<Candidate> candidates = new ArrayList<Candidate>();
	
	
	
	/** Constructor que toma como parametro una linea de formato "id#,#:#,#:#,...,n:n"
	 * y una lista de candidatos
	 * 
	 * @param line - String
	 * @param candidates - List
	 */
	public Ballot(String line, List<Candidate> candidates) {
		String[] lineArr = line.split(",");
		this.ballotnum = Integer.parseInt(lineArr[0]);
		this.candidates = candidates;
		
		if(lineArr.length > 1) {
			for(int i = 1; i < lineArr.length; i++) {
				votes.add(lineArr[i]);
			}
		}
		
	}
	
	/**Getter que devuelve la lista de los votos
	 * 
	 * @return votes - List
	 */
	public List<String> getVotes(){
		return votes;
	}
	
	/**Getter que devuelve el numero de id del ballot
	 * 
	 * @return ballotnum - int
	 */
	public int getBallotNum() {
		return ballotnum;
		
	}
	
	/** Devuelve el rango de ese candidato, si el rango no esta disponible devuelve -1
	 * 
	 * @param candidateID - int
	 * @return rank - int
	 */
	public int getRankByCandidate(int candidateID) {
		for(int i = 0; i < this.votes.size(); i++) {
			
			if(Integer.parseInt(this.votes.get(i).substring(0, this.votes.get(i).indexOf(':'))) == candidateID) {
				
				return Integer.parseInt(this.votes.get(i).substring(this.votes.get(i).indexOf(':') + 1));
			}
		}
		
		return -1;
	}
	
	/**Devuelve el id del candidato con ese rango, si el candidato no esta disponible devuelve -1
	 * 
	 * @param rank - int
	 * @return candidateID - int
	 */
	public int getCandidateByRank(int rank) {
		for(int i = 0; i < this.votes.size(); i++) {
			
			if(Integer.parseInt(this.votes.get(i).substring(this.votes.get(i).indexOf(':') + 1)) == rank) {
				
				return Integer.parseInt(this.votes.get(i).substring(0, this.votes.get(i).indexOf(':')));
			}
		}
		
		return -1;
	}
	
	/**Elimina el candidato con el id dado
	 * 
	 * @param candidateId
	 * @return True or False
	 */
	public boolean eliminate(int candidateId) {
		for (int i = 0; i < candidates.size(); i++) {
			if(candidates.get(i).getId() == candidateId) {
				candidates.get(i).setActive(false);
				
				boolean flag = false;
				for(int j = 0; j < votes.size(); j++) {
					String v[] = votes.get(j).split(":");
					int id_remv = Integer.parseInt(v[0]);
					
					if(j == votes.size() - 1) {
						votes.remove(j);
						flag = false;
						
					} else if (id_remv == candidateId || flag){
						flag = true;
						votes.set(j, (votes.get(j + 1).substring(0, votes.get(j).indexOf(":")) + votes.get(j).substring(votes.get(j).indexOf(":"))));
					}
				}
			}
		}
		return false;
		
	}
	
	/**Devuelve un int que indica si el ballot es: 0 - valido, 1 - vacio o 2 - invalido
	 * 
	 * @return type - int
	 */
	public int getBallotType() {
		
		for(int i = 0; i < votes.size(); i++) {
			String data[] = votes.get(i).split(":");
			
			for(int j = 0; j < votes.size(); j++) {
				if(i != j) {
					if(data[0].equals(votes.get(j).substring(0, votes.get(j).indexOf(":")))) {
						return 2;
					}
				}
			}
			
			if(Integer.parseInt(data[1]) != i + 1) {
				
				return 2;
			}
			if(Integer.parseInt(data[0]) > candidates.size()) {
				return 2;
			}
		}
		
		if(votes.size() == 0) {
			return 1;
		}
		
		return 0;
		
	}
}
