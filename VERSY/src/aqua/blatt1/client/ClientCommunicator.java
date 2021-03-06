package aqua.blatt1.client;

import java.net.InetSocketAddress;

import aqua.blatt1.common.FishModel;
import aqua.blatt1.common.Properties;
import aqua.blatt1.common.msgtypes.DeregisterRequest;
import aqua.blatt1.common.msgtypes.HandoffRequest;
import aqua.blatt1.common.msgtypes.NeighborUpdate;
import aqua.blatt1.common.msgtypes.RegisterRequest;
import aqua.blatt1.common.msgtypes.RegisterResponse;
import aqua.blatt1.common.msgtypes.Token;
import messaging.Endpoint;
import messaging.Message;

public class ClientCommunicator {
	private final Endpoint endpoint;

	public ClientCommunicator() {
		endpoint = new Endpoint();
	}

	public class ClientForwarder {
		private final InetSocketAddress broker;

		private ClientForwarder() {
			this.broker = new InetSocketAddress(Properties.HOST, Properties.PORT);
		}

		public void register() {
			endpoint.send(broker, new RegisterRequest());
		}

		public void deregister(String id) {
			endpoint.send(broker, new DeregisterRequest(id));
		}

		public void handOffFish(FishModel fish, TankModel tankModel) {
			if (tankModel.hasToken()) {
				switch (fish.getDirection()) {
				case LEFT:
					endpoint.send(tankModel.leftNeighbor, new HandoffRequest(fish));
					break;
				case RIGHT:
					endpoint.send(tankModel.rightNeighbor, new HandoffRequest(fish));
					break;
				}
			} else {
				fish.reverse();
			}
		}

		public void handOffToken(TankModel tankModel) {
			endpoint.send(tankModel.leftNeighbor, Token.getInstance());
		}
	}

	public class ClientReceiver extends Thread {
		private final TankModel tankModel;

		private ClientReceiver(TankModel tankModel) {
			this.tankModel = tankModel;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				Message msg = endpoint.blockingReceive();

				if (msg.getPayload() instanceof RegisterResponse)
					tankModel.onRegistration(((RegisterResponse) msg.getPayload()).getId());

				if (msg.getPayload() instanceof HandoffRequest)
					tankModel.receiveFish(((HandoffRequest) msg.getPayload()).getFish());

				if (msg.getPayload() instanceof Token)
					tankModel.recieveToken();

				if (msg.getPayload() instanceof NeighborUpdate) {
					NeighborUpdate neighborUpdate = (NeighborUpdate) msg.getPayload();
					if (neighborUpdate.getLeftNeighbor() != null) {
						tankModel.leftNeighbor = neighborUpdate.getLeftNeighbor();
					}
					if (neighborUpdate.getRightNeighbor() != null) {
						tankModel.rightNeighbor = neighborUpdate.getRightNeighbor();
					}
				}

			}
			System.out.println("Receiver stopped.");
		}
	}

	public ClientForwarder newClientForwarder() {
		return new ClientForwarder();
	}

	public ClientReceiver newClientReceiver(TankModel tankModel) {
		return new ClientReceiver(tankModel);
	}

}
