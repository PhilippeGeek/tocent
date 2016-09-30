package fr.insa.tc.tocent;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Server representation for Tocent.
 */
public class Server {

    private Client[] clients;
    private HashMap<String, String> files = new HashMap<>();

    public Server(int port, Client[] clients) throws TTransportException {
        this.clients = clients;
        FileServer.Processor<FileServerHandler> processor = new FileServer.Processor<>(new FileServerHandler());
        TServerTransport transport = new TServerSocket(port);
        TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor));
        server.serve();
    }

    private class FileServerHandler implements FileServer.Iface {

        @Override
        public String download(String name) throws TException {
            return files.get(name);
        }

        @Override
        public boolean upload(String name, String data) throws TException {
            files.put(name, data);
            for(Client c:clients){
                c.upload(name, data);
            }
            return true;
        }

        @Override
        public boolean rm(String name) throws TException {
            files.remove(name);
            return true;
        }

        @Override
        public List<String> ls() throws TException {
            ArrayList<String> list = new ArrayList<>();
            list.addAll(files.keySet());
            return list;
        }
    }

    public static void main(String... args){
        int port = Integer.parseInt(args[0]);
        ArrayList<Client> clients = new ArrayList<>(args.length/2);
        if(args.length > 1 && args.length%2==1) {
            int i=1;
            while(i+1<args.length){
                clients.add(new Client(args[i], Integer.parseInt(args[i+1])));
                i++;
            }
        }
        try {
            new Server(port, clients.toArray(new Client[clients.size()]));
        } catch (TTransportException e) {
            System.out.println("Impossible de démarrer le serveur");
        }
    }
}
