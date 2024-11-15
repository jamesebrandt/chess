package server.services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.JoinGameRequest;
import model.JoinGameResponse;

public class JoinGameService {

    private AuthDAO authDAO = AuthDAO.getInstance();
    private GameDAO gameDAO = GameDAO.getInstance();

    public JoinGameResponse joinGame(JoinGameRequest request, String auth){
        try{

            if(!authDAO.isValidToken(auth)){
                return new JoinGameResponse(false, "Error: unauthorized");
            }
            else if (!gameDAO.isValidGameID(request.gameID()) || !gameDAO.isValidColor(request.playerColor())){
                return new JoinGameResponse(false, "Error: bad request");
            }
            else if (!gameDAO.isStealingTeamColor(request)){
                return new JoinGameResponse(false, "Error: already taken");
            }
            else{
                gameDAO.addUsername(request, AuthDAO.getInstance().getUser(auth));
                return new JoinGameResponse(true, "Added!");
            }


        }catch (Exception e){
            e.printStackTrace();
            return new JoinGameResponse(false, "Error");
        }
    }
}
