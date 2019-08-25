package org.topicquests.ks.backside.javalin.user;

import org.mindrot.jbcrypt.BCrypt;
import org.topicquests.ks.backside.javalin.AppEnvironment;

public class UserController {
	private AppEnvironment environment;
	private UserDao userDao;

	public UserController(AppEnvironment env) {
		environment = env;
		userDao = environment.getUserDao();
	}
    // Authenticate the user by hashing the inputted password using the stored salt,
    // then comparing the generated hashed password to the stored hashed password
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, user.salt);
        return hashedPassword.equals(user.hashedPassword);
    }

    // This method doesn't do anything, it's just included as an example
    public void setPassword(String username, String oldPassword, String newPassword) {
        if (authenticate(username, oldPassword)) {
            String newSalt = BCrypt.gensalt();
            String newHashedPassword = BCrypt.hashpw(newSalt, newPassword);
            // Update the user salt and password
        }
    }
}
