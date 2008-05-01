package sicssim.peers;

import java.util.Random;

import sicssim.config.SicsSimConfig;
import sicssim.links.AbstractLink;
import sicssim.network.Bandwidth;
import sicssim.network.Network;
import sicssim.network.NodeId;
import sicssim.types.Data;
import sicssim.types.EventType;

/**
 * Nodes simulated in the system have to implement this interface
 * @author Amir Payberah
 * @author Fatemeh Rahimian
 */
public abstract class BandwidthPeer extends AbstractPeer {
	protected int downloadBandwidth;
	protected int uploadBandwidth;
	
	protected Bandwidth bandwidth;
	
//----------------------------------------------------------------------------------
	/**
	 * Initialize the peer.
	 */
	public void init(NodeId nodeId, AbstractLink link, Network network) {
		super.init(nodeId, link, network);
		this.bandwidth = this.network.getBandwidth();
		this.uploadBandwidth = ((new Random()).nextInt(SicsSimConfig.UPLOAD_BW) + 1) * SicsSimConfig.SEGMENT_RATE;
		this.downloadBandwidth = (SicsSimConfig.DOWNLOAD_BW) * SicsSimConfig.SEGMENT_RATE;
	}

//----------------------------------------------------------------------------------
	/**
	 * Initialize the peer with specific bandwidth
	 */
	public void init(NodeId nodeId, AbstractLink link, Network network, int uploadBandwidth, int downloadBandwidth) {
		super.init(nodeId, link, network);
		this.bandwidth = this.network.getBandwidth();
		this.uploadBandwidth = uploadBandwidth;
		this.downloadBandwidth = downloadBandwidth;
	}
	
//----------------------------------------------------------------------------------
	public void setUploadBandwidth(int uploadBandwidth) {
		this.uploadBandwidth = uploadBandwidth;
	}

//----------------------------------------------------------------------------------
	public void setDownloadBandwidth(int downloadBandwidth) {
		this.downloadBandwidth = downloadBandwidth;
	}

//----------------------------------------------------------------------------------
	public int getUploadBandwidth() {
		return this.uploadBandwidth;
	}

//----------------------------------------------------------------------------------
	public int getDownloadBandwidth() {
		return this.downloadBandwidth;
	}
	
//----------------------------------------------------------------------------------
	public int getAvailableDownloadBandwidth() {
		return this.downloadBandwidth - this.bandwidth.getTotalDownloadBandwidth(this.nodeId);
	}

//----------------------------------------------------------------------------------
	public int getAvailableUploadBandwidth() {
		return this.uploadBandwidth - this.bandwidth.getTotalUploadBandwidth(this.nodeId);
	}

//----------------------------------------------------------------------------------
	public int getUploadBandwidthTo(NodeId destId) {
		return this.bandwidth.getCurrentUploadRate(this.nodeId, destId);
	}

//----------------------------------------------------------------------------------
    protected boolean startSendData(NodeId destId, Object msg) {
		Data controlData = new Data();
		controlData.data = msg;
		controlData.type = EventType.START_RECV_DATA;
				
		if (this.bandwidth.getTotalUploadBandwidth(this.nodeId) + SicsSimConfig.SEGMENT_RATE <= this.uploadBandwidth) {
			this.link.send(destId, controlData);
			return true;
		}
		else
			return false;			
	}

//----------------------------------------------------------------------------------
    protected void stopSendData(NodeId destId, Object msg) {
		Data controlData = new Data();
		controlData.data = msg;
		controlData.type = EventType.STOP_RECV_DATA;

		this.link.send(destId, controlData);
	}
}

