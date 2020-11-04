package aqua.blatt1.broker;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JOptionPane;

import aqua.blatt1.common.Direction;
import aqua.blatt1.common.Properties;
import aqua.blatt1.common.msgtypes.DeregisterRequest;
import aqua.blatt1.common.msgtypes.HandoffRequest;
import aqua.blatt1.common.msgtypes.RegisterRequest;
import aqua.blatt1.common.msgtypes.RegisterResponse;
import aqua.blatt2.broker.PoisonPill;
import messaging.Endpoint;
import messaging.Message;

public class Broker {

	private static final String TANK = "tank";
	private static final int POOL_SIZE = 10;
	private volatile int idIndex = 1;
	private volatile boolean stopRequested = false;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Endpoint endpoint = new Endpoint(Properties.PORT);
	private volatile ClientCollection<InetSocketAddress> clients = new ClientCollection<>();

	public void broker() {
		ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);
		new Thread(stopServerInterface()).start();
		while (!stopRequested) {
			Message actualMessage = endpoint.nonBlockingReceive();
			threadPool.execute(() -> {
				if (actualMessage.getPayload() instanceof RegisterRequest) {
					register(actualMessage.getSender());
				} else if (actualMessage.getPayload() instanceof DeregisterRequest) {
					deregister(actualMessage.getSender());
				} else if (actualMessage.getPayload() instanceof HandoffRequest) {
					handoffFish(actualMessage);
				} else if (actualMessage.getPayload() instanceof PoisonPill) {
					threadPool.shutdown();
					System.exit(0);
				} else {
					System.err.println("Message contains unknown payload!");
				}
			});
		}
		threadPool.shutdown();
	}

	private void register(InetSocketAddress client) {
		lock.writeLock().lock();
		String id = TANK + idIndex++;
		clients.add(id, client);
		lock.writeLock().unlock();
		endpoint.send(client, new RegisterResponse(id));
	}

	private void deregister(InetSocketAddress client) {
		lock.writeLock().lock();
		clients.remove(clients.indexOf(client));
		lock.writeLock().unlock();
	}

	private void handoffFish(Message message) {
		Direction direction = ((HandoffRequest) message.getPayload()).getFish().getDirection();
		InetSocketAddress adress;
		if (direction == Direction.LEFT) {
			lock.readLock().lock();
			adress = clients.getLeftNeighorOf(clients.indexOf(message.getSender()));
			lock.readLock().unlock();
		} else {
			lock.readLock().lock();
			adress = clients.getRightNeighorOf(clients.indexOf(message.getSender()));
			lock.readLock().unlock();
		}
		endpoint.send(adress, message.getPayload());
	}

	private Runnable stopServerInterface() {
		return () -> {
			JOptionPane.showMessageDialog(null, "Press OK button to stop server");
			stopRequested = true;
		};
	}

	public static void main(String[] args) {
		System.out.println("Starting Broker...");
		Broker broker = new Broker();
		broker.broker();
	}
}
