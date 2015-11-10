public class HyperLogLog {
	static int registerIdxSize;
	static int registerCount;
	static int[] registers;
	
	static final double alpha16, alpha32, alpha64;
	static double alphaM;
	static{
		alpha16 = 0.673;
		alpha32 = 0.697;
		alpha64 = 0.709;
	}
	
	public static void init(int IdxSize){
		registerIdxSize = IdxSize;
		registerCount = (int) Math.pow(2, IdxSize);
		registers = new int[registerCount];
		alphaM = 0.7213/(1 + 1.079/registerCount);
	}
	
	public double getCardinality(double rawEstimate) {
		double cardinality;
		if(rawEstimate <= 5/2 * registerCount){
			cardinality = LinearCounting(rawEstimate);
		}
		else if(rawEstimate <= (1/30 * Math.pow(2, 32))){
			cardinality = rawEstimate;
		}
		else{
			cardinality = largeRangeCorrection(rawEstimate);
		}
		
		return cardinality;
	}
	
	private double largeRangeCorrection(double rawEstimate) {
		double pow32 = Math.pow(2, 32);
		double correctedCardinality;
		correctedCardinality = -pow32 * Math.log(1-rawEstimate/pow32);
		return correctedCardinality;
	}


	private double LinearCounting(double rawEstimate) {
		double zeroRegisterCount = 0;
		double correctedCardinality;
		
		for(Integer registerVal : registers){
			if(registerVal == 0)
				zeroRegisterCount++;
		}
		if(zeroRegisterCount != 0){
			correctedCardinality = registerCount * 
					Math.log((registerCount/zeroRegisterCount));
		}
		else{
			correctedCardinality = rawEstimate;
		}
		
		return correctedCardinality;
	}
}
