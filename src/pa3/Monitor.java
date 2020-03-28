package pa3;

import java.util.concurrent.locks.Condition;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	enum Status {EATING, HUNGRY, THINKING, TALKING};
	Status [] state;
	int piNumberOfPhilosophers;
	//Philosopher [] condVarChopsticks;
	//Condition[] condVarChopsticks;
	Object[] condVar;


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		this.piNumberOfPhilosophers = piNumberOfPhilosophers;
		
		//Initialize the number of chopsticks. It would be used as condVar
		//Because philosopher id starts from 1, not from 0
		condVar =  new Object[piNumberOfPhilosophers + 1];
		
		//Initialize the status of each philosopher
		state = new Status[piNumberOfPhilosophers + 1];		
		for(int i = 1; i <= piNumberOfPhilosophers; i++) {
			state[i] = Status.THINKING;
			//Initialize each object
			condVar[i] = new Object();
			//test if condVar is null
			//System.out.println(condVarChopsticks[i]);
			
		}
				
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */
	
	//add a test eating method to verify if both chopsticks are available
	public synchronized void test(int piTID) {
		
		synchronized(condVar[piTID]){

			//If both side are not eating and itself is hungry, then philosopher can pass the test and send a single to start eating
			if(state[(piTID + piNumberOfPhilosophers - 1) % piNumberOfPhilosophers] != Status.EATING 
					&& state[(piTID + 1) % piNumberOfPhilosophers] != Status.EATING
					&& state[piTID] == Status.HUNGRY
					//priority give the even philosopher to avoid staving and deadlock
					) {
				state[piTID] = Status.EATING;

				condVar[piTID].notify();
			
		}
}
	}
	
	//add a test talking method to verify if others are talking now. only one philosopher can talking	
	public synchronized void testTalking(final int piTID) {
		synchronized(condVar[piTID]) {
			
			//all the philosophers are not talking
			//use a counter to record the # who is not talking
			int numTalking = 0;
			for(int i = 1; i <= piNumberOfPhilosophers; i++) {
				//if self is talking, still count the number
				if(state[i] != Status.TALKING || (state[i] == Status.TALKING && i == piTID)) {
					numTalking++;
				}
			}
			
			if(numTalking < piNumberOfPhilosophers) {
				state[piTID] = Status.TALKING;
				condVar[piTID].notify();
				
			}


			/*for(int i = 1; i <= piNumberOfPhilosophers; i++) {
				if(state[i] == Status.TALKING) {
					try {
						condVar[i].wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}*/
		}
	}

	

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		System.out.println("Philosopher with ID " + piTID + " trys to pick up the chopsticks! ");
		synchronized(condVar[piTID]) {
			//When philosopher is hungry, need to pick up chopsticks to eat.
			state[piTID] = Status.HUNGRY;
			
			//to see if both forks are available
			test(piTID);
			
			//If pass the test, the pick up action is finished and signal from test is simply discarded. Otherwise, it will wait until
			//receive the signal to finish the action
			if(state[piTID] != Status.EATING) {
				try {
					System.out.println("Philosopher with ID " + piTID + " can't pick up the chopsticks and need to wait! ");
					condVar[piTID].wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//System.out.println("Philosopher with ID " + piTID + " picks up the chopsticks! ");
		}
						
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
		System.out.println("Philosopher with ID " + piTID + " put down the chopsticks! ");
		synchronized(condVar[piTID]) {
			state[piTID] = Status.THINKING;
			
			test((piTID + piNumberOfPhilosophers - 1) % piNumberOfPhilosophers);
			test((piTID + 1) % piNumberOfPhilosophers);
		}
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		//Philosopher tries to talking
		System.out.println("Philosopher with ID " + piTID + " trys to talking! ");
		synchronized(condVar[piTID]) {
			//test if he has the right to talking, invoking the test method
			testTalking(piTID);
			if(state[piTID] != Status.TALKING) {
				try {
					condVar[piTID].wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Philosopher with ID " + piTID + " has the right to talking! ");
			

		}

	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		// ...
		System.out.println("Philosopher with ID " + piTID + " gives back the talking right! ");
		synchronized(condVar[piTID]) {
			state[piTID] = Status.THINKING;			
		}
		
	}
}

// EOF
