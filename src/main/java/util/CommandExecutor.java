package util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 执行java命令的类
 * @author alfred
 *
 */
public final class CommandExecutor {
	
	/**
	 * 
	 * @param cmdArr java命令和其参数
	 * @param envArgs key=value 形式的环境参数
	 * @param dir 运行文件夹
	 * @param timeout 运行限定时间
	 * @return
	 */
	public static int executeCmd(String[] cmdArr,String[] envArgs,File dir,int timeout){
		try {
			//执行命令
			final Process proc = Runtime.getRuntime().exec(cmdArr, envArgs, dir);
			StreamRedirector std = new StreamRedirector(proc.getInputStream(),System.out);
			StreamRedirector err = new StreamRedirector(proc.getErrorStream(),System.err);
			std.start();
			err.start();
			
			//计时，timeout时间到，结束proc
			Timer timer = new Timer();
			if(timeout > 0){
				TimerTask killTask = new TimerTask() {
					
					@Override
					public void run() {
						proc.destroy();
						System.out.println(proc.toString() + "is canceled");
					}
				};
				timer.schedule(killTask, timeout);
			}
			
			
			int exitCode = proc.waitFor();
			if(timeout > 0){
				timer.cancel();
			}
			return exitCode;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static class StreamRedirector extends Thread{
		private InputStream input = null;
		private PrintStream output = null;
		
		public StreamRedirector(InputStream pInput, PrintStream pOutput){
			this.input = pInput;
			this.output = pOutput;
			this.setDaemon(true);
			
		}
		
		public void run(){
			BufferedReader br = new BufferedReader(new InputStreamReader(this.input));
			String s;
			try {
				while((s = br.readLine()) != null){
					output.println(s);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}
}

