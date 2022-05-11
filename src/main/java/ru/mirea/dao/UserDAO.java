package ru.mirea.dao;

import org.springframework.stereotype.Component;
import ru.mirea.models.User;

import java.sql.*;

/**
 * Класс для работы с БД с таблицей пользователей
 */
@Component
public class UserDAO {
  private static final String URL = "jdbc:mysql://localhost:3306/langmaster";
  private static final String USERNAME = "ezh1k";
  private static final String PASSWORD = "alastor_cool";

  private static Connection conn;

  /* Подключение к БД */
  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Выполняет регистрацию пользователя в БД
   * @param user        объект пользователя
   */
  public void registerUser(User user) {
    try {
      String query = "INSERT INTO `User`(`name`, `email`, `password`) VALUES(?, ?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPassword());

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Находит пользователя в БД по его id
   * @param id        идентификатор пользователя
   */
  public User getUser(int id) {
    User user = null;

    try {
      // `user_id` уникальное поле по умолчанию (PRIMARY KEY), поэтому вернётся 1 ряд
      String query = "SELECT * FROM `User` WHERE `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, id);
      ResultSet result = preparedStatement.executeQuery();

      // Пользователь с указанным id не найден
      if (!result.next())
        return null;

      user = new User();

      user.setId(result.getInt("user_id"));
      user.setName(result.getString("name"));
      user.setEmail(result.getString("email"));
      user.setPassword(result.getString("password"));
      user.setRegistrationDate(result
        .getDate("registration_date")
        .toString());
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return user;
  }

  /**
   * Находит пользователя в БД по его имени
   * @param name        имя пользователя
   */
  public User getUser(String name) {
    User user = null;

    try {
      // имя пользователя должно быть уникальным, поэтому вернётся только 1 ряд
      String query = "SELECT * FROM `User` WHERE `name` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, name);
      ResultSet result = preparedStatement.executeQuery();

      // Пользователь с указанным именем не найден
      if (!result.next())
        return null;

      user = new User();

      user.setId(result.getInt("user_id"));
      user.setName(result.getString("name"));
      user.setEmail(result.getString("email"));
      user.setPassword(result.getString("password"));
      user.setRegistrationDate(result
        .getDate("registration_date")
        .toString());
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return user;
  }

  /**
   * Возвращает true, если пользователь с указанным id является администратором
   * @param id        id пользователя
   */
  public boolean isAdmin(int id) {
    boolean userIsAdmin = false;

    try {
      String query = "SELECT * FROM `UserAdmin` WHERE `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, id);
      ResultSet result = preparedStatement.executeQuery();

      userIsAdmin = result.next();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return userIsAdmin;
  }

  /**
   * Обновляет данные о пользователе
   * @param user        объект пользователя
   */
  public void updateUser(User user) {
    try {
      String query = "UPDATE `User` SET `name` = ?, `email` = ? WHERE `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, user.getName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setInt(3, user.getId());

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Удаляет запись о пользователе в БД
   * @param userId    id удаляемого пользователя
   */
  public void deleteUser(int userId) {
    try {
      // Удаление из таблицы `User`
      String query = "DELETE FROM `User` WHERE `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, userId);
      preparedStatement.executeUpdate();

      // Удаление из таблицы `UserAdmin`
      if (!this.isAdmin(userId))
        return;

      query = "DELETE FROM `UserAdmin` WHERE `user_id` = ?";
      preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, userId);
      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
