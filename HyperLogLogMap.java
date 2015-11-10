import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HyperLogLogMap {

	public static void main(String[] args) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Set<String> countMap = new HashSet<>();
		BufferedReader reader = new BufferedReader(new FileReader("Sherlock.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String split[] = line.split(" ");
			for (String str : split) {
				countMap.add(str);
			}
		}
		reader.close();
		
		Runtime rt = Runtime.getRuntime();
		rt.gc();
		double memory = (rt.totalMemory() - rt.freeMemory())/(1024.0 * 1024.0);
		System.out.println(memory);
		System.out.println(countMap.size());
		
	}
}
