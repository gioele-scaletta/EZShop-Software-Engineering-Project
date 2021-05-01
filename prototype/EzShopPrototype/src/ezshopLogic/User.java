package ezshopLogic;
import java.lang.Exception;

public class User {

    private enum Role {
        administrator, shopmanager, cashier
    }

    private String username;
    private String password;
    private Role role;
    private Integer id;

    public User(Integer id, String username, String password, String role) throws Exception /*throws InvalidRoleException*/ {
        this.id = id;
        this.username = username;
        this.password = password;
        try {
            this.role = Role.valueOf(role.toLowerCase());
        } catch(IllegalArgumentException e) {
            //System.out.println("Ruolo assegnato non valido");
            //throw new InvalidRoleException;
            throw new Exception("Ruolo assegnato non valido");
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role.toString();
    }

    public boolean setRole(String role) /*throws InvalidRoleException*/ {
        try {
            this.role = Role.valueOf(role.toLowerCase());
        } catch(IllegalArgumentException e) {
            System.out.println("Ruolo assegnato non valido");
            //throw new InvalidRoleException;
            return false;
        }
        return true;
    }

    public Integer getID(){
        return this.id;
    }

    public boolean canManageUsers(){
        return switch (role) {
            case administrator -> true;
            case cashier -> false;
            case shopmanager -> false;
            default -> false;
        };
    }

    public boolean canManageProductList(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> false;
            default -> false;
        };
    }

    public boolean canListProducts(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> true;
            default -> false;
        };
    }

    public boolean canManageInventory(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> true;
            default -> false;
        };
    }

    public boolean canManageSaleTransactions(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> true;
            default -> false;
        };
    }

    public boolean canManagePayments(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> true;
            default -> false;
        };
    }

    public boolean canManageAccounting(){
        return switch (role) {
            case administrator -> true;
            case cashier -> true;
            case shopmanager -> false;
            default -> false;
        };
    }

}