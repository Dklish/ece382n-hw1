import java.io.*;
import java.net.*;

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

        Socket tcpSocket = null;
        DataInputStream tcpIn = null;
        DataOutputStream tcpOut = null;
        DatagramSocket udpSocket = new DatagramSocket();
        InetAddress host = InetAddress.getByName(SERVER_HOST);

        BufferedReader in = new BufferedReader(new FileReader(commandFile));
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        String line;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) continue;
            String[] tokens = line.split(" ");

            switch (tokens[0]) {
                case "set-mode":
                    useTcp = tokens[1].equals("t");
                case "begin-loan":
                case "end-loan":
                case "get-loans":
                case "get-inventory":
                case "exit":
                    String response;
                    if (useTcp) {
                        if (tcpSocket == null) {
                            tcpSocket = new Socket(SERVER_HOST, TCP_PORT);
                            tcpIn = new DataInputStream(tcpSocket.getInputStream());
                            tcpOut = new DataOutputStream(tcpSocket.getOutputStream());
                        }
                        tcpOut.writeUTF(line);
                        tcpOut.flush();
                        response = tcpIn.readUTF();
                    } else {
                        byte[] data = line.getBytes("UTF-8");
                        udpSocket.send(new DatagramPacket(data, data.length, host, UDP_PORT));
                        DatagramPacket reply = new DatagramPacket(new byte[65507], 65507);
                        udpSocket.receive(reply);
                        response = new String(reply.getData(), 0, reply.getLength(), "UTF-8");
                    }
                    if (tokens[0].equals("exit")) {
                        in.close();
                        out.close();
                        if (tcpSocket != null) tcpSocket.close();
                        udpSocket.close();
                        return;
                    }
                    out.write(response);
                    out.newLine();
                    break;
                default:
                    System.out.println("ERROR: No such command");
            }
        }

        in.close();
        out.close();
        if (tcpSocket != null) tcpSocket.close();
        udpSocket.close();
    }
}
