package server.services;

import model.CreateGameRequest;
import model.CreateGameResponse;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class CreateGameService {

    private final GameDAO gameDAO = GameDAO.getInstance();
    private final AuthDAO authDAO = AuthDAO.getInstance();

    public CreateGameResponse createGame(CreateGameRequest request){
        try {
            if (request.gameName() == null){
                return new CreateGameResponse(false, "Error: bad request", null);
            }
            else if (!authDAO.isValidToken(request.authToken())){
                return new CreateGameResponse(false, "Error: unauthorized", null);
            }
            else if(gameDAO.isDuplicateGameName(request.gameName())){
                return new CreateGameResponse(false, "Error: Name already in use", null);
            }
            else{
                int iD = gameDAO.createGame(request.gameName(), request.authToken());
                return new CreateGameResponse(true, "Game Created", iD);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new CreateGameResponse(false, "Error", null);
        }
    }
}
