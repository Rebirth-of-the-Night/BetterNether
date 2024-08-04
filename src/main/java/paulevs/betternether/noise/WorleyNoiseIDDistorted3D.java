package paulevs.betternether.noise;

public class WorleyNoiseIDDistorted3D {
	private WorleyNoiseID3D idNoise;
	private WorleyNoiseOctaved noise;
	private double cachedX;
	private double cachedY;
	private double cachedZ;
	private double cachedNoiseX;
	private double cachedNoiseY;
	private double cachedNoiseZ;
	private int cachedBiome;

	public WorleyNoiseIDDistorted3D(long seed, int maxID) {
		idNoise = new WorleyNoiseID3D(seed, maxID);
		noise = new WorleyNoiseOctaved(seed, 3);
		cachedX = Double.NaN;
		cachedY = Double.NaN;
		cachedZ = Double.NaN;
		cachedNoiseX = Double.NaN;
		cachedNoiseY = Double.NaN;
		cachedNoiseZ = Double.NaN;
		cachedBiome = 0;
	}

	public int GetValue(double x, double y, double z) {
		// Check if the input coordinates have changed since the last call
		if (x != cachedX || y != cachedY || z != cachedZ) {
			// Cache the new input coordinates
			cachedX = x;
			cachedY = y;
			cachedZ = z;

			// Calculate the noise for each dimension and cache them
			cachedNoiseX = noise.GetValue(y * 0.5, -z * 0.5);
			cachedNoiseY = noise.GetValue(z * 0.5, -x * 0.5);
			cachedNoiseZ = noise.GetValue(x * 0.5, -y * 0.5);

			// Calculate the biome using the cached noise values
			double nx = x + cachedNoiseX;
			double ny = y + cachedNoiseY;
			double nz = z + cachedNoiseZ;
			cachedBiome = idNoise.getBiome(nx, ny, nz);
		}

		// Return the cached biome value
		return cachedBiome;
	}
}