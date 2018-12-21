
package lludp;

import java.io.PrintStream;;

public class Log {
    protected static PrintStream stream = System.out;

    protected static boolean debug = false;

    protected static void log(Object ... o) {
        for (Object i : o) {
            stream.print(i.toString());
        }
        stream.println();
    }

    public static void d(Object ... o) {
        if (debug == true)
        log(o);
    }
    public static void o(Object ... o) {
        
        log(o);
    }

}