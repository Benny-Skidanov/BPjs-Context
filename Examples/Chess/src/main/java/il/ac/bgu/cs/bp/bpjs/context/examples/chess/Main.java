package il.ac.bgu.cs.bp.bpjs.context.examples.chess;

import java.io.*;
import java.util.Scanner;

/**
 * Created By: Assaf, On 14/02/2020
 * Description:
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // String inData = "position fen 8/p3pp1p/8/1PPP2P1/8/8/4PP2/8 w k - 0 1\n";
        String inData = "position fen r1b2b1r/pppppppp/8/8/8/8/PPPPPPPP/R1B2B1R w k - 0 1\n";
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream(inData.getBytes()));

        UCI uci = new UCI();
        Thread t = new Thread(uci);
        t.start();
        t.join();
    }
}