package ru.mirea.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CourseDAO {
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
}
