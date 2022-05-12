package ru.mirea.dao;

import org.springframework.stereotype.Component;
import ru.mirea.models.Lesson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с БД с таблицей уроков
 */
@Component
public class LessonDAO {
  private static final String URL = "jdbc:mysql://localhost:3306/langmaster?useUnicode=true&characterEncoding=UTF-8";
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
   * Создаёт урок для указанного курса
   * @param courseId        id курса
   */
  public void createLesson(int courseId, Lesson lesson) {
    try {
      String query = "INSERT INTO `Lesson`(`title`, `content`, `course_id`) VALUES(?, ?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, lesson.getTitle());
      preparedStatement.setString(2, lesson.getContent());
      preparedStatement.setInt(3, courseId);

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Возвращает урок по его id
   * @param lessonId      id урока
   */
  public Lesson getLesson(int lessonId) {
    Lesson lesson = null;

    try {
      String query = "SELECT * FROM `Lesson` WHERE `lesson_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, lessonId);
      ResultSet result = preparedStatement.executeQuery();

      // Урока с указанным id не существует
      if (!result.next())
        return null;

      lesson = new Lesson();

      lesson.setId(result.getInt("lesson_id"));
      lesson.setTitle(result.getString("title"));
      lesson.setContent(result.getString("content"));
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return lesson;
  }

  /**
   * Возвращает список всех уроков, принадлежащих указанному курсу
   * @param courseId        id курса
   */
  public List<Lesson> getCourseLessons(int courseId) {
    List<Lesson> courseLessons = null;

    try {
      String query = "SELECT * FROM `Lesson` WHERE `course_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, courseId);
      ResultSet result = preparedStatement.executeQuery();

      // Для указанного курса нет уроков
      if (!result.next())
        return null;

      courseLessons = new ArrayList<>();

      do {
        Lesson lesson = this.getLesson(result.getInt("lesson_id"));
        courseLessons.add(lesson);
      }
      while (result.next());
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return courseLessons;
  }

  /**
   * Обновляет содержимое урока
   * @param lesson      обновляемый урок
   */
  public void updateLesson(Lesson lesson) {
    try {
      String query = "UPDATE `Lesson` SET `title` = ?, `content` = ? WHERE `lesson_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, lesson.getTitle());
      preparedStatement.setString(2, lesson.getContent());
      preparedStatement.setInt(3, lesson.getId());

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Удаляет урок из курса
   * @param lessonId        id удаляемого урока
   */
  public void deleteLesson(int lessonId) {
    try {
      String query = "DELETE FROM `Lesson` WHERE `lesson_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, lessonId);
      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
