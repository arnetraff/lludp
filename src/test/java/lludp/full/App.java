

package lludp.full;

import org.junit.Test;

public class App {
    @Test public void t() {
        Server s = new Server();
        Client c = new Client();
        s.run();
        c.run();


        


    }
}