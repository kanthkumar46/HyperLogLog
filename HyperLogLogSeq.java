import java.io.File;
import java.util.Scanner;
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
		
		HyperLogLog hyperLogLog= new HyperLogLog();
		
		File file = new File(args[0]);
		Pattern pattern = Pattern.compile(args[1]);
		HyperLogLog.initRegister(Integer.parseInt(args[2]));
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			Matcher matcher = pattern.matcher(line);
			if(matcher.find()){
				String ipaadress = matcher.group();
				HyperLogLogUtil.MaptoRegister(ipaadress);
			}
		}
		
		double alpha = HyperLogLogUtil.computeConstant(hyperLogLog);
		double indicator = HyperLogLogUtil.computeIndicator();
		
		double rawEstimate = HyperLogLogUtil.computeRawEstimate(alpha, indicator);
		cardinality = hyperLogLog.getCardinality(rawEstimate);
		
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
