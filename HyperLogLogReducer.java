import java.util.HashSet;

import edu.rit.pjmr.Reducer;

public class HyperLogLogReducer extends Reducer<String, HyperLogLogVbl> {

	HyperLogLog hyperLogLog;
	double cardinality;
	HashSet<String> uniqueWords;
	HashSet<String> uniqueWords1;
	HashSet<String> intersection;
	HashSet<String> unionHs;
	
	@Override
	public void start(String[] args) {
		hyperLogLog = new HyperLogLog(Integer.parseInt(args[1]));
		uniqueWords = new HashSet<>();
		uniqueWords1 = new HashSet<>();
		
	}

	@Override
	public void reduce(String index, HyperLogLogVbl value) {
		
		int registerIndex = Integer.parseInt(index.substring(1, index.length()));
		if ((index.startsWith("A")) && value.words.size() != 0) {
			uniqueWords.addAll(value.words);
		} 
		
		else if ((index.startsWith("B")) && value.words.size() != 0) {
			uniqueWords1.addAll(value.words);
		}
		
		else if (index.startsWith("B") && value.words.size() == 0) {
			HyperLogLog.resigtersA[registerIndex] = value.leadingZero;
		} 
		else if (index.startsWith("A") && value.words.size() == 0) {
			HyperLogLog.registers[registerIndex] = value.leadingZero;
		}
		
		
	}

	@Override
	public void finish() {
		
		intersection = new HashSet<>(uniqueWords);
		intersection.retainAll(uniqueWords1);
		unionHs = new HashSet<>(uniqueWords);
		unionHs.addAll(uniqueWords1);
		
		double alpha = HyperLogLogUtil.computeConstant();
		double indicator = HyperLogLogUtil.computeIndicator(HyperLogLog.registers);
		double inicator1 = HyperLogLogUtil.computeIndicator(HyperLogLog.resigtersA);
		
		double rawEstimate1 = HyperLogLogUtil.computeRawEstimate(alpha, inicator1);
		double rawEstimate = HyperLogLogUtil.computeRawEstimate(alpha, indicator);
		cardinality = hyperLogLog.getCardinality(rawEstimate, HyperLogLog.registers);
		double cardinality1 = hyperLogLog.getCardinality(rawEstimate1, HyperLogLog.resigtersA);

		byte[] union = new byte[HyperLogLog.registerCount];
		
		for(int i =0; i< HyperLogLog.registerCount; i++){
			union[i] = (byte) Math.max(HyperLogLog.registers[i], HyperLogLog.resigtersA[i]);
		}
		
		double indicator2 = HyperLogLogUtil.computeIndicator(union);
		double rawEstimate2 = HyperLogLogUtil.computeRawEstimate(alpha, indicator2);
		double cardinality2 = hyperLogLog.getCardinality(rawEstimate2, union);
		
		double intersectionCard = cardinality1 + cardinality - cardinality2;
		
		System.out.printf("HyperLogLog Estimated Cardinality A: %d\n", (int) Math.floor(cardinality));
		System.out.printf("Actual Cardinality A: %d\n", uniqueWords.size());
		System.out.printf("Relative Error : %f%s\n",
				HyperLogLogUtil.relativeError(uniqueWords.size(), Math.floor(cardinality)), "%");
		
		System.out.println();

		System.out.printf("HyperLogLog Estimated Cardinality B: %d\n", (int) Math.floor(cardinality1));
		System.out.printf("Actual Cardinality B: %d\n", uniqueWords1.size());
		System.out.printf("Relative Error : %f%s\n",
				HyperLogLogUtil.relativeError(uniqueWords1.size(), (int) Math.floor(cardinality1)), "%");
		
		System.out.println();
		
		System.out.println("Estimate (A union B): "+ cardinality2);
		System.out.printf("Actual (A union B): %d\n", unionHs.size());
		System.out.printf("Relative Error : %f%s\n",
				HyperLogLogUtil.relativeError(unionHs.size(), cardinality2), "%");
		
		
		System.out.println();
		
		System.out.println("Estimate (A intersect B): "+ intersectionCard);
		System.out.printf("Actual (A intersect B): %d\n", intersection.size());
		
		System.out.printf("Relative Error : %f%s\n",
				HyperLogLogUtil.relativeError(intersection.size(), intersectionCard), "%");
		
		System.out.println();
		
		System.out.println(intersectionCard/ cardinality2);

	}
}
