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
 * ִ��java�������
 * @author alfred
 *
 */
public final class CommandExecutor {
	
	/**
	 * 
	 * @param cmdArr java����������
	 * @param envArgs key=value ��ʽ�Ļ�������
	 * @param dir �����ļ���
	 * @param timeout �����޶�ʱ��
	 * @return
	 */
	public static int executeCmd(String[] cmdArr,String[] envArgs,File dir,int timeout){
		try {
			//ִ������
			final Process proc = Runtime.getRuntime().exec(cmdArr, envArgs, dir);
			StreamRedirector std = new StreamRedirector(proc.getInputStream(),System.out);
			StreamRedirector err = new StreamRedirector(proc.getErrorStream(),System.err);
			std.start();
			err.start();
			
			//��ʱ��timeoutʱ�䵽������proc
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

