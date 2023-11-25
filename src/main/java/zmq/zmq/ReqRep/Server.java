package zmq.zmq.ReqRep;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {
        try(ZContext context = new ZContext()){
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:9999");
            while (!Thread.currentThread().isInterrupted()){
                byte []reply = socket.recv(0);
                System.out.println(
                        "Received request: "+new String(reply,ZMQ.CHARSET)
                );
                String response = "World";
                socket.send(response.getBytes(ZMQ.CHARSET),0);
            }
        }
    }

}
