package fr.insa.tc.tocent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Made able to request files on the servers.
 */
class Client {

    /**
     * Current instance of the client connection.
     */
    private FileServer.Client client;

    /**
     * Create a new client for a given remote server.
     * @param server The server address or IP
     * @param port The port of the server
     */
    Client(String server, int port){
        TTransport transport = new TSocket(server, port);
        try {
            transport.open();
        } catch (TTransportException e) {
            throw new RuntimeException("Unable to connect to "+server+":"+port, e);
        }
        TProtocol protocol = new TBinaryProtocol(transport);
        this.client = new FileServer.Client(protocol);
    }

    /**
     * Upload a new file on the remote server.
     * If the file already exists, the old one would be erased
     * @return True if store was successful
     */
    public boolean upload(String name, String data) {
        try {
            return client.upload(name, data);
        } catch (TException e) {
            return false;
        }
    }

    /**
     * Get a file on the remote server.
     * @param file The name of requested file.
     * @return The requested file or null if it does not exist.
     */
    public String download(String file) {
        try {
            return client.download(file);
        } catch (TException e) {
            return null;
        }
    }

    public List<String> ls(){
        try {
            return client.ls();
        } catch (TException e) {
            return new ArrayList<>();
        }
    }

    public boolean rm(String name){
        try {
            return client.rm(name);
        } catch (TException e) {
            return false;
        }
    }

    public static void main(String... args){
        Client c=new Client(args[0], Integer.parseInt(args[1]));
        Scanner input = new Scanner(System.in);
        boolean quit = false;
        while (!quit){
            System.out.print("client $ ");
            switch (input.next()) {
                case "quit":
                    quit = true;
                    break;
                case "save":
                    c.upload(input.next(), input.next());
                    break;
                case "read":
                    System.out.println("- "+c.download(input.next()));
                    break;
                case "ls":
                    System.out.println("== Fichiers :");
                    for(String s:c.ls()){
                        System.out.println("- "+s);
                    }
                    break;
                case "rm":
                    if(c.rm(input.next())){
                        System.out.println("== Fichier supprimé");
                    } else {
                        System.out.println("== Fichier NON supprimé");
                    }
                    break;
                default:
                    System.out.println("== Commande introuvable !");
            }
        }
    }

}
