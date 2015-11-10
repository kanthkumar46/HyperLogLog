import edu.rit.pj2.vbl.IntVbl;
import edu.rit.pjmr.PjmrJob;
import edu.rit.pjmr.TextFileSource;
import edu.rit.pjmr.TextId;

public class HyperLogLogSmp extends PjmrJob<TextId, String, Integer, IntVbl>{

	@Override
	public void main(String[] args) throws Exception {
		if(args.length != 3){
			System.err.println("Enter correct number of arguments");
			printUsage();
		}
		
		String file = args[0];
		String pattern = args[1];
		HyperLogLog.init(Integer.parseInt(args[2]));
		
		String[] nodes = args[0].split (",");
		int NT = Math.max (threads(), 1);
		
		for(String node : nodes){
			/*mapperTask(node).
			source(new TextFileSource("src//accesslog")).
			mapper(NT, HyperLogLogMapper.class, pattern);*/
		}
		
		mapperTask().
		source(new TextFileSource(file)).
		mapper(NT, HyperLogLogMapper.class, pattern);
		
		reducerTask().reducer(HyperLogLogReducer.class);
		
		startJob();
	}

	private void printUsage() {
		System.err.println("Print Usage");
		throw new IllegalArgumentException();
	}

}
