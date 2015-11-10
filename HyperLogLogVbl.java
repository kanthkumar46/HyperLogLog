import java.util.HashSet;

import edu.rit.pj2.Vbl;

public class HyperLogLogVbl implements Vbl {

	private int leadingZero;
	private HashSet<String> words;
	
	
	public HyperLogLogVbl (){
		words = new HashSet<>();
	}
	
	@Override
	public void reduce(Vbl vbl) {
		HyperLogLogVbl hyperVbl = (HyperLogLogVbl) vbl;
		if(this.leadingZero < hyperVbl.leadingZero){
			set(hyperVbl);
		}
		addToHashSet(hyperVbl);
	}

	@Override
	public void set(Vbl vbl) {
		HyperLogLogVbl hyperVbl = (HyperLogLogVbl) vbl;
		this.leadingZero = hyperVbl.leadingZero;	
	}
	
	public void addToHashSet(Vbl vbl){
		HyperLogLogVbl hyperVbl = (HyperLogLogVbl) vbl;
		for(String word: hyperVbl.words){
			this.words.add(word);
		}
	}

	@Override
	public Object clone() {
		HyperLogLogVbl hyperLogLogReduction = null;
		try {
			hyperLogLogReduction = (HyperLogLogVbl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return hyperLogLogReduction;
	}

}
