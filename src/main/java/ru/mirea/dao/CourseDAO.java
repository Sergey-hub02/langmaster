package ru.mirea.dao;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.mirea.models.Course;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с БД с таблицей курсов
 */
@Component
public class CourseDAO {
  private static final String URL = "jdbc:mysql://localhost:3306/langmaster?useUnicode=true&characterEncoding=UTF-8";
  private static final String USERNAME = "ezh1k";
  private static final String PASSWORD = "alastor_cool";

  private static Connection conn;
  private ServletContext servletContext;

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
   * Возвращает объект изображения
   * @param imageName           название изображения
   */
  private MultipartFile createImage(String imageName) throws IOException {
    File imageFile = new File(this.servletContext.getRealPath("/")
      + "resources/images/"
      + imageName);
    FileInputStream imageStream = new FileInputStream(imageFile);

    return new MockMultipartFile("file", imageFile.getName(),
      "image/jpeg",
      IOUtils.toByteArray(imageStream));
  }

  /**
   * Внедрение зависимостей
   * @param servletContext      контекст приложения
   */
  @Autowired
  public CourseDAO(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  /**
   * Создаёт курс
   * @param authorId        id автора курса
   * @param course          объект создаваемого курса
   */
  public void createCourse(int authorId, Course course) {
    try {
      String query = "INSERT INTO `Course`(`title`, `description`, `user_id`, `image`) VALUES(?, ?, ?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, course.getTitle());
      preparedStatement.setString(2, course.getDescription());
      preparedStatement.setInt(3, authorId);
      preparedStatement.setString(4, course.getImage().getOriginalFilename());

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Добавляет курс в список проходимых курсов пользователя
   * @param userId        id пользователя, проходящего курс
   * @param courseId      id курса
   */
  public void assignCourse(int userId, int courseId) {
    try {
      String query = "INSERT INTO `UserCourse` VALUES(?, ?)";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, courseId);

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Находит курс по указанному id
   * @param courseId        id нужного курса
   */
  public Course getCourse(int courseId) {
    Course course = null;

    try {
      String query = "SELECT * FROM `Course` WHERE `course_id` = ?";

      PreparedStatement preparedStatement = conn.prepareStatement(query);
      preparedStatement.setInt(1, courseId);

      ResultSet result = preparedStatement.executeQuery();

      // Курс с указанным id не найден
      if (!result.next())
        return null;

      course = new Course();

      course.setId(result.getInt("course_id"));
      course.setTitle(result.getString("title"));
      course.setDescription(result.getString("description"));
      course.setImage(this.createImage(result.getString("image")));
    }
    catch (SQLException | IOException e) {
      e.printStackTrace();
    }

    return course;
  }

  /**
   * Возвращает список курсов, созданных пользователем с указанным id
   * @param authorId      id пользователя
   */
  public List<Course> getCreatedCourses(int authorId) {
    List<Course> courses = null;

    try {
      String query = "SELECT `course_id`, `title`, `description`, `image`" +
        "FROM `Course`" +
        "JOIN `User`" +
        "ON `Course`.`user_id` = `User`.`user_id`" +
        "WHERE `Course`.`user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, authorId);
      ResultSet result = preparedStatement.executeQuery();

      // Пользователь не создал ни одного курса
      if (!result.next())
        return null;

      courses = new ArrayList<>();

      do {
        Course course = new Course();

        course.setId(result.getInt("course_id"));
        course.setTitle(result.getString("title"));
        course.setDescription(result.getString("description"));
        course.setImage(this.createImage(result.getString("image")));

        courses.add(course);
      }
      while (result.next());
    }
    catch (SQLException | IOException e) {
      e.printStackTrace();
    }

    return courses;
  }

  /**
   * Возвращает список курсов, которые проходит пользователь с указанным id
   * @param userId        id пользователя
   */
  public List<Course> getCurrentCourses(int userId) {
    List<Course> courses = null;

    try {
      String query = "SELECT `course_id` FROM `UserCourse` WHERE `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, userId);
      ResultSet result = preparedStatement.executeQuery();

      // Пользователь с указанным id не проходит никаких курсов
      if (!result.next())
        return null;

      courses = new ArrayList<>();

      do {
        Course course = this.getCourse(result.getInt("course_id"));
        courses.add(course);
      }
      while (result.next());
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return courses;
  }

  /**
   * Возвращает список всех доступных курсов
   */
  public List<Course> getAllCourses() {
    List<Course> courses = null;

    try {
      Statement statement = conn.createStatement();
      ResultSet result = statement.executeQuery("SELECT * FROM `Course`");

      // В БД нет никаких курсов
      if (!result.next())
        return null;

      courses = new ArrayList<>();

      do {
        Course course = new Course();

        course.setId(result.getInt("course_id"));
        course.setTitle(result.getString("title"));
        course.setDescription(result.getString("description"));
        course.setImage(this.createImage(result.getString("image")));

        courses.add(course);
      }
      while (result.next());
    }
    catch (SQLException | IOException e) {
      e.printStackTrace();
    }

    return courses;
  }

  /**
   * Возвращает true, если пользователь с указанным id проходит указанный курс
   * @param userId          id пользователя
   * @param courseId        id курса
   */
  public boolean courseIsInUserList(int userId, int courseId) {
    boolean courseIsInList = false;

    try {
      String query = "SELECT * FROM `UserCourse` WHERE `user_id` = ? AND `course_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, courseId);

      ResultSet result = preparedStatement.executeQuery();
      courseIsInList = result.next();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return courseIsInList;
  }

  /**
   * Возвращает true, если указанный пользователь является создателем указанного курса
   * @param userId        id пользователя
   * @param courseId      id курса
   */
  public boolean userIsCourseCreator(int userId, int courseId) {
    boolean result = false;

    try {
      String query = "SELECT * FROM `Course` WHERE `course_id` = ? AND `user_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, courseId);
      preparedStatement.setInt(2, userId);

      ResultSet resultSet = preparedStatement.executeQuery();
      result = resultSet.next();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Изменяет курс
   * @param course      обновляемый курс
   */
  public void updateCourse(Course course) {
    try {
      String query = "UPDATE `Course` SET `title` = ?, `description` = ? WHERE `course_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setString(1, course.getTitle());
      preparedStatement.setString(2, course.getDescription());
      preparedStatement.setInt(3, course.getId());

      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Удаляет курс из его уроки из БД
   * @param courseId      id удаляемого курса
   */
  public void deleteCourse(int courseId) {
    try {
      // Удаление из таблицы `Course`
      String query = "DELETE FROM `Course` WHERE `course_id` = ?";
      PreparedStatement preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, courseId);
      preparedStatement.executeUpdate();

      // Удаление из таблицы `Lesson`
      query = "DELETE FROM `Lesson` WHERE `course_id` = ?";
      preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, courseId);
      preparedStatement.executeUpdate();

      // Удаление из таблицы `UserCourse`
      query = "DELETE FROM `UserCourse` WHERE `course_id` = ?";
      preparedStatement = conn.prepareStatement(query);

      preparedStatement.setInt(1, courseId);
      preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
