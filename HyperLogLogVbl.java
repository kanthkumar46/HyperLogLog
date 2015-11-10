import java.io.Serializable;
import java.util.HashSet;

import edu.rit.pj2.Vbl;

public class HyperLogLogVbl implements Vbl, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int leadingZero;
	public HashSet<String> words;
	
	
	public HyperLogLogVbl (){
		words = new HashSet<String>();
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
		this.words.addAll(hyperVbl.words);
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
