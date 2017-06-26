package de.uulm.silab.tutorial;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.wivw.silab.sys.*;

/** Java class 'MyFirstDPU'.<br>
  *  Hello World
  * <br>
  * Created: 07.06.2017 (SILABDPUWizard).<br>
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
class MyFirstDPU extends JPU
{
	
	private JFrame jFrame;
	private JLabel speedLabel;
	
	private double maxHue = 0.33;
	
	/** The JPU's constructor.
	  * Every derived JPU class must implement a constructor
	  * that takes one long argument and pass that argument to
	  * the superclass (JPU) constructor.<br>
	  * Within the constructor, the varXXX() methods may be used
	  * to create additional SILAB variables.
	  */
	public MyFirstDPU(long peer)
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
		jFrame = new JFrame();
		speedLabel = new JLabel("0 km/h");
		speedLabel.setFont(new Font("arial", Font.BOLD, 60));
		speedLabel.setVerticalAlignment(SwingConstants.CENTER);
		speedLabel.setForeground(Color.getHSBColor(3.33F, 1, 1));
		jFrame.setSize(300, 200);
		jFrame.getContentPane().setLayout(new BorderLayout());
		jFrame.getContentPane().add(speedLabel, BorderLayout.EAST);
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
		
		return JPU_READY;
	}

	/** Called periodically while the simulation is running.<br>
	  * Task: Implement the JPU's functionality during the simulation.
	  * Each call shouldn't take more than 1 ms to complete.
	  */
	@Override public void trigger(double time, double timeError)
	{
		int speedKmh = (int) (v*3.6);
		float hue;
		
		if (speedKmh > 250) {
			hue = 0;
		} else if (speedKmh <= 0) {
			hue = 0.33F;
		} else {
			hue = map(speedKmh, 0, 250, 0.33, 0);
		}
		
		speedLabel.setText(speedKmh + " km/h");
		speedLabel.setForeground(Color.getHSBColor(hue, 1, 1));
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
		jFrame.dispose();
	}

	/** A sample input variable. The default value is set to 0. */
	@VarIn(def=0) private double v;
	/** A sample output variable. The default value is set to 0. */
	@VarOut(def=0) private double Out;
	
	
	/**
	 * maps a number 'val' from one range to another
	 * 
	 */
	float map(double val, double in_min, double in_max, double out_min, double out_max) {
	  return (float) ((val - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
	}
	
}
