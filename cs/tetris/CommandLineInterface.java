import java.util.*;
import cs.tetris.geom.*;

/**
 * Command Line interface, useful for testing without a gui.
 */
public class CommandLineInterface {
    public static TreeMap<String, Object> vars;
    
    public static Object parseValue(String s) {
        if (s.startsWith("'")) {
            // a string
            s = s.substring(1);
            int i = 0;
            while (i >= 0) {
                i = s.indexOf("_", i + 1);
                if (i < 0)
                    break;
                int j = i;
                int u = i;
                while (--j >= 0) {
                    if (s.charAt(j) == '\\')
                        u = j;
                    else
                        break;
                }
                int nbs = i - u;
                int rbs = nbs >> 1;
                int esc = nbs & 1;
                String backslashes = "";
                for (int i = 0; i < rbs; i++)
                    backslashes += "\\";
                s = s.substring(0, u) + backslashes + (esc == 1 ? "_" : " ") + s.substring(i + 1);
                i -= rbs + esc;
            }
            return s;
        } else if (s.matches("^[0-9.-].*")) {
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException e) {
                return Double.valueOf(s);
            }
        } else {
            return vars.get(s);
        }
    }
    
    public static void main(String argv[]) {
        Scanner s = new Scanner(System.in);
        while (true) {
            /*
             * Commands:
             * set [var] [value]
             * 	value can be:
             * 		A variable name
             * 		An integer, double, or float
             * 		A string starting with a ' and spaces as _s (can be escaped)
             * new [classname] [constructor arguments]
             * 	arguments are specified in the same format as values
             * 	stored in variable '_ret'
             * call [this] [function name] [arguments]
             * 	stored in variable '_ret'
             * callstatic [classname] [function name] [arguments]
             */
        	String cmd = s.next();
            if (cmd.equals("set")) {
                var = s.next();
                val = s.next();
                vars.put(var, parseValue(val));
            }
        }
    }
}