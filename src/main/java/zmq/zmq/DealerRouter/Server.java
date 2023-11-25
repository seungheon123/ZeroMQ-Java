package zmq.zmq.DealerRouter;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread;

import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        int numServers = 1;
        ServerTask serverTask = new ServerTask(numServers);
        serverTask.start();
        try {
            serverTask.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ServerTask extends Thread {
        private final int numServers;

        public ServerTask(int numServers) {
            this.numServers = numServers;
        }

        @Override
        public void run() {
            try (ZContext context = new ZContext()) {
                Socket frontend = context.createSocket(SocketType.ROUTER);
                frontend.bind("tcp://*:5570");

                Socket backend = context.createSocket(SocketType.DEALER);
                backend.bind("inproc://backend");

                List<ServerWorker> workers = new ArrayList<>();
                for (int i = 0; i < numServers; i++) {
                    ServerWorker worker = new ServerWorker(context, i);
                    worker.start();
                    workers.add(worker);
                }

                ZMQ.proxy(frontend, backend, null);
            }
        }
    }

    static class ServerWorker extends Thread {
        private final ZContext context;
        private final int id;

        public ServerWorker(ZContext context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        public void run() {
            try (Socket worker = context.createSocket(SocketType.DEALER)) {
                worker.connect("inproc://backend");
                System.out.println("Worker#" + id + " started");

                while (!Thread.currentThread().isInterrupted()) {
                    String message = worker.recvStr();
                    System.out.println("Worker#" + id + " received " + message);
                    worker.send(message);
                }
            }
        }
    }
}


