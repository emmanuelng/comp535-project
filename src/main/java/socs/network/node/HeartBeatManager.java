package socs.network.node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatManager {

	private static class HeartBeat extends TimerTask {

		private Router aRouter;
		private LinkStateDatabase aLsd;
		private Socket aSocket;
		private ObjectOutputStream aOos;
		private int aPort;

		public HeartBeat(Router pRouter, int pPort, LinkStateDatabase pLsd) throws IOException {
			aRouter = pRouter;
			aLsd = pLsd;
			aPort = pPort;

			RouterDescription rd = pRouter.neighbors()[pPort];
			aSocket = new Socket(rd.getProcessIp(), rd.getProcessPort());
			aOos = new ObjectOutputStream(aSocket.getOutputStream());
		}

		@Override
		public void run() {
			try {
				aOos.writeObject(0);
			} catch (IOException e) {
				aRouter.disconnect(aPort);
				aLsd.clean();
			}
		}
	}

	private Router aRouter;
	private LinkStateDatabase aLsd;
	private HeartBeat[] aHeartBeats;
	private Timer aTimer;

	public HeartBeatManager(Router pRouter, LinkStateDatabase pLsd) {
		aRouter = pRouter;
		aLsd = pLsd;
		aHeartBeats = new HeartBeat[pRouter.neighbors().length];
		aTimer = new Timer();
	}

	public boolean watch(int pPort) {
		if (pPort >= 0 && pPort < aHeartBeats.length && aHeartBeats[pPort] == null) {
			try {
				aHeartBeats[pPort] = new HeartBeat(aRouter, pPort, aLsd);
				aTimer.scheduleAtFixedRate(aHeartBeats[pPort], 0, 3000);
				return true;
			} catch (IOException e) {
				unwatch(pPort);
			}
		}

		return false;
	}

	public void unwatch(int pPort) {
		if (pPort >= 0 && pPort < aHeartBeats.length && aHeartBeats[pPort] != null) {
			aHeartBeats[pPort].cancel();
			aHeartBeats[pPort] = null;
		}
	}

	public void stop() {
		aTimer.purge();
		aTimer.cancel();
		aHeartBeats = new HeartBeat[aRouter.neighbors().length];
	}
}
