package aqua.blatt1.broker;

import java.net.InetSocketAddress;

import aqua.blatt1.common.Direction;
import aqua.blatt1.common.Properties;
import aqua.blatt1.common.msgtypes.DeregisterRequest;
import aqua.blatt1.common.msgtypes.HandoffRequest;
import aqua.blatt1.common.msgtypes.RegisterRequest;
import aqua.blatt1.common.msgtypes.RegisterResponse;
import messaging.Endpoint;
import messaging.Message;

public class Broker {

	private static final String TANK = "tank";
	private int idIndex = 1;

	private Endpoint endpoint = new Endpoint(Properties.PORT);
	private ClientCollection<InetSocketAddress> clients = new ClientCollection<>();

	public void broker() {
		while (true) {
			Message actualMessage = endpoint.blockingReceive();
			if (actualMessage.getPayload() instanceof RegisterRequest) {
				register(actualMessage.getSender());
			} else if (actualMessage.getPayload() instanceof DeregisterRequest) {
				deregister(actualMessage.getSender());
			} else if (actualMessage.getPayload() instanceof HandoffRequest) {
				handoffFish(actualMessage);
			} else {
				System.out.println("The payload is an instance of an unknown object!");
			}
		}
	}

	public void register(InetSocketAddress client) {
		String id = TANK + idIndex++;
		clients.add(id, client);
		endpoint.send(client, new RegisterResponse(id));
	}

	public void deregister(InetSocketAddress client) {
		clients.remove(clients.indexOf(client));
	}

	public void handoffFish(Message message) {
		Direction direction = ((HandoffRequest) message.getPayload()).getFish().getDirection();
		InetSocketAddress adress;
		if (direction == Direction.LEFT) {
			adress = clients.getLeftNeighorOf(clients.indexOf(message.getSender()));
		} else {
			adress = clients.getRightNeighorOf(clients.indexOf(message.getSender()));
		}
		endpoint.send(adress, message.getPayload());
	}

	public static void main(String[] args) {
		Broker broker = new Broker();
		broker.broker();
	}
}
