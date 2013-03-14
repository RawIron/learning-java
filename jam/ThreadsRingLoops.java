import java.util.Random;


interface RingConnector {
	public void give(String message);
	public String pick();
}


class MessageBuffer implements RingConnector {
	protected String[] messages = new String[4];
	protected int readFrom = 0;
	protected int writeTo = 0;

	public synchronized void give(String message) {
		while(writeTo - readFrom == 4) {
			try { wait(); } catch (Exception e) {}				
		}
		messages[writeTo&3] = message;
		++writeTo;
		notifyAll();
	}
	public synchronized String pick() {
		String message;
		while(writeTo - readFrom == 0) {
			try { wait(); } catch (Exception e) {}
		}
		message = messages[readFrom&3];
		++readFrom;
		notifyAll();
		return message;
	}	
}


class RingCoordinator {
	
}

class RingNode implements Runnable, RingConnector {
	private RingConnector nic;
	private RingNode neighbor;
	private boolean isStopped = false;
	private String name;
	private String id;
	private Random g;
	private int closedLoops = 0;
	
	public RingNode(String name, RingConnector nic) {
		this.nic = nic;
		this.name = name;
		this.g = new Random();
		this.id = name + g.nextInt(10);
	}
	
	public void neighborIs(RingNode node) {
		this.neighbor = node;
	}
	
	protected void serve() {
		String message = id;
		System.out.println("send " + name + " " + message);
		neighbor.give(message);
	}
	protected void forward(String message) {
		System.out.println("forward " + name + " " + message);
		neighbor.give(message);
	}
	protected void loopClosed() {
		System.out.println(name + " closed");
		++closedLoops;
		if (closedLoops > 10) {
			matchOver();
		}
	}
	protected void matchOver() {
		System.out.println(name + " done");
		neighbor.give("matchover");
		isStopped = true;
	}
	
	protected void deadlock() {
		for (int served=0; served <= 10; ++served) {
			serve();
		}		
	}
	protected void async() {
		(new Thread() {
			public void run() {
				for (int served=0; served <= 10; ++served) {
					serve();
				}
			}
		}).start();		
	}
	public void play() {
		async();
		String action;
		while(!isStopped) {
			action = pick();
			if (action.startsWith(id)) { loopClosed(); }
			else if (action.equals("matchover")) { matchOver(); }
			else { forward(action); }
		}
	}
	
	@Override
	public void run() {
		play();
		System.out.println(name + " worked " + closedLoops);
	}
	@Override
	public void give(String message) {
		nic.give(message);
	}
	@Override
	public String pick() {
		return nic.pick();
	}
}


public class ThreadsRingLoops {
	protected static final int NNODES = 2;
	protected static final RingNode[] ring = new RingNode[NNODES];
	protected static final String[] NAMES = {"Iron", "Bat"};
	
	public static void main(String[] args) {
		RingConnector nic = null;
		for (int i=0; i<NNODES; ++i) {
			nic = new MessageBuffer();
			ring[i] = new RingNode(NAMES[i], nic);
		}
		for (int i=0; i<NNODES; ++i) {
			ring[i].neighborIs(ring[(i+1)&(NNODES-1)]);
			new Thread(ring[i]).start();
		}
	}
}