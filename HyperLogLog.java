public class HyperLogLog {
	static int registerIdxSize = 10;
	static int registerCount = 1024;
	static int[] registers = new int[registerCount];
	
	double alpha16, alpha32, alpha64, alphaM;
	
	public HyperLogLog() {
		this.alpha16 = 0.673;
		this.alpha32 = 0.697;
		this.alpha64 = 0.709;
		this.alphaM = 0.7213/(1 + 1.079/registerCount);
	}
	
	public static void initRegister(int IdxSize){
		registerIdxSize = IdxSize;
		registerCount = (int) Math.pow(2, IdxSize);
		registers = new int[registerCount];
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
