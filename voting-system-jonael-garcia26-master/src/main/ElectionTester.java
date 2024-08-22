package main;
public class ElectionTester {
    public static void main(String[] args) {
        // Instantiate the Election class using the default constructor
        Election election = new Election();

        // Alternatively, you can use the constructor with file names
        // Election election = new Election("candidates.csv", "ballots.csv");

        // Call methods to test various functionalities
        election.printBallotDistribution();
//        System.out.println("Winner: " + election.getWinner());
//        System.out.println("Total Ballots: " + election.getTotalBallots());
//        System.out.println("Total Invalid Ballots: " + election.getTotalInvalidBallots());
//        System.out.println("Total Blank Ballots: " + election.getTotalBlankBallots());
//        System.out.println("Total Valid Ballots: " + election.getTotalValidBallots());
//        System.out.println("Eliminated Candidates: " + election.getEliminatedCandidates());
    }
}
