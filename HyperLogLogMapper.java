import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.TextId;

public class HyperLogLogMapper extends Mapper<TextId, String, Integer, HyperLogLogVbl>{

	private Pattern pattern;
	private Matcher matcher;
	private HyperLogLogVbl registerVbl = new HyperLogLogVbl();
	MessageDigest msgDigest = null;
	
	@Override
	public void start(String[] args, Combiner<Integer, HyperLogLogVbl> combiner) {
		pattern = Pattern.compile(args[0]);
		try {
			msgDigest = (MessageDigest) HyperLogLogUtil.MSG_DIGEST.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void map(TextId textId, String line, 
			Combiner<Integer, HyperLogLogVbl> combiner) {
		int registerIdx;
		byte[] digest;
		
		matcher = pattern.matcher(line);
		while(matcher.find()){
			String ipaadress = matcher.group();
			
			msgDigest.update(ipaadress.getBytes());
			digest = msgDigest.digest();
			String binaryDigest = new BigInteger(1, digest).toString(2);
			
			registerIdx = HyperLogLogUtil.getRegisterIndex(binaryDigest);
			registerVbl.leadingZero = HyperLogLogUtil.getRegisterValue(binaryDigest);
			registerVbl.words.add(ipaadress);

			combiner.add(registerIdx, registerVbl);
		}
	}
	
}
