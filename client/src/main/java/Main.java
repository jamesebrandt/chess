import Exceptions.ResponseException;
import chess.*;
import ui.Repl;

import javax.swing.plaf.synth.SynthDesktopIconUI;

public class Main {
    public static void main(String[] args) throws ResponseException {
        System.out.println("♕ 240 Chess Client: ♛");
        var port = 8080;
        var serverUrl = "http://localhost:"+port;
        if (args.length == 1){
            serverUrl = args[0];
        }
        new Repl(serverUrl).run();
    }
}