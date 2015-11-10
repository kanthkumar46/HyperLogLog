import java.util.HashSet;

import edu.rit.pjmr.Reducer;

public class HyperLogLogReducer extends Reducer<Integer, HyperLogLogVbl>{

	HyperLogLog hyperLogLog;
	double cardinality;
	HashSet<String> uniqueWords; 
	
	@Override
	public void start(String[] args) {
		hyperLogLog = new HyperLogLog();
		uniqueWords = new HashSet<String>();
	}
	
	@Override
	public void reduce(Integer index, HyperLogLogVbl value) {
		HyperLogLog.registers[index] = value.leadingZero;
		uniqueWords.addAll(value.words);
	}
	
	@Override
	public void finish() {
		double alpha = HyperLogLogUtil.computeConstant();
		double indicator = HyperLogLogUtil.computeIndicator();
		
		double rawEstimate = HyperLogLogUtil.computeRawEstimate(alpha, indicator);
		cardinality = hyperLogLog.getCardinality(rawEstimate);
		
		System.out.printf("HyperLogLog Appromximated Cardinality : %d\n",
				(int)Math.floor(cardinality));
		System.out.printf("Actual Cardinality : %d\n",uniqueWords.size());
		System.out.flush();
	}
}
