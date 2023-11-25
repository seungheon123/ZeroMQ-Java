package zmq.zmq.PullPush;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;


public class Client {

    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5557");
            ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
            publisher.connect("tcp://localhost:5558");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            Random rand = new Random();

            while(true){
                if(poller.poll(100)==1){
                    String message = subscriber.recvStr();
                    System.out.println("I: received message " + message);
                }else {
                    int randNum = rand.nextInt(100) + 1;
                    if (randNum < 10) {
                        publisher.send(String.format("%d", randNum).getBytes(ZMQ.CHARSET));
                        System.out.println("I: sending message " + randNum);
                    }
                }
            }
        }
    }
}
