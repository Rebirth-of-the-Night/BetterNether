package paulevs.betternether.noise;

public class WorleyNoiseOctaved {
	private WorleyNoise noise;
	private int octaves;
	private double[] multipliers;
	private double[] negativeMultipliers;
	private int[] offsets;

	public WorleyNoiseOctaved(long seed, int octaves) {
		this.octaves = octaves;
		noise = new WorleyNoise(seed);
		multipliers = new double[octaves];
		negativeMultipliers = new double[octaves];
		offsets = new int[octaves];
		for (int i = 0; i < octaves; i++) {
			offsets[i] = 2 << i;
			negativeMultipliers[i] = 1D / (double) offsets[i];
			multipliers[i] = 1D - negativeMultipliers[i];
		}
	}

	public double GetValue(double x, double y) {
		double[] result = new double[octaves];
		result[0] = noise.GetValue(x, y);
		for (int i = 1; i < octaves; i++) {
			double noiseValue = noise.GetValue(x * offsets[i], y * offsets[i]);
			result[i] = noiseValue * negativeMultipliers[i-1] + result[i-1] * multipliers[i-1];
		}
		return result[octaves-1];
	}
}