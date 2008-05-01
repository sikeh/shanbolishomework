package sicssim.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import sicssim.types.LinkRate;

public class Bandwidth {

	HashMap<String, HashMap<String, LinkRate>> bandwidth = new HashMap<String, HashMap<String, LinkRate>>();
	
//----------------------------------------------------------------------------------
	public void useUploadBandwidth(NodeId srcId, NodeId destId, int rate) {
		HashMap<String, LinkRate> nodeList;
		LinkRate newRate;

		if (this.bandwidth.containsKey(srcId.toString())) {
			nodeList = this.bandwidth.get(srcId.toString());
			if (nodeList.containsKey(destId.toString()))
				newRate = nodeList.get(destId.toString());
			else
				newRate = new LinkRate();
		}
		else {
			nodeList = new HashMap<String, LinkRate>();
			newRate = new LinkRate();
		}

		newRate.uploadBandwidth += rate;
		nodeList.put(destId.toString(), newRate);
		this.bandwidth.put(srcId.toString(), nodeList);
	}

//	----------------------------------------------------------------------------------
	public void useDownloadBandwidth(NodeId srcId, NodeId destId, int rate) {
		HashMap<String, LinkRate> nodeList;
		LinkRate newRate;

		if (this.bandwidth.containsKey(destId.toString())) {
			nodeList = this.bandwidth.get(destId.toString());
			if (nodeList.containsKey(srcId.toString()))
				newRate = nodeList.get(srcId.toString());
			else
				newRate = new LinkRate();
		}
		else {
			nodeList = new HashMap<String, LinkRate>();
			newRate = new LinkRate();
		}
		
		newRate.downloadBandwidth += rate;
		nodeList.put(srcId.toString(), newRate);
		this.bandwidth.put(destId.toString(), nodeList);
	}

//----------------------------------------------------------------------------------
	public int getTotalDownloadBandwidth(NodeId nodeId) {
		HashMap<String, LinkRate> nodeList;
		int downloadRate = 0;
		
		if ((nodeList = this.bandwidth.get(nodeId.toString())) == null)
			return 0;
		
		Iterator<String> nodeListIter = nodeList.keySet().iterator();
		while (nodeListIter.hasNext())
			downloadRate += nodeList.get(nodeListIter.next()).downloadBandwidth;

		return downloadRate;
	}
	
//----------------------------------------------------------------------------------
	public int getTotalUploadBandwidth(NodeId nodeId) {
		HashMap<String, LinkRate> nodeList;
		int uploadRate = 0;
		
		if ((nodeList = this.bandwidth.get(nodeId.toString())) == null)
			return 0;

		Iterator<String> nodeListIter = nodeList.keySet().iterator();
		while (nodeListIter.hasNext())
			uploadRate += nodeList.get(nodeListIter.next()).uploadBandwidth;

		return uploadRate;
	}

//----------------------------------------------------------------------------------
	public boolean releaseUploadBandwidth(NodeId srcId, NodeId destId, int rate) {
		HashMap<String, LinkRate> nodeList;
		LinkRate currentRate;

		if (this.bandwidth.containsKey(srcId.toString())) {
			nodeList = this.bandwidth.get(srcId.toString());
			if (nodeList.containsKey(destId.toString()))
				currentRate = nodeList.get(destId.toString());
			else
				return false;
		}
		else 
			return false;

		currentRate.uploadBandwidth -= rate;

		if (currentRate.downloadBandwidth == 0 && currentRate.uploadBandwidth == 0)
			nodeList.remove(destId.toString());
		else
			nodeList.put(destId.toString(), currentRate);

		this.bandwidth.put(srcId.toString(), nodeList);
		
		return true;
	}

//----------------------------------------------------------------------------------
	public boolean releaseDownloadBandwidth(NodeId srcId, NodeId destId, int rate) {
		HashMap<String, LinkRate> nodeList;
		LinkRate currentRate;

		// Set the destination node rate		
		if (this.bandwidth.containsKey(destId.toString())) {
			nodeList = this.bandwidth.get(destId.toString());
			if (nodeList.containsKey(srcId.toString()))
				currentRate = nodeList.get(srcId.toString());
			else
				return false;
		}
		else 
			return false;

		currentRate.downloadBandwidth -= rate;

		if (currentRate.uploadBandwidth == 0 && currentRate.downloadBandwidth == 0) 
			nodeList.remove(srcId.toString());
		else
			nodeList.put(srcId.toString(), currentRate);

		this.bandwidth.put(destId.toString(), nodeList);
		
		return true;
	}

//----------------------------------------------------------------------------------
	public int getCurrentUploadRate(NodeId srcId, NodeId destId) {
		try {
			return this.bandwidth.get(srcId.toString()).get(destId.toString()).uploadBandwidth;
		}
		catch(Exception e) {
			return 0;
		}
	}

//----------------------------------------------------------------------------------
	public void removeNode(NodeId nodeId) {
		String node;
		HashMap<String, LinkRate> nodeList = new HashMap<String, LinkRate>();
		
		if (this.bandwidth.containsKey(nodeId.toString()))
			this.bandwidth.remove(nodeId.toString());

		Iterator<String> bandwidthIter = this.bandwidth.keySet().iterator();
		while (bandwidthIter.hasNext()) {
			node = bandwidthIter.next();
			nodeList = this.bandwidth.get(node);
			if (nodeList.containsKey(nodeId.toString())) {
				nodeList.remove(nodeId.toString());
				this.bandwidth.put(node, nodeList);
			}
		}
					
	}
	
//----------------------------------------------------------------------------------
	public Vector<String> getListOfUploads(NodeId nodeId) {
		if (!this.bandwidth.containsKey(nodeId.toString()) || this.bandwidth.get(nodeId.toString()).size() == 0)
			return null;
		
		String node;
		Vector<String> listOfUploads = new Vector<String>();
		HashMap<String, LinkRate> nodeList = this.bandwidth.get(nodeId.toString());
		Iterator<String> nodeListIter = nodeList.keySet().iterator();
		while (nodeListIter.hasNext()) {
			node = nodeListIter.next();
			if (nodeList.get(node).uploadBandwidth > 0)
				listOfUploads.add(node);
		}
		
		return ((listOfUploads.size() != 0) ? listOfUploads : null);
	}

//----------------------------------------------------------------------------------
	public Vector<String> getListOfDownloads(NodeId nodeId) {
		if (!this.bandwidth.containsKey(nodeId.toString()) || this.bandwidth.get(nodeId.toString()).size() == 0)
			return null;
		
		String node;
		Vector<String> listOfDownloads = new Vector<String>();
		HashMap<String, LinkRate> nodeList = this.bandwidth.get(nodeId.toString());
		Iterator<String> nodeListIter = nodeList.keySet().iterator();
		while (nodeListIter.hasNext()) {
			node = nodeListIter.next();
			if (nodeList.get(node).downloadBandwidth > 0)
				listOfDownloads.add(node);
		}
		
		return ((listOfDownloads.size() != 0) ? listOfDownloads : null);
	}
	
//----------------------------------------------------------------------------------
	public String toString() {
		String node1, node2;
		String str = new String();
		String newLine = System.getProperty("line.separator");		
		HashMap<String, LinkRate> nodeList = new HashMap<String, LinkRate>();
		
		Iterator<String> bandwidthIter = this.bandwidth.keySet().iterator();
		while (bandwidthIter.hasNext()) {
			node1 = bandwidthIter.next();
			str += (newLine + node1 + " => ");
			nodeList = this.bandwidth.get(node1);
			Iterator<String> nodeListIter = nodeList.keySet().iterator();
			while (nodeListIter.hasNext()) {
				node2 = nodeListIter.next();
				str += ("[" + node2 + " (up: " + nodeList.get(node2).uploadBandwidth + ", down: " + nodeList.get(node2).downloadBandwidth + ")]   ");
			}
		}
	
		return str;
	}
}
