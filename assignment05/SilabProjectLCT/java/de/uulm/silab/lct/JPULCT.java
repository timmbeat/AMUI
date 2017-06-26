package de.uulm.silab.lct;

import java.util.Random;

import org.omg.CORBA.DynAnyPackage.Invalid;

import de.wivw.silab.sys.*;

/** Java class 'JPULCT'.<br>
  *  A LCT JPU for the 2nd SILAB assignment
  * <br>
  * Created: 20.06.2017 (SILABDPUWizard).<br>
  * @author Luis Beaucamp
  * <p>
  * A class that can be loaded as a DPU (using the DPUJava) into SILAB.
  * The {@link #trigger} method will be called
  * periodically while the simulation is running.<br>
  * More callbacks ({@link #prepare}, {@link #start}, {@link #stop} and {@link #release}) are available
  * that are called at respective times during the simulation's lifetime.<p>
  * Communication with other SILAB DPUs is most easily implemented by annotating
  * the fields using the "VarIn", "VarOut" and "VarIO" annotations.
  */
class JPULCT extends JPU
{
	private Random r = new Random();

	private int nextLane;
	
	/** The JPU's constructor.
	  * Every derived JPU class must implement a constructor
	  * that takes one long argument and pass that argument to
	  * the superclass (JPU) constructor.<br>
	  * Within the constructor, the varXXX() methods may be used
	  * to create additional SILAB variables.
	  */
	public JPULCT(long peer)
	{
		super(peer);
	}

	/** Called once when loading the simulation.<br>
	  * Task: Run preparations that might take a long time, such as
	  * reading configuration files, precomputing tables, etc.
	  * @return true if preparations succeeded; false otherwise.
	  */
	@Override public boolean prepare()
	{
	    return true;
	}

	/** Called periodically once the user clicks 'Launch'.<br>
	  * Task: Run preparations that have to be done whenever the
	  * simulation is re-started. The tasks must be subdivided into
	  * small steps (approx. 1 ms). The method will be called repeatedly
	  * as long as JPU_NOTREADY is returned.
	  * @param step Call counter (0 for the first call, 1 for the second, etc.)
	  * @return JPU_READY when the preparations are complete.<br>
	  *         JPU_NOTREADY when more time is needed. The method will be called again.<br>
	  *         JPU_ABORT when an error occurred. The simulation will be stopped.
	  */
	@Override public int start(int step)
	{
		// set these here as we don't need to change them on the straight road
		PosLeftY = -6;
		PosRightY = 6;
		
		nextLane = 2;
		return JPU_READY;
	}

	/** Called periodically while the simulation is running.<br>
	  * Task: Implement the JPU's functionality during the simulation.
	  * Each call shouldn't take more than 1 ms to complete.
	  */
	@Override public void trigger(double time, double timeError)
	{
		// are the signs more than 200m behind the car? (all the Pos*X should be the same)
		// (we only consider forward driving)
		if (CarPosX - PosLeftX >= 0) {
			
			// feedback
			if (LaneIndex == nextLane) {
				SILAB.logSys("------------- correct lane!");
			} else {
				SILAB.logSys("------------- wrong lane!");				
				// reset nextLane
				nextLane = LaneIndex;
			}
			
			// first hide all signs
			VisibleL = false;
			VisibleC = false;
			VisibleR = false;
			
			// update the x positions of the signs (y positions stay the same)
			PosLeftX += 200;
			PosRightX += 200;
			
			// which lane are we on?
			switch(LaneIndex) {
				case 0:
					// left lane -> 50/50
					if (r.nextBoolean()) {
						// display straight sign
						VisibleC = true;
					} else {
						// display right sign
						VisibleR = true;
						nextLane++;
					}
					break;
				case 1:
					double rand = r.nextDouble();
					if (rand < 0.33) {
						// display left sign
						VisibleL = true;
						nextLane--;
						
					} else if (rand < 0.66) {
						// display straight sign
						VisibleC = true;
					} else {
						// display right sign
						VisibleR = true;
						nextLane++;
					}
					break;
				case 2:
					// right lane -> 50/50
					if (r.nextBoolean()) {
						// display straight sign
						VisibleC = true;
					} else {
						// display left sign
						VisibleL = true;
						nextLane--;
					}
					break;
				default:
					SILAB.logSys("invalid Lane Index: " + LaneIndex);
						
			}
		}
		
		
	
		
	}

	/** Called periodically once the user clicks 'Stop', or when one
	  * DPU's preparations (during 'Launch') have failed.<br>
	  * Task: Cleanup things before the simulation is stopped. The tasks must be subdivided into
	  * small steps (approx. 1 ms). The method will be called repeatedly
	  * as long as JPU_NOTREADY is returned.
	  * @param step Call counter (0 for the first call, 1 for the second, etc.)
	  * @return JPU_READY when the cleanup is complete.<br>
	  *         JPU_NOTREADY when more time is needed. The method will be called again.<br>
	  *         JPU_ABORT when an error occurred. The simulation will be stopped.
	  */
	@Override public int stop(int step)
	{
		return JPU_READY;
	}

	/** Generic cleanup of the JPU.<br>
	  * The method will be called when the simulation is shut down,
	  * no matter if the shutdown is regular or using the 'emergency stop' (x)
	  * button.<br>
	  * <b>Warning:</b> release() may be called more than once.<br>
	  * <b>Warning:</b> release() may be called in any state of the simulation, because
	  * it is always possible to shut down the simulation.<br>
	  * Task: Release all resources held by the JPU.
	  */
	@Override public void release()
	{
	}

	/** The x-position input variable. The default value is set to 0. */
	@VarIn(def=0) private double CarPosX;
	
	/** Task b: We also need the current lane index (0|1|2) */
	@VarIn(def=0) private int LaneIndex;
	
	/** x-pos of left lane indicator sign */
	@VarOut(def=0) private double PosLeftX;
	
	/** y-pos of left lane indicator sign (isn't this constant?) */
	@VarOut(def=0) private double PosLeftY;
	
	/** x-pos of right lane indicator sign */
	@VarOut(def=0) private double PosRightX;
	
	/** y-pos of right lane indicator sign (constant?)*/
	@VarOut(def=0) private double PosRightY;
	
	/** lange change to the left should be performed */
	@VarOut(def=0) private boolean VisibleL;
	
	/** lange change to the right should be performed */
	@VarOut(def=0) private boolean VisibleR;
	
	/** no lange change should be performed */
	@VarOut(def=0) private boolean VisibleC;
	
	
	
}
