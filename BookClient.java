import java.io.*;

// Usage: java BookClient <command-file> <clientId>
public class BookClient {

    static final String SERVER_HOST = "localhost";
    static final int TCP_PORT = 5000;
    static final int UDP_PORT = 5001;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments");
            System.out.println("\t(1) <command-file>: file containing one command per line");
            System.out.println("\t(2) <clientId>: used to name the output file out_<clientId>.txt");
            System.exit(-1);
        }

        String commandFile = args[0];
        String outputFile = "out_" + args[1] + ".txt";
        boolean useTcp = false;

        BufferedReader in = new BufferedReader(new FileReader(commandFile));
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        String line;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) continue;
            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "set-mode":
                    break; // TODO
                case "begin-loan":
                case "end-loan":
                case "get-loans":
                case "get-inventory":
                    break; // TODO: send over TCP/UDP, write response to `out`
                case "exit":
                    in.close();
                    out.close();
                    return;
                default:
                    System.out.println("ERROR: No such command");
            }
        }

        in.close();
        out.close();
    }
}
