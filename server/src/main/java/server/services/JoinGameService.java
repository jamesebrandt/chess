package server.services;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import model.JoinGameRequest;
import model.JoinGameResponse;

public class JoinGameService {

    private UserDAO userDAO = UserDAO.getInstance();
    private AuthDAO authDAO = AuthDAO.getInstance();
    private GameDAO gameDAO = GameDAO.getInstance();

    public JoinGameResponse joinGame(JoinGameRequest request, String auth){
        try{

            //check for another user playing as that team
            if(!authDAO.isValidToken(auth)){
                return new JoinGameResponse(false, "Error: unauthorized");
            }
            else if (gameDAO.canJoinGameDb(request)){
                gameDAO.addUsername(request, AuthDAO.getInstance().getUser(auth));
                return new JoinGameResponse(true, "Added!");
            }
            else{
                return new JoinGameResponse(false, "Error");
            }



        }catch (Exception e){
            e.printStackTrace();
            return new JoinGameResponse(false, "Error");
        }
    }
}
