package pa3;

import pa3.DiningPhilosophers;

/**
 * Class Monitor To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
	/**
	 * create the class Chopstick
	 */
	class Chopstick{
		//attributes
		boolean picked;
		int whoPicked;
		
		//constructor
		public Chopstick(){
			picked = false;
			whoPicked = 0;
		}
		
		//the chopstick has been picked up by philosopher with ID piTID.
		public void pickUpOne(final int piTID) {
			picked = true;
			whoPicked = piTID;
			
		}
		public void putDownOne() {
			picked = false;
		}
		public boolean pickedByMe(final int piTID) {
			return whoPicked == piTID;
		}
		public boolean pickedByOther(final int piTID) {
			return whoPicked != piTID && picked == true;
		}
	}
	/*
	 * ------------ Data members ------------
	 */
	enum Status {
		EATING, HUNGRY, THINKING, TALKING, DEAD
	};

	Status[] state;
	int piNumberOfPhilosophers;
	Chopstick[] chops;
	boolean talking;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {

		// TODO: set appropriate number of chopsticks based on the # of philosophers
		this.piNumberOfPhilosophers = piNumberOfPhilosophers;
		chops = new Chopstick[piNumberOfPhilosophers];
		state = new Status[piNumberOfPhilosophers+1];
		
		for(int i = 0; i < chops.length; i++) {
			chops[i] = new Chopstick();
		}
		for(int i = 0; i <= piNumberOfPhilosophers; i++) {
			state[i] = Status.THINKING;
		}

	}

	/*
	 * ------------------------------- User-defined monitor procedures
	 * -------------------------------
	 */
	public void setState(final int piTID, Status s) {
		state[piTID] = s;
	}
	public Status getState(final int piTID) {
		return state[piTID];
	}


	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID) {
		// methodLocker.lock();
		System.out.println("Philosopher with ID " + piTID + " trys to pick up the chopsticks! ");
		int chopsId = piTID - 1;
		
		while(true) {
			//if the left side, or the right side or both sides are not available
			if(chops[chopsId].pickedByOther(piTID) || chops[(chopsId + 1) % piNumberOfPhilosophers].pickedByOther(piTID)) {
				//the priority gives to left side
				if(!chops[chopsId].picked && !chops[chopsId].pickedByMe(piTID)) {
					chops[chopsId].pickUpOne(piTID);
				}else if(!chops[(chopsId + 1) % piNumberOfPhilosophers].picked && !chops[(chopsId + 1) % piNumberOfPhilosophers].pickedByMe(piTID)) {
					//do nothing
				}
			}else {
				//both are available to be picked up
				chops[chopsId].pickUpOne(piTID);
				chops[(chopsId + 1) % piNumberOfPhilosophers].pickUpOne(piTID);
				break;
			}
			
			try {
				wait();
			}catch(InterruptedException e) {
				
				System.err.println("Monitor.pickUp():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
				
			}			
			
		}
		
		state[piTID] = Status.EATING;
		System.out.println("Philosopher with ID " + piTID + " has the right to pick up the chopsticks! ");


	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {

		System.out.println("Philosopher with ID " + piTID + " put down the chopsticks! ");
		
		//put down both sides
		//int chopsId = piTID - 1;
		//chops[chopsId].putDownOne();
		//chops[(chopsId + 1) % piNumberOfPhilosophers].putDownOne();
		chops[piTID - 1].putDownOne();
		chops[piTID % piNumberOfPhilosophers].putDownOne();
		state[piTID] = Status.THINKING;
		
		//signal all the waiting threads
		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final int piTID) {
		// Philosopher tries to talking

		System.out.println("Philosopher with ID " + piTID + " trys to talking! ");
		
		while(talking) 
		{
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Monitor.requestTalk():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		
		//current one is now talking
		talking = true;
		System.out.println("Philosopher with ID " + piTID + " has the right to talking! ");
		state[piTID] = Status.TALKING;

		

	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final int piTID) {
		// ...
		System.out.println("Philosopher with ID " + piTID + " ends talking and gives back the talking right! ");
		talking = false;
		state[piTID] = Status.THINKING;
		notifyAll();
	}
}

// EOF
