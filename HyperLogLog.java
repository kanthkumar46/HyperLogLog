public class HyperLogLog {
	// represents p
	static int registerIdxSize;
	
	// represent m
	static int registerCount;
	
	
	static byte[] registers;
	static byte[] resigtersA;
	
	static final double alpha16, alpha32, alpha64;
	static double alphaM;
	
	static{
		alpha16 = 0.673;
		alpha32 = 0.697;
		alpha64 = 0.709;
	}
	
	public HyperLogLog(int IdxSize){
		registerIdxSize = IdxSize;
		registerCount = 1 << IdxSize;
		registers = new byte[registerCount];
		resigtersA = new byte[registerCount];
		alphaM = 0.7213/(1 + 1.079/registerCount);
	}
	
	public double getCardinality(double rawEstimate, byte[] registers) {
		double cardinality;
		if(rawEstimate <= 5/2 * registerCount){
			cardinality = LinearCounting(rawEstimate, registers);
		}
		else if(rawEstimate <= (1/30.0 * (1 << 32))){
			cardinality = rawEstimate;
		}
		else{
			cardinality = largeRangeCorrection(rawEstimate);
		}
		
		return cardinality;
	}
	
	private double largeRangeCorrection(double rawEstimate) {
		double pow32 = 1 << 32;
		double correctedCardinality;
		correctedCardinality = -pow32 * Math.log(1-rawEstimate/pow32);
		return correctedCardinality;
	}


	private double LinearCounting(double rawEstimate, byte [] registers) {
		double zeroRegisterCount = 0;
		double correctedCardinality;
		
		for(byte registerVal : registers){
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
