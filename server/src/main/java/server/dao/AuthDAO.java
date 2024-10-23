package server.dao;

public class AuthDAO {

    public void deleteAll() {
        String sql = "DELETE FROM auth_tokens";
    }
}