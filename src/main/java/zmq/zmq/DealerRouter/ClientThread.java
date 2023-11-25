package zmq.zmq.DealerRouter;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class ClientThread {
    public static void main(String[] args) {
        String clientId = "client2";
        ClientTask clientTask = new ClientTask(clientId);
        clientTask.start();
    }

    static class ClientTask extends Thread {
        private final String clientId;
        private ZContext context;
        private Socket socket;
        private Poller poller;

        public ClientTask(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            this.context = new ZContext();
            this.socket = context.createSocket(SocketType.DEALER);
            this.socket.setIdentity(clientId.getBytes(ZMQ.CHARSET));
            this.socket.connect("tcp://localhost:5570");
            System.out.println("Client " + clientId + " started");

            this.poller = context.createPoller(1);
            this.poller.register(socket, Poller.POLLIN);

            Thread clientThread = new Thread(this::recvHandler);
            clientThread.setDaemon(true);
            clientThread.start();

            int reqs = 0;
            while (!Thread.currentThread().isInterrupted()) {
                reqs++;
                System.out.println("Req #" + reqs + " sent..");
                socket.send(String.format("request #%d", reqs));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            socket.close();
            context.close();
        }

        private void recvHandler() {
            while (!Thread.currentThread().isInterrupted()) {
                int poll = poller.poll(1000);
                if (poll == -1)
                    break; // Interrupted

                if (poller.pollin(0)) {
                    String msg = socket.recvStr();
                    System.out.println(clientId + " received: " + msg);
                }
            }
        }
    }
}

