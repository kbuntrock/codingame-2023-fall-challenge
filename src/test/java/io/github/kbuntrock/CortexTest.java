package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class CortexTest {

	@Test
	public void test() {

		final String input = "T22->18:4:0:0:5:1:0:6:0:1:7:1:1:8:0:2:9:1:2:10:2:0:11:3:0:12:2:1:13:3:1:14:2:2:15:3:2:16:-1:-1:17:-1:-1:18:-1:-1:19:-1:-1:20:-1:-1:21:-1:-1;0:0:0:0:2:0:3237:9475:0:23:2:5449:2611:0:30:2:1:6488:6865:0:9:3:1035:6153:0:25:17:0:5:0:11:0:6:0:9:0:14:2:4:2:13:1:4:1:10:1:7:1:8:1:15:1:13:3:5:3:12:3:9:3:14:3:9:1442:9408:35:-197:16:4548:9807:-523:-133:17:5451:9807:0:0:28:0:4:TR:0:5:TR:0:8:BR:0:9:TL:0:12:TR:0:13:TR:0:14:TL:0:15:TR:0:16:BR:0:17:BR:0:18:TL:0:19:TR:0:20:TR:0:21:TR:2:4:BR:2:5:BL:2:8:BR:2:9:BL:2:12:BL:2:13:BR:2:14:BL:2:15:BR:2:16:BL:2:17:BR:2:18:BL:2:19:BR:2:20:BL:2:21:BR";
		final EScanner in = new EScanner(input);
		System.out.println("");

		final Player player = new Player();
		player.in = in;
		player.run();
	}

}
