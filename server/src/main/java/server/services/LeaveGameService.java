package server.services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.JoinGameRequest;
import model.JoinGameResponse;

public class LeaveGameService {

    private AuthDAO authDAO = AuthDAO.getInstance();
    private GameDAO gameDAO = GameDAO.getInstance();

    public JoinGameResponse leaveGame(JoinGameRequest request, String auth){
        try{

            if(!authDAO.isValidToken(auth)){
                return new JoinGameResponse(false, "Error: unauthorized");
            }
            else if (!gameDAO.isValidGameID(request.gameID()) || !gameDAO.isValidColor(request.playerColor())){
                return new JoinGameResponse(false, "Error: bad request");
            }
            else{
                gameDAO.addUsername(request, null);
                return new JoinGameResponse(true, "Removed From Game");
            }


        }catch (Exception e){
            e.printStackTrace();
            return new JoinGameResponse(false, "Error");
        }
    }
}
