package ui;

public class Repl{
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;

    public Repl(String serverUrl){
        preLoginClient = new PreLoginClient();
        postLoginClient = new PostLoginClient();
        gameClient = new GameClient();
    }

    public void run(){




    }
}
