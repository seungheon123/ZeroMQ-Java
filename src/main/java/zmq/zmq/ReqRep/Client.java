package zmq.zmq.ReqRep;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    static int count = 10;
    public static void main(String[] args) {
        try(ZContext context = new ZContext()){
            System.out.println("Connecting to hello world server");
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:9999");
            while(count!=0){
                System.out.println("Sending request");
                socket.send("Hello");
                String message = socket.recvStr();
                System.out.println("Received reply: "+message);
                count--;
            }
        }
    }
}
