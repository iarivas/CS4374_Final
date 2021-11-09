package stellarium.util.math;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

public class Allocator {
	
	public static Vector3[][] createAndInitialize(int nrow, int ncol) {
		Vector3[][] vector = new Vector3[nrow][ncol];
		for(int i = 0; i < nrow; i++)
			for(int j = 0; j < ncol; j++)
				vector[i][j] = new Vector3();
		return vector;
	}
	
	public static Vector3[] createAndInitialize(int n) {
		Vector3[] vector = new Vector3[n];
		for(int i = 0; i < n; i++)
			vector[i] = new Vector3();
		return vector;
	}
	
	public static SpCoord[][] createAndInitializeSp(int nrow, int ncol) {
		SpCoord[][] coord = new SpCoord[nrow][ncol];
		for(int i = 0; i < nrow; i++)
			for(int j = 0; j < ncol; j++)
				coord[i][j] = new SpCoord();
		return coord;
	}

}
