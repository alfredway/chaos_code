package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandExecutorTest {

	@Test
	public void testCmd() {
		String[] cmd = new String[]{"java"};
		assertEquals(0, CommandExecutor.executeCmd(cmd, null, null, 140));
	}

}
