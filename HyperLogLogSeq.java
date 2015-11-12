import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rit.pj2.Task;

public class HyperLogLogSeq extends Task{
	
	double cardinality;
	
	@Override
	public void main(String[] args) throws Exception {
		if(args.length != 3){
			System.err.println("Enter correct number of arguments");
			printUsage();
		}
		
		//HyperLogLog hyperLogLog= new HyperLogLog();
		
		File file = new File(args[0]);
		Pattern pattern = Pattern.compile(args[1]);
		//HyperLogLog.init(Integer.parseInt(args[2]));
		try(BufferedReader reader = 
				new BufferedReader(new FileReader(file))){
			MessageDigest msgDigest = (MessageDigest) HyperLogLogUtil.MSG_DIGEST.clone();
			String line;
			while((line = reader.readLine()) != null){
				Matcher matcher = pattern.matcher(line);
				while(matcher.find()){
					String ipaadress = matcher.group();
					HyperLogLogUtil.MaptoRegister(ipaadress, msgDigest);
				}
			}
		}
		
		double alpha = HyperLogLogUtil.computeConstant();
		double indicator = HyperLogLogUtil.computeIndicator(HyperLogLog.registers);
		double rawEstimate = HyperLogLogUtil.computeRawEstimate(alpha, indicator);
		
		//cardinality = hyperLogLog.getCardinality(rawEstimate, HyperLogLog.registers);
		System.out.println("HyperLogLog Appromximated Cardinality : "
						+ (int)Math.floor(cardinality));
	}

	protected static int coresRequired(){
		 return 1;
	}
	
	private void printUsage() {
		System.err.println("Print Usage");
		throw new IllegalArgumentException();
	}
	
}
