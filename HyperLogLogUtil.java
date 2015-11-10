import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperLogLogUtil {
	static MessageDigest MSG_DIGEST;
	static Pattern pattern = Pattern.compile("10*$");
	
	static{
		try {
			MSG_DIGEST = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static void MaptoRegister(String ipaadress, MessageDigest msgDigest) 
			throws CloneNotSupportedException {
		int curRegisterVal;
		int registerIdx, leadingZeroCount;
		byte[] digest;
		
		msgDigest.update(ipaadress.getBytes());
		digest = msgDigest.digest();
		String binaryDigest = new BigInteger(1, digest).toString(2);
		
		registerIdx = getRegisterIndex(binaryDigest);
		leadingZeroCount = getRegisterValue(binaryDigest);
		
		curRegisterVal = HyperLogLog.registers[registerIdx];
		leadingZeroCount =  curRegisterVal < leadingZeroCount ? 
				leadingZeroCount : curRegisterVal;
		
		HyperLogLog.registers[registerIdx] = leadingZeroCount ;
	}
	
	public static int getRegisterIndex(String binaryHash){
		int registerIdx;
		int length = binaryHash.length();
		registerIdx = Integer.parseInt(binaryHash.substring
							(length - HyperLogLog.registerIdxSize , length), 2);
		return registerIdx;
	}
	
	
	
	public static int getRegisterValue(String binaryHash){
		int leadingZeroCount = 1;
		int length = binaryHash.length();
		String remaining = binaryHash.substring(0, length - HyperLogLog.registerIdxSize);
		Matcher matcher = pattern.matcher(remaining);
		if(matcher.find())
			leadingZeroCount = matcher.group().length();
		return leadingZeroCount;
	}
	
	public static double computeConstant() {
		double constant;
		switch (HyperLogLog.registerCount) {
		case 16:
			constant = HyperLogLog.alpha16;
			break;
		case 32:
			constant = HyperLogLog.alpha32;
			break;
		case 64:
			constant = HyperLogLog.alpha64;
			break;
		default:
			constant = HyperLogLog.alphaM;
			break;
		}
		
		return constant;
	}
	
	public static double cardinalityRatio(double estimateA, double estimateB){
		return Math.max(estimateA, estimateB)/ Math.min(estimateA, estimateB);
	}
	
	public static double overlap(double intersection, double estimateA, double estimateB){
		return intersection/ Math.min(estimateA, estimateB);
	}
	
	public static double absoluteError(double estimate, double trueValue) {
		return Math.abs(trueValue - estimate )/ trueValue; 
	}
	
	

	public static double computeIndicator() {
		double indicator;
		double harmonicMean = 0.0;
		
		for(Integer registerVal : HyperLogLog.registers){
			harmonicMean += 1/Math.pow(2, registerVal);
		}
		indicator = 1/harmonicMean;
		return indicator;
	}
	
	public static double computeRawEstimate(double alpha, double indicator) {
		double rawEstimate;
		rawEstimate = alpha * Math.pow(HyperLogLog.registerCount, 2) * indicator;
		return rawEstimate;
	}
	
}
