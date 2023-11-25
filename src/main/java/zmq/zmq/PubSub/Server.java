package zmq.zmq.PubSub;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

import java.io.IOException;
import java.util.Random;


public class Server {
    public static void main(String[] args) throws IOException {
        System.out.println("Publishing updates at weather server...");
        try(ZContext context = new ZContext()){
            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:9999");
            Random rand = new Random();
            while (!Thread.currentThread().isInterrupted()){
                int zipcode = rand.nextInt(100000 - 1) + 1;
                int temperature = rand.nextInt(135 - (-80)) + (-80);
                int relhumidity = rand.nextInt(60 - 10) + 10;
                String info =   String.format("%d %d %d",zipcode,temperature,relhumidity);
                publisher.send(info.getBytes(ZMQ.CHARSET),0);
            }
        }
    }
}
