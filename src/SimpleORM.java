import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class SimpleORM {
    private Connection connection;

    public SimpleORM(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void save(Object object) throws SQLException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder(") VALUES (");

        for (Field field: fields) {
            field.setAccessible(true);
            query.append(field.getName()).append(", ");
            values.append("?, ");
        }

        query.setLength(query.length() -2);
        values.setLength(values.length() -2);
        query.append(values).append(")");


        try (PreparedStatement stmt = connection.prepareStatement(query.toString())){
            int parameterIndex = 1;
            for (Field field : fields) {
                stmt.setObject(parameterIndex++, field.get(object));
            }

            stmt.executeUpdate();
        }


    }


    public <T> T findById(Class<T> clazz, int id) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String tableName = clazz.getSimpleName().toLowerCase();
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                T object = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(object, rs.getObject(field.getName()));
                }
                return object;
            }
        }
        return null;
    }


    public void update(Object object) throws SQLException, IllegalAccessException, NoSuchFieldException {

        Class<?> clazz = object.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");

        for (Field field : fields) {
            if (!field.getName().equals("id")) {
                query.append(field.getName()).append(" = ?, ");
            }
        }

        query.setLength(query.length() -2);
        query.append(" WHERE id = ?");

        try (PreparedStatement stmt = connection.prepareStatement(query.toString())){
            int parameterIndex = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.getName().equals("id")){
                    stmt.setObject(parameterIndex++, field.get(object));
                }
            }

            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            stmt.setObject(parameterIndex, idField.get(object));

            stmt.executeUpdate();
        }

    }
    public void delete(Object object) throws SQLException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        String tableName = clazz.getSimpleName().toLowerCase();

        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)){
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            stmt.setObject(1, idField.get(object));

            stmt.executeUpdate();
        }
    }




}
