package ch.imedias.rsccfx.model.connectionutils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.ice4j.ice.Agent;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;


public class IceStateListener implements PropertyChangeListener {


  public Component rtpComponent;

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof Agent) {


      Agent agent = (Agent) evt.getSource();

      if (agent.getState().equals(IceProcessingState.TERMINATED)) {


        // Your agent is connected. Terminated means ready to communicate
        for (IceMediaStream stream : agent.getStreams()) {
          if (stream.getName().contains("data")) {

            rtpComponent = stream.getComponent(org.ice4j.ice.Component.RTP);

          }
        }
      }
    }
  }

}