import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperLogLogUtil {
	static MessageDigest MSG_DIGEST;
	static Pattern pattern = Pattern.compile("10*$");

	static {
		try {
			MSG_DIGEST = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void MaptoRegister(String ipaadress, MessageDigest msgDigest) throws CloneNotSupportedException {
		byte curRegisterVal;
		int registerIdx;
		byte[] digest;
		byte leadingZeroCount;
		msgDigest.update(ipaadress.getBytes());

		digest = msgDigest.digest();
		String binaryDigest = new BigInteger(1, digest).toString(2);
		registerIdx = getRegisterIndex(binaryDigest);
		leadingZeroCount = getRegisterValue(binaryDigest);
		curRegisterVal = HyperLogLog.registers[registerIdx];
		leadingZeroCount = curRegisterVal < leadingZeroCount ? leadingZeroCount : curRegisterVal;
		HyperLogLog.registers[registerIdx] = leadingZeroCount;
	}

	public static int getRegisterIndex(String binaryHash) {
		int registerIdx;
		int length = binaryHash.length();
		String subString = binaryHash.substring(length - HyperLogLog.registerIdxSize, length);
		registerIdx = Integer.parseInt(subString, 2);
		return registerIdx;
	}

	public static double cardinalityRatio(double estimateA, double estimateB) {
		return Math.max(estimateA, estimateB) / Math.min(estimateA, estimateB);
	}

	public static double overlap(double intersection, double estimateA, double estimateB) {
		return intersection / Math.min(estimateA, estimateB);
	}

	public static double relativeError(double trueValue, double estimate) {
		return 100.0 * (trueValue - estimate) / trueValue;
	}


	public static byte getRegisterValue(String binaryHash) {
		byte leadingZeroCount = 1;
		int length = binaryHash.length();
		String remaining = binaryHash.substring(0, length - 64 - HyperLogLog.registerIdxSize);
		Matcher matcher = pattern.matcher(remaining);
		if (matcher.find())
			leadingZeroCount = (byte) matcher.group().length();
		return leadingZeroCount;
	}

	public static double computeConstant() {
		double constant;
		switch (HyperLogLog.registerCount) {
		case 1:
		case 2:
		case 4:
		case 8:
			throw new IllegalArgumentException("m should be power of two and m>= 16");
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

	

	public static double computeIndicator(byte [] registers) {
		double indicator;
		double harmonicMean = 0.0;
		for (byte registerVal : registers) {
			harmonicMean += 1 / (1 << registerVal);
		}
		indicator = 1 / harmonicMean;
		return indicator;
	}

	public static double computeRawEstimate(double alpha, double indicator) {
		double rawEstimate;
		rawEstimate = alpha * Math.pow(HyperLogLog.registerCount, 2) * indicator;
		return rawEstimate;
	}

}
