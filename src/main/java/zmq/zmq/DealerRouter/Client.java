package zmq.zmq.DealerRouter;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

public class Client {
    public static void main(String[] args) {
        String clientId = "client1";
        ClientTask clientTask = new ClientTask(clientId);
        clientTask.start();
    }

    static class ClientTask extends Thread {
        private final String clientId;

        public ClientTask(String clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try (ZContext context = new ZContext()) {
                Socket socket = context.createSocket(SocketType.DEALER);
                socket.setIdentity(clientId.getBytes(ZMQ.CHARSET));
                socket.connect("tcp://localhost:5570");

                System.out.println("Client " + clientId + " started");
                Poller poller = context.createPoller(1);
                poller.register(socket, Poller.POLLIN);

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
}
