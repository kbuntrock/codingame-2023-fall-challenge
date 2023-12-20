package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class CortexTest {

	@Test
	public void test() {

		final String input = "T44->12:4:0:0:5:1:0:6:0:1:7:1:1:8:0:2:9:1:2:10:2:0:11:3:0:12:2:1:13:3:1:14:2:2:15:3:2-48:0:7:15:9:4:6:14:7:8:0:2:0:6304:5952:0:30:2:6530:5952:0:30:2:1:5455:7088:0:4:3:5695:7088:0:14:15:1:4:1:10:1:6:1:7:1:8:1:15:1:12:3:5:3:7:3:6:3:8:3:9:3:15:3:11:3:12:0:24:0:4:TR:0:5:TL:0:6:BR:0:7:BR:0:8:BR:0:9:BL:0:10:TR:0:11:TL:0:12:BR:0:13:BL:0:14:BR:0:15:BL:2:4:TR:2:5:TL:2:6:BR:2:7:BR:2:8:BL:2:9:BL:2:10:TR:2:11:TL:2:12:BR:2:13:BL:2:14:BR:2:15:BL";
		final EScanner in = new EScanner(input);
		System.out.println("");

		final Player player = new Player();
		player.in = in;
		player.run();
	}

}
