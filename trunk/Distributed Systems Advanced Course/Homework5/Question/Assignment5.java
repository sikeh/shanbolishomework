package assignment5;

import tbn.TBN;
import tbn.api.Channel;
import tbn.api.ChannelDoesNotExistException;
import tbn.api.Component;
import tbn.api.MalformedComponentNameException;
import tbn.api.NoSuchComponentException;
import tbn.api.ParsingFailedException;
import tbn.api.SystemBuildFailedException;
import tbn.api.TBNSystem;
import tbn.comm.mina.NodeReference;
import assignment1.events.InitEvent;
import assignment4.events.ApplicationEvent;
import assignments.util.TopologyDescriptor;
import assignments.util.TopologyParser;

public class Assignment5 {

	private static TopologyParser topologyParser;

	private static TopologyDescriptor topologyDescriptor;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			System.err.println("usage: Assignment5 <topology.xml> "
					+ "<nodeId> <ops>");
			return;
		}

		try {
			String topologyFileName = args[0];
			String nodeId = args[1];
			String ops = args[2];
			ApplicationEvent applicationEvent = new ApplicationEvent(ops);

			// Parsing Topology Descriptor File
			topologyParser = new TopologyParser(Integer.parseInt(nodeId),
					topologyFileName);

			topologyDescriptor = topologyParser.parseTopologyFile();
			NodeReference.setThisNodeReference(topologyDescriptor
					.getMyNodeRef());

			TBNSystem sys = TBN.getSystem();
			sys.buildSystem(Assignment5.class.getResource("assignment5.xml")
					.getPath());
			 sys.startReconfigurationServer();

			InitEvent startEvent = new InitEvent(topologyDescriptor);

			Component component = sys.findComponent("ApplicationComponent"
					+ "Factory:ApplicationComponent");

			Channel initChannel = sys.findChannel("InitEventChannel");
			Channel applicationChannel = sys
					.findChannel("ApplicationComponentInputChannel");

			component.raiseEventInChannel(startEvent, initChannel);
			component.raiseEventInChannel(applicationEvent, applicationChannel);
		} catch (ParsingFailedException e) {
			e.printStackTrace();
		} catch (SystemBuildFailedException e) {
			e.printStackTrace();
		} catch (NoSuchComponentException e) {
			e.printStackTrace();
		} catch (MalformedComponentNameException e) {
			e.printStackTrace();
		} catch (ChannelDoesNotExistException e) {
			e.printStackTrace();
		}
	}
}
