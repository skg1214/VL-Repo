package mainsources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.testng.internal.remote.SlavePool;

import com.sun.jna.platform.unix.X11.Atom;

public class LaunchEmulator {
	
	public static String sdkPath = "/Users/phaneendra/Downloads/Android/";
	public  static String adbPath = sdkPath + "platform-tools" + File.separator + "adb";
	public static String emulatorPath = sdkPath + "tools" + File.separator + "emulator";
//	private static String emulatorPath = "/Users/phaneendra/Downloads/Android/tools" + "emulator";
	static String Emulatorport = "emulator-5554";
	
	public static void main(String args[]) throws InterruptedException{
		//LaunchEmulator le = new LaunchEmulator();
		
			LaunchEmualtorCharles("AtomEmulator");
	
//		le.LaunchEmulator("AVD_for_Nexus_5_by_Google");
//		le.isEmulatorOrDeviceRunning();
//		le.waitForEmulatorToBeReady();
		closeEmulator("emulator-5554");
	}
	
	/**--Launch Emulator--**/
	public static void LaunchEmulator(String AVD_for_Nexus_5_by_Google){
		System.out.println("Starting emulator for '" + AVD_for_Nexus_5_by_Google + "' .. ");
		String[] aCommand = new String[]{emulatorPath, "-avd", AVD_for_Nexus_5_by_Google};
		String[] command = new String[]{adbPath, "shell", "input", "keyevent 82"};
	//	String[] pincommand = new String[]{adbPath, "shell", "input", "text 0879", "&&", adbPath, "shell", "input", "keyevent 66"};
		//adb shell input keyevent 82
		//"touchscreen", "swipe", "930 880 930 380"
		try{
			Process p = new ProcessBuilder(aCommand).start();
			p.waitFor(120, TimeUnit.SECONDS);
			System.out.println("Emulator launched");
		//	Process p1 = new ProcessBuilder(command).start();
		//	System.out.println(p1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void LaunchEmualtorCharles(String AtomEmulator) {
		System.out.println("Launching emulator with charles certificate");
		String[] aCommand = new String[]{emulatorPath, "-netdelay", "none", "-netspeed", "full", "-avd", AtomEmulator, "-http-proxy", "http://127.0.0.1:8888"};
		try {
			closeEmulator("emulator-5554");
			Process p = new ProcessBuilder(aCommand).start();
			p.waitFor(45, TimeUnit.SECONDS);
			String[] command = new String[]{adbPath, "shell", "input", "keyevent", "82"};
			String[] pincommand = new String[]{adbPath, "shell", "input", "text", "0879"};
			String[] okcommand = new String[]{adbPath, "shell", "input", "keyevent", "66"};
			Process p2 = new ProcessBuilder(command).start();
			Thread.sleep(1000);
			Process p1 = new ProcessBuilder(pincommand).start();
			Thread.sleep(1000);
			Process p3 = new ProcessBuilder(okcommand).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**--Verify the list of devices--**/
	public static boolean isEmulatorOrDeviceRunning() {
		 
		 try {
		  String[] commandDevices = new String[] { adbPath, "devices" };
		  Process process = new ProcessBuilder(commandDevices).start();
		 
		  BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		 
		  String output = "";
		  String line = null;
		  while ((line = inputStream.readLine()) != null) {
		   System.out.println(line);
		   output = output + line;
		  }
		  if (!output.replace("List of devices attached", "").trim().equals("")) {
		   return true;
		  }
		 } catch (Exception e) {
		  e.printStackTrace();
		 }
		 return false;
		}
	
	/**--Verify the emulator is ready to take inputs--**/
	public static void waitForEmulatorToBeReady() {
		 try {
		  String[] commandBootComplete = new String[] { adbPath, "shell", "getprop", "dev.bootcomplete" };
		  Process process = new ProcessBuilder(commandBootComplete).start();
		  BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		 
		  // wait till the property returns '1'
		  while (!inputStream.readLine().equals("1")) {
		   process.waitFor(1, TimeUnit.SECONDS);
		   process = new ProcessBuilder(commandBootComplete).start();
		   inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		  }
		 
		  String[] commandBootAnim = new String[] { adbPath, "shell", "getprop", "init.svc.bootanim" };
		  process = new ProcessBuilder(commandBootAnim).start();
		  inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		 
		  // wait till the property returns 'stopped'
		  while (!inputStream.readLine().equals("stopped")) {
		   process.waitFor(1, TimeUnit.SECONDS);
		   process = new ProcessBuilder(commandBootAnim).start();
		   inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		  }
		 
		  System.out.println("Emulator is ready to use!");
		 } catch (Exception e) {
		  e.printStackTrace();
		 }
		}
	
	/**--Kill Emulator--**/
	public static void closeEmulator(String Emulatorport) {
		 System.out.println("Killing emulator if any exists...");
		 String[] aCommand = new String[] { adbPath, "-s", Emulatorport, "emu", "kill" };
		 try {
		  Process process = new ProcessBuilder(aCommand).start();
		  process.waitFor(1, TimeUnit.SECONDS);
		  System.out.println("Emulator closed successfully!");
		 } catch (Exception e) {
		  e.printStackTrace();
		 }
		}
}
