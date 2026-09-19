import java.io.*;

/**
 * ECE 382N HW1 - Q5: Online Library Book Loan Client.
 *
 * Usage: java BookClient <command-file> <clientId>
 *
 * Reads commands from <command-file> one per line, sends each to
 * BookServer using the current communication mode (UDP by default,
 * TCP after a "set-mode t" command), and appends every response from
 * the server to out_<clientId>.txt in the current directory.
 *
 * TODO (not yet implemented):
 *   - open/close a TCP socket (or reuse one) when in TCP mode
 *   - open a DatagramSocket and send/receive packets when in UDP mode
 *   - write each server response line to the output file
 */
public class BookClient {

    // TODO: must match BookServer's ports (or be passed in as extra CLI args)
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
        String clientId = args[1];
        String outputFile = "out_" + clientId + ".txt";

        boolean useTcp = false; // default mode is UDP per spec

        BufferedReader in = new BufferedReader(new FileReader(commandFile));
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        String line;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "set-mode":
                    // TODO: set useTcp based on tokens[1] ("t" or "u"),
                    // then write the server's confirmation to `out`
                    break;
                case "begin-loan":
                case "end-loan":
                case "get-loans":
                case "get-inventory":
                    // TODO: send `line` to BookServer using the current
                    // protocol (TCP socket or UDP DatagramSocket) and
                    // write the response(s) to `out`
                    break;
                case "exit":
                    // TODO: tell the server this client is done
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
