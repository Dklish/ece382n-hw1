import java.io.*;
import java.util.*;

// Usage: java BookServer <input-file>
public class BookServer {

    static final int TCP_PORT = 5000;
    static final int UDP_PORT = 5001;

    static Map<String, Integer> inventory = new HashMap<>();
    static Map<Integer, Loan> activeLoans = new HashMap<>();
    static int nextLoanId = 1;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument");
            System.out.println("\t(1) <input-file>: the file of inventory");
            System.exit(-1);
        }

        loadInventory(args[0]);

        // TODO: start TCP listener (accept loop, one thread per client)
        // TODO: start UDP listener
    }

    private static void loadInventory(String inputFile) {
        // TODO: parse "Book Name" quantity lines into `inventory`
    }

    static synchronized String handleCommand(String command) {
        String[] tokens = command.split(" ");

        switch (tokens[0]) {
            case "set-mode":
                return null; // TODO
            case "begin-loan":
                return null; // TODO
            case "end-loan":
                return null; // TODO
            case "get-loans":
                return null; // TODO
            case "get-inventory":
                return null; // TODO
            case "exit":
                return null; // TODO: write inventory.txt
            default:
                return "ERROR: No such command";
        }
    }

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
