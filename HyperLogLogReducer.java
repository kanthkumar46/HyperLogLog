import edu.rit.pj2.vbl.IntVbl;
import edu.rit.pjmr.Reducer;

public class HyperLogLogReducer extends Reducer<Integer, IntVbl>{

	HyperLogLog hyperLogLog;
	double cardinality;
	
	@Override
	public void start(String[] args) {
		hyperLogLog = new HyperLogLog();
	}
	
	@Override
	public void reduce(Integer index, IntVbl value) {
		HyperLogLog.registers[index] = value.intValue();
	}
	
	@Override
	public void finish() {
		double alpha = HyperLogLogUtil.computeConstant();
		double indicator = HyperLogLogUtil.computeIndicator();
		
		double rawEstimate = HyperLogLogUtil.computeRawEstimate(alpha, indicator);
		cardinality = hyperLogLog.getCardinality(rawEstimate);
		
		System.out.printf("HyperLogLog Appromximated Cardinality : %d\n",
				(int)Math.floor(cardinality));
		System.out.flush();
	}
}
