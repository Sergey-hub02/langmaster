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
  public List<Course> getCourses(int authorId) {
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
}
