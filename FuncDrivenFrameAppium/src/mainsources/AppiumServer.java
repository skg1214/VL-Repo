package mainsources;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import java.io.IOException;

public class AppiumServer {

	public void KillServer()  {
		try{
			Runtime.getRuntime().exec("taskkill /F /IM node.exe");
			Thread.sleep(5000);
		}catch(IOException e){
			e.printStackTrace();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	public void startServer() {

		KillServer();
		CommandLine command = new CommandLine("cmd");
		command.addArgument("/c");
		command.addArgument("C:\\PROGRA~2\\Appium\\node.exe");
		command.addArgument("C:\\PROGRA~2\\Appium\\node_modules\\appium\\bin\\appium.js");
		command.addArgument("--log-level", false);
		command.addArgument("error");
		command.addArgument("--address");
		command.addArgument("127.0.0.1");
		command.addArgument("--port");
		command.addArgument("4723");
		command.addArgument("--no-reset");
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		try{
			executor.execute(command, resultHandler);	
			Thread.sleep(25000);
			System.out.println("Appium server started.");
		} catch(IOException e){
			e.printStackTrace();
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	public void stopServer() {
		CommandLine command = new CommandLine("cmd");
		command.addArgument("/c");
		command.addArgument("taskkill");
		command.addArgument("/F");
		command.addArgument("/IM");
		command.addArgument("node.exe");
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		try{
			executor.execute(command, resultHandler);	
			Thread.sleep(25000);
			System.out.println("Appium server stopped.");
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		} 
	}
}