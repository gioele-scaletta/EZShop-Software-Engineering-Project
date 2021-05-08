package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

public interface User {

    Integer getId();

    void setId(Integer id);

    String getUsername() throws InvalidUsernameException;

    void setUsername(String username) throws InvalidUsernameException;

    String getPassword();

    void setPassword(String password) throws InvalidPasswordException;

    String getRole();

    void setRole(String role) throws InvalidRoleException;
}
