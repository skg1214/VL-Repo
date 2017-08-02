package mainsources;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.exec.CommandLine;

public class TestDmos {

	public static void main1(String[] args) throws Exception{

		TestDem team1=new  TestDem();
		Thread t = new Thread(team1);
		Thread t1=  new Thread(new  WaitState());
		t.start();
		t1.start();
		t.join();
		t1.join();
		team1.getP().destroy();
	}
}

class TestDem implements Runnable {
	private Process p;

	public Process getP() {
		return p;
	}

	public void setP(Process p) {
		this.p = p;
	}

	public void run() {

		try{
			//			p = Runtime.getRuntime().exec("/usr/bin/open -a Terminal "+System.getProperty("user.dir"));
			//			Thread.sleep(5000);
			//			String[] command = {"appium"};
			//			p=Runtime.getRuntime().exec(command);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}


	}
}

class WaitState implements  Runnable {
	public void run() {
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}