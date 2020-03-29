package pa3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

/**
 * Class Monitor To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
	/*
	 * ------------ Data members ------------
	 */
	enum Status {
		EATING, HUNGRY, THINKING, TALKING, DEAD
	};

	Status[] state;
	int piNumberOfPhilosophers;
	private Lock eatingLocker;
	private Lock talkingLocker;
	private Lock methodLocker;
	private static Condition[] condVar;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		// initialize the locker
		eatingLocker = new ReentrantLock();
		talkingLocker = new ReentrantLock();
		methodLocker = new ReentrantLock();

		// TODO: set appropriate number of chopsticks based on the # of philosophers
		this.piNumberOfPhilosophers = piNumberOfPhilosophers;

		// Initialize the condition based on the number of chopsticks. Start from index
		// 1 with range piNumberOfPhilosophers
		condVar = new Condition[piNumberOfPhilosophers + 1];
		// condVar = new Philosopher[piNumberOfPhilosophers + 1];

		// Initialize the status of each philosopher
		state = new Status[piNumberOfPhilosophers + 1];
		for (int i = 0; i <= piNumberOfPhilosophers; i++) {
			state[i] = Status.THINKING;

			// Initialize condition for each thread. condition with lock
			condVar[i] = methodLocker.newCondition();

			// test if condVar is null
			// System.out.println(condVar[i]);
		}
	}

	/*
	 * ------------------------------- User-defined monitor procedures
	 * -------------------------------
	 */

	// add a test eating method to verify if both chopsticks are available
	public synchronized void test(int piTID) {
		// methodLocker.lock();
		if (piTID != 0) {
			synchronized (condVar[piTID]) {

				// If both side are not eating and itself is hungry and philosopher is hungry
				if (state[(leftIndex(piTID, piNumberOfPhilosophers)) % piNumberOfPhilosophers] != Status.EATING
						&& state[(rightIndex(piTID, piNumberOfPhilosophers)) % piNumberOfPhilosophers] != Status.EATING
						&& state[piTID] == Status.HUNGRY
						// avoid dead lock and starving issue, use the even number priority
						/*&& ((piTID % 2 != 0
								&& state[(leftIndex(piTID, piNumberOfPhilosophers))
										% piNumberOfPhilosophers] != Status.HUNGRY
								&& state[(rightIndex(piTID, piNumberOfPhilosophers))
										% piNumberOfPhilosophers] != Status.HUNGRY)
								|| piTID % 2 == 0)*/
				// priority give the even philosopher to avoid staving and deadlock
				) {
					state[piTID] = Status.EATING;
					//System.out.println("notify start!");
					condVar[piTID].notify();
					//System.out.println("notify end!");
				}
			}
		}
		// methodLocker.unlock();
	}

	// add a test talking method to verify if others are talking now. only one
	// philosopher can talking
	public synchronized void testTalking(final int piTID) {
		// use the synchronization of object, otherwise will throw the illegal monitor
		// exception while kusing the notify method

		synchronized (condVar[piTID]) {
			// methodLocker.lock();

			// all the philosophers are not talking
			// use a counter to record the # who is not talking
			int numTalking = 0;
			for (int i = 1; i <= piNumberOfPhilosophers; i++) {
				// if self is talking, still count the number
				if (state[i] == Status.TALKING && i != piTID) {
					numTalking++;
				}
			}
			 System.out.println("numTalking is "+numTalking);

			if (numTalking == 0 && state[piTID] != Status.DEAD) {
				state[piTID] = Status.TALKING;
				// System.out.println("thread " + piTID + " signal wakes up !!");
				condVar[piTID].notify();

				// System.out.println("thread " + piTID + " signal finish !!");

			}
			// methodLocker.unlock();

		}
	}

	// help method for converting the index
	public int leftIndex(final int piTID, final int piNumberOfPhilosophers) {
		if (piTID == 1) {
			// System.out.println("the pid " + piTID + "the left index is "+
			// piNumberOfPhilosophers);
			return piNumberOfPhilosophers;
		}
		// System.out.println("the pid " + piTID + "the left index is "+ ((piTID - 1) %
		// piNumberOfPhilosophers));
		return (piTID - 1) % piNumberOfPhilosophers;
	}

	public int rightIndex(int piTID, int piNumberOfPhilosophers) {
		if (piTID == piNumberOfPhilosophers) {
			// System.out.println("the pid " + piTID + "the right index is 1 ");
			return 1;
		}
		// System.out.println("the pid " + piTID + "the right index is "+ (piTID+1));
		return piTID + 1;

	}
	
	public Condition getCondVar(int piTID) {
		return condVar[piTID];
	}
	
	public Status getState(int piTID) {
		return state[piTID];
	}
	public void setState(int piTID, Status s) {
		state[piTID] = s;
	}

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public void pickUp(final int piTID) {
		// methodLocker.lock();
		System.out.println("Philosopher with ID " + piTID + " trys to pick up the chopsticks! ");
		synchronized (condVar[piTID]) {
			// When philosopher is hungry, need to pick up chopsticks to eat.
			state[piTID] = Status.HUNGRY;

			// to see if both forks are available
			test(piTID);

			// If pass the test, the pick up action is finished and signal from test is
			// simply discarded. Otherwise, it will wait until
			// receive the signal to finish the action
			if (state[piTID] != Status.EATING) {
				try {
					System.out.println(
							"Philosopher with ID " + piTID + " can't pick up the chopsticks and need to wait! ");
					// condVar[piTID].wait();
					// condVar[piTID].wait(Philosopher.TIME_TO_WASTE);
					condVar[piTID].wait(1000);
					System.out.println("The philosopher with ID " + piTID + " can not wait more and neighbour should give up eating");

					putDown((leftIndex(piTID, piNumberOfPhilosophers)));
					putDown((rightIndex(piTID, piNumberOfPhilosophers)));

					state[piTID] = Status.EATING;

					//System.out.println("can execute with notify signal");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println("Philosopher with ID " + piTID + " picks up the chopsticks! ");
			state[piTID] = Status.EATING;
		}
		// methodLocker.unlock();

	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public void putDown(final int piTID) {
		// ...
		// methodLocker.lock();

		if (state[piTID] == Status.EATING)
			System.out.println("Philosopher with ID " + piTID + " put down the chopsticks! ");

		if (piTID != 0) {
			synchronized (condVar[piTID]) {
				state[piTID] = Status.THINKING;
				
				//condVar[(leftIndex(piTID, piNumberOfPhilosophers)) % piNumberOfPhilosophers].notify();
				

				test((leftIndex(piTID, piNumberOfPhilosophers)) % piNumberOfPhilosophers);
				test((rightIndex(piTID, piNumberOfPhilosophers)) % piNumberOfPhilosophers);
			}
		}

		// methodLocker.unlock();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public void requestTalk(final int piTID) {
		// Philosopher tries to talking

		System.out.println("Philosopher with ID " + piTID + " trys to talking! ");

		// methodLocker.lock();
		synchronized (condVar[piTID]) {
			// test if he has the right to talking, invoking the test method
			testTalking(piTID);

			// methodLocker.unlock();

			if (state[piTID] != Status.TALKING) {
				System.out.println("Philosopher with ID " + piTID + " has no right to talking and need to wait! ");
				try {
					condVar[piTID].wait(1000);
					for(int i = 1; i <= piNumberOfPhilosophers; i++) {
						if(state[i] != Status.DEAD) {
							state[i] = Status.THINKING;
						}
					}
					state[piTID] = Status.TALKING;
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// condVar.wait();
			}
			System.out.println("Philosopher with ID " + piTID + " has the right to talking! ");

		}

	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public void endTalk(final int piTID) {
		// ...
		System.out.println("Philosopher with ID " + piTID + " ends talking and gives back the talking right! ");
		// methodLocker.lock();
		synchronized (condVar[piTID]) {

			state[piTID] = Status.THINKING;

			// random assign the talking right
			Random random = new Random();
			int rand = 0;
			while (true) {
				// the argument in the nextInt(int x) is excluded, for the range 1 -- argument,
				// so we have to provide
				// argument + 1
				rand = random.nextInt(piNumberOfPhilosophers + 1);
				if (rand != 0 && rand != piTID)
					break;
			}

			testTalking(rand);

			// methodLocker.unlock();
		}
	}
}

// EOF
