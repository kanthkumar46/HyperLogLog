import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.TextId;

public class HyperLogLogMapper extends Mapper<TextId, String, String, HyperLogLogVbl>{

	private Pattern pattern;
	private Matcher matcher;
	
	MessageDigest msgDigest = null;
	HashSet<String> words = new HashSet<>();
	private String prefix;
	
	@Override
	public void start(String[] args, Combiner<String, HyperLogLogVbl> combiner) {
		pattern = Pattern.compile(args[0]);
		prefix = args[1];
		try {
			msgDigest = (MessageDigest) HyperLogLogUtil.MSG_DIGEST.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void map(TextId textId, String line, 
			Combiner<String, HyperLogLogVbl> combiner) {
		int registerIdx;
		byte[] digest;
		
		matcher = pattern.matcher(line);
		while(matcher.find()){
			String ipaadress = matcher.group();
			
			msgDigest.update(ipaadress.getBytes());
			digest = msgDigest.digest();
			String binaryDigest = new BigInteger(1, digest).toString(2);
			HyperLogLogVbl registerVbl = new HyperLogLogVbl();
			registerIdx = HyperLogLogUtil.getRegisterIndex(binaryDigest);
			registerVbl.leadingZero = (byte) HyperLogLogUtil.getRegisterValue(binaryDigest);
			words.add(ipaadress);
			combiner.add(prefix+registerIdx, registerVbl);
		}
	}


	@Override
	public void finish(Combiner<String, HyperLogLogVbl> combiner) {
		HyperLogLogVbl vbl = new HyperLogLogVbl();
		int index = (int)(Math.pow(2, HyperLogLog.registerIdxSize)+1);
		vbl.leadingZero = -1;
		vbl.words = words;
		combiner.add(prefix + index, vbl);
	}
	
}
