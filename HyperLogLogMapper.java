import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.rit.pj2.vbl.IntVbl;
import edu.rit.pjmr.Combiner;
import edu.rit.pjmr.Mapper;
import edu.rit.pjmr.TextId;

public class HyperLogLogMapper extends Mapper<TextId, String, Integer, IntVbl>{

	private Pattern pattern;
	private Matcher matcher;
	private IntVbl leadingZeroCount = new IntVbl.Max();
	
	@Override
	public void start(String[] args, Combiner<Integer, IntVbl> combiner) {
		pattern = Pattern.compile(args[0]);
	}
	
	@Override
	public void map(TextId textId, String line, 
			Combiner<Integer, IntVbl> combiner) {
		int registerIdx;
		byte[] digest;
		
		matcher = pattern.matcher(line);
		if(matcher.find()){
			String ipaadress = matcher.group();
			
			HyperLogLogUtil.MSG_DIGEST.update(ipaadress.getBytes());
			digest = HyperLogLogUtil.MSG_DIGEST.digest();
			String binaryDigest = new BigInteger(1, digest).toString(2);
			
			registerIdx = HyperLogLogUtil.getRegisterIndex(binaryDigest);
			leadingZeroCount.item = HyperLogLogUtil.getRegisterValue(binaryDigest);

			combiner.add(registerIdx, leadingZeroCount);
		}
	}
	
}
