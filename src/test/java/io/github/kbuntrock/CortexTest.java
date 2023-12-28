package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class CortexTest {

	@Test
	public void test() {

		final String input = "T30->16:4:0:0:5:1:0:6:0:1:7:1:1:8:0:2:9:1:2:10:2:0:11:3:0:12:2:1:13:3:1:14:2:2:15:3:2:16:-1:-1:17:-1:-1:18:-1:-1:19:-1:-1;14:0:4:10:6:9:4:0:2:0:4383:3721:0:25:2:7900:209:0:25:2:1:7672:5544:0:9:3:4169:8069:0:30:20:0:5:0:11:0:7:0:13:0:15:0:8:0:14:1:4:1:10:1:6:1:12:1:7:1:14:1:15:1:9:3:11:3:7:3:13:3:15:3:8:1:11:3947:3216:-261:-303:30:0:4:TR:0:5:BL:0:6:BR:0:7:BR:0:9:BR:0:10:TR:0:11:TL:0:12:BR:0:13:BL:0:14:BR:0:15:BR:0:16:BR:0:17:BR:0:18:BL:0:19:BR:2:4:BR:2:5:BL:2:6:BL:2:7:BL:2:9:BR:2:10:BR:2:11:BL:2:12:BR:2:13:BL:2:14:BL:2:15:BL:2:16:BL:2:17:BL:2:18:BL:2:19:BR";
		final EScanner in = new EScanner(input);
		System.out.println("");

		final Player player = new Player();
		player.in = in;
		player.run();
	}

}
