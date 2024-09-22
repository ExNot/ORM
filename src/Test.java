import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Test {

    public static void main(String[] args) {

        try {
            SimpleORM orm = new SimpleORM("jdbc:mysql://localhost:3306/testdb", "root", "Mysql1453.");
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john@example.com");
            orm.save(user);
            System.out.println("saved!");

            User retrievedUser = orm.findById(User.class, 1);
            System.out.println("User: " + retrievedUser);

            retrievedUser.setEmail("newemail@example.com");
            orm.update(retrievedUser);
            System.out.println("Updated!");

            User updatedUser = orm.findById(User.class, 1);
            System.out.println("Updated user: " + updatedUser);

            orm.delete(retrievedUser);
            System.out.println("DELETED");

            User deletedUser = orm.findById(User.class, 1);
            if (deletedUser == null){
                System.out.println("LOL");
            }


        }   catch (SQLException | IllegalAccessException | InstantiationException | NoSuchMethodException |
                   InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }


    }

}
