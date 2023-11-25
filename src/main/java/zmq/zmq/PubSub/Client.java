package zmq.zmq.PubSub;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args) {
        try(ZContext context = new ZContext()) {
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5557");
            String zipFilter = (args.length>0)?args[0]:"10001";
            subscriber.subscribe(zipFilter.getBytes(ZMQ.CHARSET));

            int total_temp = 0;
            for(int i = 0; i<20; i++){
                String[] receive = subscriber.recvStr().split(" ");
                String zipcode = receive[0];
                int temperature = Integer.parseInt(receive[1]);
                String relhumidity = receive[2];
                total_temp+=temperature;
                System.out.println("Receive temperature for zipcode '"
                        + zipFilter + "' was " + temperature + " F");
            }
            System.out.println("Average temperature for zipcode '"
                    + zipFilter + "' was " + (total_temp / 20.0) + " F");
        }
    }
}
