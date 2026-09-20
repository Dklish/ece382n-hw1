import java.io.*;
import java.net.*;
import java.util.*;

// Usage: java BookServer <input-file>
public class BookServer {

    static final int TCP_PORT = 5000;
    static final int UDP_PORT = 5001;

    static Map<String, Integer> inventory = new LinkedHashMap<>();
    static Map<Integer, Loan> activeLoans = new HashMap<>();
    static int nextLoanId = 1;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument");
            System.out.println("\t(1) <input-file>: the file of inventory");
            System.exit(-1);
        }

        loadInventory(args[0]);

        new Thread(BookServer::runTcp).start();
        new Thread(BookServer::runUdp).start();
    }

    private static void runTcp() {
        try (ServerSocket server = new ServerSocket(TCP_PORT)) {
            while (true) {
                Socket client = server.accept();
                new Thread(() -> serveTcpClient(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serveTcpClient(Socket client) {
        try (Socket c = client;
             DataInputStream in = new DataInputStream(c.getInputStream());
             DataOutputStream out = new DataOutputStream(c.getOutputStream())) {
            while (true) {
                String command = in.readUTF();
                out.writeUTF(handleCommand(command));
                out.flush();
            }
        } catch (EOFException e) {
            // client closed the connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runUdp() {
        try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
            while (true) {
                DatagramPacket packet = new DatagramPacket(new byte[65507], 65507);
                socket.receive(packet);
                new Thread(() -> serveUdpPacket(socket, packet)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serveUdpPacket(DatagramSocket socket, DatagramPacket packet) {
        try {
            String command = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
            byte[] reply = handleCommand(command).getBytes("UTF-8");
            socket.send(new DatagramPacket(reply, reply.length, packet.getAddress(), packet.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadInventory(String inputFile) {
        try (BufferedReader in = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int end = line.lastIndexOf('"');
                String name = line.substring(1, end);
                int quantity = Integer.parseInt(line.substring(end + 1).trim());
                inventory.put(name, quantity);
            }
        } catch (IOException e) {
            System.out.println("ERROR: Could not read " + inputFile);
            System.exit(-1);
        }
    }

    static synchronized String handleCommand(String command) {
        String[] tokens = command.split(" ", 3);

        switch (tokens[0]) {
            case "set-mode":
                return "The communication mode is set to " + (tokens[1].equals("t") ? "TCP" : "UDP");
            case "begin-loan":
                return beginLoan(tokens[1], stripQuotes(tokens[2]));
            case "end-loan":
                return endLoan(Integer.parseInt(tokens[1]));
            case "get-loans":
                return getLoans(tokens[1]);
            case "get-inventory":
                return getInventory();
            case "exit":
                writeInventory();
                return "OK";
            default:
                return "ERROR: No such command";
        }
    }

    private static String stripQuotes(String s) {
        return s.startsWith("\"") && s.endsWith("\"") ? s.substring(1, s.length() - 1) : s;
    }

    private static String beginLoan(String user, String book) {
        Integer count = inventory.get(book);
        if (count == null) return "Request Failed - We do not have this book";
        if (count == 0) return "Request Failed - Book not available";
        inventory.put(book, count - 1);
        int id = nextLoanId++;
        activeLoans.put(id, new Loan(id, user, book));
        return "Your request has been approved, " + id + " " + user + " \"" + book + "\"";
    }

    private static String endLoan(int loanId) {
        Loan loan = activeLoans.remove(loanId);
        if (loan == null) return loanId + " not found, no such borrow record";
        inventory.merge(loan.bookName, 1, Integer::sum);
        return loanId + " is returned";
    }

    private static String getLoans(String user) {
        StringBuilder sb = new StringBuilder();
        for (Loan loan : new TreeMap<>(activeLoans).values()) {
            if (loan.userName.equals(user)) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(loan.loanId).append(" \"").append(loan.bookName).append("\"");
            }
        }
        return sb.length() == 0 ? "No record found for " + user : sb.toString();
    }

    private static String getInventory() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("\"").append(e.getKey()).append("\" ").append(e.getValue());
        }
        return sb.toString();
    }

    private static void writeInventory() {
        try (PrintWriter out = new PrintWriter(new FileWriter("inventory.txt"))) {
            for (Map.Entry<String, Integer> e : inventory.entrySet()) {
                out.println("\"" + e.getKey() + "\" " + e.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
