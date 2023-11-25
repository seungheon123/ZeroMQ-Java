package zmq.zmq.PullPushV2;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class Client {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.subscribe("".getBytes(ZMQ.CHARSET));
            subscriber.connect("tcp://localhost:5557");
            ZMQ.Socket publisher = context.createSocket(SocketType.PUSH);
            publisher.connect("tcp://localhost:5558");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(subscriber, ZMQ.Poller.POLLIN);

            String clientID = "client1";
            Random rand = new Random();

            while(true){
                if(poller.poll(100)==1){
                    String message = subscriber.recvStr();
                    System.out.println(clientID+": receive status =>"+message);
                }else{
                    int num = rand.nextInt(100)+1;
                    if(num<10){
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        String message = "("+clientID+")";
                        publisher.send(message.getBytes(ZMQ.CHARSET));
                        System.out.println(clientID + ": send status - activated");
                    }else{
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String msg = "(" + clientID + ":OFF)";
                        publisher.send(msg.getBytes(ZMQ.CHARSET));
                        System.out.println(clientID + ": send status - deactivated");
                    }
                }
            }
        }
    }
}
