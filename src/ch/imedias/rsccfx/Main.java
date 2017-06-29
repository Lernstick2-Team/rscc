package ch.imedias.rsccfx;

import com.tigervnc.vncviewer.VncViewer;

/**
 * Created by pwigger on 29.06.17.
 */
public class Main {
  public static void main(String[] args) {
    String[] newArgs={"localhost:2601"};
   VncViewer viewer = new VncViewer(newArgs);
   viewer.start();
  }
}
