package io.github.kbuntrock;

import org.junit.jupiter.api.Test;

/**
 * @author Kévin Buntrock
 */
public class CortexTest {

	@Test
	public void test() {

		final String input = "T5->16:4:0:0:5:1:0:6:0:1:7:1:1:8:0:2:9:1:2:10:2:0:11:3:0:12:2:1:13:3:1:14:2:2:15:3:2:16:-1:-1:17:-1:-1:18:-1:-1:19:-1:-1;0:0:0:0:2:0:969:3315:0:27:2:6982:3179:0:26:2:1:8086:3447:0:30:3:2808:3241:0:30:1:0:5:0:32:0:4:BR:0:5:BL:0:6:BR:0:7:BR:0:8:BR:0:9:BR:0:10:BR:0:11:BR:0:12:BR:0:13:BR:0:14:BR:0:15:BR:0:16:BR:0:17:BR:0:18:BR:0:19:BR:2:4:BR:2:5:BL:2:6:BL:2:7:BR:2:8:BL:2:9:BR:2:10:BL:2:11:BL:2:12:BL:2:13:BR:2:14:BL:2:15:BL:2:16:BL:2:17:BL:2:18:BL:2:19:BR;$P[4||null||false||2500||5000||2758||5000||6914||10000||0||0||null||null]$P[5||null||false||2500||5000||3575||3975||585||985||1||0||null||$V[585|3775]]$P[6||null||false||5000||7500||5000||7500||1231||6800||0||1||null||null]$P[7||null||false||5000||7500||5000||7500||6914||10000||1||1||null||null]$P[8||null||false||7500||10000||7500||10000||1231||2800||0||2||null||null]$P[9||null||false||7500||10000||7500||10000||6914||10000||1||2||null||null]$P[10||null||false||2500||5000||2758||5000||1231||6800||2||0||null||null]$P[11||null||false||2500||5000||2758||5000||6914||7208||3||0||null||null]$P[12||null||false||5000||7500||5000||7500||1231||6800||2||1||null||null]$P[13||null||false||5000||7500||5000||7500||6914||10000||3||1||null||null]$P[14||null||false||7500||10000||7500||10000||5714||6914||2||2||null||null]$P[15||null||false||7500||10000||7500||10000||1231||6800||3||2||null||null]";
		final EScanner in = new EScanner(input);
		System.out.println("");

		final Player player = new Player();
		player.in = in;
		player.run();
	}

}
