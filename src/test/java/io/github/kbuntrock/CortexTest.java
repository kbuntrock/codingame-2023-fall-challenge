package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class CortexTest {

	@Test
	public void test() {

		final String input = "T1->12:2:0:0:3:1:0:4:0:1:5:1:1:6:0:2:7:1:2:8:2:0:9:3:0:10:2:1:11:3:1:12:2:2:13:3:2-0:0:0:0:1:0:3333:500:0:30:1:1:6666:500:0:30:0:0:12:0:2:BR:0:3:BR:0:4:BL:0:5:BR:0:6:BL:0:7:BR:0:8:BR:0:9:BL:0:10:BL:0:11:BR:0:12:BR:0:13:BR";
		final EScanner in = new EScanner(input);
		System.out.println("");

		final Player player = new Player();
		player.in = in;
		player.run();
	}

}
