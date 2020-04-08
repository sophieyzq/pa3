package pa3;
import java.util.Scanner;

/**
 * Class DiningPhilosophers
 * The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DiningPhilosophers
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	static int iPhilosophers;
	

	/**
	 * This default may be overridden from the command line
	 */
	public static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;

	/**
	 * Dining "iterations" per philosopher thread
	 * while they are socializing there
	 */
	public static final int DINING_STEPS = 10;

	/**
	 * Our shared monitor for the philosphers to consult
	 */
	public static Monitor soMonitor = null;

	/*
	 * -------
	 * Methods
	 * -------
	 */
	public static int numPhilosphers() {
		try{
		//read the input value 
		Scanner input = new Scanner(System.in);
		
		System.out.println("Please enter the number of philosophers: ");
		String str = input.nextLine();
		
		//if no parameter was entered, the program will use the default value
		if(str.equalsIgnoreCase("")) {
			System.out.println("Using the default number of philosophers (4).");
			input.close();
			return DEFAULT_NUMBER_OF_PHILOSOPHERS;
		}
			
		int numPhilosphers = Integer.parseInt(str);
		input.close();	
		
		if(numPhilosphers <= 0){	
			System.out.println(numPhilosphers + " is not a positive decimal integer.");
			//must return integer
			return DEFAULT_NUMBER_OF_PHILOSOPHERS;
		} else {
			return numPhilosphers;
		}
		
		}
		catch(NumberFormatException e){
			System.out.println("Using the default number of philosophers (4).");
			return DEFAULT_NUMBER_OF_PHILOSOPHERS;
		}
	}
	

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv)
	{
		try
		{
			/*
			 * TODO:
			 * Should be settable from the command line
			 * or the default if no arguments supplied.
			 */
			//int iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
			iPhilosophers = numPhilosphers();

			// Make the monitor aware of how many philosophers there are
			soMonitor = new Monitor(iPhilosophers);

			// Space for all the philosophers
			Philosopher aoPhilosophers[] = new Philosopher[iPhilosophers];

			// Let 'em sit down
			for(int j = 0; j < iPhilosophers; j++)
			{				
				aoPhilosophers[j] = new Philosopher();
				aoPhilosophers[j].start();
			}
			
			System.out.println
			(
				iPhilosophers +
				" philosopher(s) came in for a dinner."
			);

			// Main waits for all its children to die...
			// I mean, philosophers to finish their dinner.
			for(int j = 0; j < iPhilosophers; j++)
				aoPhilosophers[j].join();

			System.out.println("All philosophers have left. System terminates normally.");
		}
		catch(InterruptedException e)
		{
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}
	} // main()

	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	public static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
}

// EOF
