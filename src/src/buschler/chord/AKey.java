package buschler.chord;

import java.util.Arrays;

import de.uniba.wiai.lspi.chord.service.Key;

public class AKey implements Key {

		@Override
		public byte[] getBytes() {
			byte[] R = new byte[160];
			Arrays.fill(R, Byte.MIN_VALUE);
			R[2] = Byte.MAX_VALUE;
			return R;
		}
	
	
}
