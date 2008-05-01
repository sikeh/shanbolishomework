package sicssim.coolstream.types;

import java.util.SortedSet;

public class PartnerInfo {
	public SortedSet<Integer> bufferMap; 
	public int uploadBw;
	
	public PartnerInfo(SortedSet<Integer> bufferMap, int uploadBw) {
		this.bufferMap = bufferMap;
		this.uploadBw = uploadBw;
	}
	
//----------------------------------------------------------------------------------
	public String toString() {
		String str = new String("(upload: " + this.uploadBw + ", bm: ");
		str += this.bufferMap.toString();
		str += ")";
		
		return str;		
	}
}
