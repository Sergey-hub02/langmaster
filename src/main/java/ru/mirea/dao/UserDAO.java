package ru.mirea.dao;

import org.springframework.stereotype.Component;
import ru.mirea.models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс для работы с БД
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
}
