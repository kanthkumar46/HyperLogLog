import edu.rit.pjmr.PjmrJob;
import edu.rit.pjmr.TextFileSource;
import edu.rit.pjmr.TextId;

public class HyperLogLogSmp extends PjmrJob<TextId, String, String, HyperLogLogVbl> {

	@Override
	public void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Enter correct number of arguments");
			printUsage();
		}
		String pattern = args[0];
		HyperLogLog log = new HyperLogLog(Integer.parseInt(args[1]));

		//String[] nodes = args[2].split(",");
		int NT = Math.max(threads(), 1);
		
		String arr[] = new String[2];
		arr[0] = pattern;
		arr[1] = "A";
		int i = 0;
		
		/*for (String node : nodes) {
			if (i == 0) {
				arr[1] = "A";
				mapperTask(node)
				.source(new TextFileSource("/var/tmp/kkd6428/Sherlock.txt"))
				.mapper(NT,HyperLogLogMapper.class, arr);
			} else {
				arr[1] = "B";
				mapperTask(node)
				.source(new TextFileSource("/var/tmp/kkd6428/Sherlock.txt"))
				.mapper(NT,HyperLogLogMapper.class, arr);
			}
			i++;
		}*/
		
		mapperTask()
		.source(new TextFileSource("Sherlock.txt"))
		.mapper(NT,HyperLogLogMapper.class, arr);
		
		String arr1[] = new String[2];
		arr1[0] = pattern;
		arr1[1] = "B";
		
		mapperTask()
		.source(new TextFileSource("vocab.kos.txt"))
		.mapper(NT,HyperLogLogMapper.class, arr1);
		

		reducerTask().reducer(HyperLogLogReducer.class, args);

		startJob();
	}

	private void printUsage() {
		System.err.println("Print Usage");
		throw new IllegalArgumentException();
	}

}
