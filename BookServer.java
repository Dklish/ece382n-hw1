import java.io.*;
import java.util.*;

/**
 * ECE 382N HW1 - Q5: Online Library Book Loan Server.
 *
 * Usage: java BookServer <input-file>
 *
 * The server must accept commands from clients over BOTH TCP and UDP,
 * and must be multithreaded so it can serve multiple clients at once.
 *
 * TODO (not yet implemented):
 *   - open a TCP ServerSocket on TCP_PORT and spawn one worker thread
 *     per accepted connection
 *   - open a UDP DatagramSocket on UDP_PORT and handle incoming packets
 *     (in its own thread, since it will block on receive())
 *   - make sure inventory/loan state is updated safely when multiple
 *     threads call handleCommand() concurrently
 */
public class BookServer {

    // TODO: agree on these with BookClient (or take them as extra CLI args)
    static final int TCP_PORT = 5000;
    static final int UDP_PORT = 5001;

    // Shared state - guard access when handling requests from multiple threads
    static Map<String, Integer> inventory = new HashMap<>();
    static Map<Integer, Loan> activeLoans = new HashMap<>();
    static int nextLoanId = 1;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument");
            System.out.println("\t(1) <input-file>: the file of inventory");
            System.exit(-1);
        }

        String inputFile = args[0];
        loadInventory(inputFile);

        // TODO: start the UDP listener thread
        // TODO: start the TCP accept loop (spawn a thread per client)
    }

    /**
     * Loads the starting inventory from the input file into `inventory`.
     * Expected format, one book per line: "Book Name" quantity
     */
    private static void loadInventory(String inputFile) {
        // TODO: read the file line by line, parse out the quoted book
        // name and the quantity, and populate `inventory`
    }

    /**
     * Handles a single command line received from a client (e.g.
     * begin-loan Mike "The Letter") and returns the response text that
     * should be sent back to that client. Called from whichever
     * thread received the request, so keep it thread-safe.
     */
    static synchronized String handleCommand(String command) {
        String[] tokens = command.split(" ");

        switch (tokens[0]) {
            case "set-mode":
                // TODO: reply "The communication mode is set to TCP"/"UDP"
                return null;
            case "begin-loan":
                // TODO: parse <user-name> <book-name>, check inventory,
                // create a Loan, assign nextLoanId, update inventory
                return null;
            case "end-loan":
                // TODO: parse <loan-id>, look it up in activeLoans,
                // remove it and restock the book, or report not found
                return null;
            case "get-loans":
                // TODO: parse <user-name>, list their active loans
                // (one "<loan-id> <book-name>" per line), or report
                // "No record found for <user-name>"
                return null;
            case "get-inventory":
                // TODO: list every book as "<book-name> <quantity>",
                // one line per book, including books with quantity 0
                return null;
            case "exit":
                // TODO: write the current inventory out to inventory.txt
                // in the current directory (overwrite if it exists)
                return null;
            default:
                return "ERROR: No such command";
        }
    }

    /** Represents a single active book loan. */
    static class Loan {
        int loanId;
        String userName;
        String bookName;

        Loan(int loanId, String userName, String bookName) {
            this.loanId = loanId;
            this.userName = userName;
            this.bookName = bookName;
        }
    }
}
