package sicssim.network;

public abstract class AuxiliaryOverlay {
	protected Network network;
	protected Bandwidth bandwidth;
	
//----------------------------------------------------------------------------------
	public void init(Network network, Bandwidth bandwidth) {
		this.network = network;
		this.bandwidth = bandwidth;
	}
	
//----------------------------------------------------------------------------------
	public abstract void update(long currentTime);

//----------------------------------------------------------------------------------
	public abstract void log(long currentTime);

}
