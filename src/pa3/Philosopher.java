package pa3;

import pa3.Monitor.Status;
import pa3.common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread
{
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;

	/**
	 * The act of eating.
	 * - Print the fact that a given phil (their TID) has started eating.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done eating.
	 */
	public void eat()
	{
		try
		{
			//Enter the eating action	
			System.out.println("Philosopher with ID " + getTID() + " starts eating now! ");
			
			//the action time is within 1000
			long start_time = System.currentTimeMillis();
			long end_time = 0;			
			while((end_time - start_time) < TIME_TO_WASTE) {				
				Thread.yield();
				sleep((long)(Math.random() * TIME_TO_WASTE));
				//Thread.yield();	
				end_time = System.currentTimeMillis();
			}				
			//System.out.println("Philosopher with ID " + getTID() + " finish eating");

		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of thinking.
	 * - Print the fact that a given phil (their TID) has started thinking.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done thinking.
	 */
	public void think()
	{
		try
		{
			// ...
			//Enter the thinking action		
			System.out.println("Philosopher with ID " + getTID() + " starts thinking now! ");
			
			//the action time is within 1000
			long start_time = System.currentTimeMillis();
			long end_time = 0;			
			while((end_time - start_time) < TIME_TO_WASTE) {				
				Thread.yield();
				sleep((long)(Math.random() * TIME_TO_WASTE));
				//Thread.yield();	
				end_time = System.currentTimeMillis();
			}
				
			System.out.println("Philosopher with ID " + getTID() + " finish thinking");
			
			
			// ...
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.think():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of talking.
	 * - Print the fact that a given phil (their TID) has started talking.
	 * - yield
	 * - Say something brilliant at random
	 * - yield
	 * - The print that they are done talking.
	 */
	public void talk()
	{
		try
		{
			// ...
			//Enter the talking action		
			System.out.println("Philosopher with ID " + getTID() + " starts talking now! ");
			
			//the action time is within 1000
			long start_time = System.currentTimeMillis();
			long end_time = 0;			
			while((end_time - start_time) < TIME_TO_WASTE) {				
				Thread.yield();
				sleep((long)(Math.random() * TIME_TO_WASTE));
				saySomething();
				//Thread.yield();	
				end_time = System.currentTimeMillis();
			}
				
			//System.out.println("Philosopher with ID " + getTID() + " finish talking");			
			
			// ...
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}
	
	

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run()
	{
		for(int i = 0; i < DiningPhilosophers.DINING_STEPS; i++)
		{
			//System.out.println("==================================");
			System.out.println("==========The philosopher " + getTID() + " step # is " + (i+1) + "=========");
			//System.out.println("==================================");
			
			think();
			
			DiningPhilosophers.soMonitor.pickUp(getTID());

			eat();

			DiningPhilosophers.soMonitor.putDown(getTID());

			think();
						
			/*
			 * TODO:
			 * A decision is made at random whether this particular
			 * philosopher is about to say something terribly useful.
			 */
			/*if(true == false)
			{
				// Some monitor ops down here...
				talk();
				// ...
			}*/
			
			DiningPhilosophers.soMonitor.requestTalk(getTID());
			talk();
			DiningPhilosophers.soMonitor.endTalk(getTID());
			
			//think();
			System.out.println("==========The philosopher " + getTID() + " ends step # " + (i+1) + "=========");
			//System.out.println("==========The philosopher " + getTID() + "  status is " + (DiningPhilosophers.soMonitor.getState(getTID())) + " =========");
			

				
			//yield();
		}
		System.out.println("==========The philosopher " + getTID() + " ends all steps !=========");
		
		//set status is dead
		
		DiningPhilosophers.soMonitor.setState(getTID(), Status.DEAD);
		
		
		for(int i = 1; i <= DiningPhilosophers.DEFAULT_NUMBER_OF_PHILOSOPHERS; i++ ) {
			System.out.println("==========The philosopher " + i + "  status is " + (DiningPhilosophers.soMonitor.getState(i)) + " =========");
			

		}
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random.
	 * Feel free to add your own phrases.
	 */
	public void saySomething()
	{
		String[] astrPhrases =
		{
			"Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
			"You know, true is false and false is true if you think of it",
			"2 + 2 = 5 for extremely large values of 2...",
			"If thee cannot speak, thee must be silent",
			"My number is " + getTID() + ""
		};

		System.out.println
		(
			"Philosopher " + getTID() + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]
		);
	}
}

// EOF

