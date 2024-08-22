package main;

public class Candidate {

	private int id;
	private String name;
	private boolean active;
	
	
	/**Constructor que crea un candidato, guarda su id#, nombre y si esta activo.
	 * 
	 * @param line - tiene como formato ID#,candidate_name.
	 */
	public Candidate(String line) {
		this.id = Integer.parseInt(line.substring(0, line.indexOf(',')));
		this.name = line.substring(line.indexOf(',') + 1);
		this.active = true;
	}
	
	/**Getter que devuelve el numero del id del candidato
	 * 
	 * @return id - numero de id del candidato
	 */
	public int getId() {
		return id;
	}
	
	// Whether the candidate is still active in the election
	/** Boolean que al momento de llamarse muestra si el candidato esta activo o no
	 * 
	 * @return True or False
	 */
	public boolean isActive() {
		return this.active;
	}
	
	/** Setter que permite cambiar el estado de actividad del candidato
	 * 
	 * @param activity - True or False
	 */
	public void setActive(boolean activity) {
		this.active = activity;
	}
	
	/**Getter que devuelve el nombre del candidato
	 * 
	 * @return candidate's name
	 */
	public String getName() {
		return name;
	}
}
