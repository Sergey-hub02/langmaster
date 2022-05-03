package ru.mirea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mirea.dao.UserDAO;
import ru.mirea.models.User;

import java.time.LocalDate;

/**
 * Обработчик HTTP-запросов приложения
 */
@Controller
@RequestMapping("/langmaster")
public class LangMasterController {
  private UserDAO userDAO;

  /**
   * Внедрение зависимости DAO
   * @param userDAO       объект, позволяющий общаться с БД
   */
  @Autowired
  public LangMasterController(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * Отображает главную страницу при GET-запросе
   * на адрес /langmaster
   */
  @GetMapping()
  public String displayIndexPage() {
    return "index";
  }

  /**
   * Отображает страницу профиля пользователя при
   * GET-запросе на адрес /langmaster/profile
   */
  @GetMapping("/profile")
  public String displayProfilePage() {
    return "pages/profile";
  }

  /**
   * Отображает страницу регистрации при
   * GET-запросе на адрес /langmaster/register
   */
  @GetMapping("/register")
  public String displayRegisterPage() {
    return "pages/register";
  }

  /**
   * Забирает данные из формы регистрации пользователя
   * и добавляет запись о нём в БД
   * @param name            имя пользователя
   * @param birthDate       дата рождения
   * @param email           адрес электронной почты
   * @param password        пароль
   */
  @PostMapping("/register")
  public String processRegister(@RequestParam("name") String name,
                                @RequestParam("birthDate") String birthDate,
                                @RequestParam("email") String email,
                                @RequestParam("password") String password) {
    User user = new User();

    user.setName(name);
    user.setBirthDate(LocalDate.parse(birthDate));
    user.setEmail(email);
    user.setPassword(password);

    this.userDAO.registerUser(user);
    return "index";
  }

  /**
   * Отображает страницу авторизации при
   * GET-запросе на адрес /langmaster/login
   */
  @GetMapping("/login")
  public String displayLoginPage() {
    return "pages/login";
  }

  /**
   * Отображает страницу курса при
   * GET-запросе на адрес /langmaster/course
   */
  // TODO: добавить @PathVariable для id курса
  @GetMapping("/course")
  public String displayCoursePage() {
    return "pages/course";
  }

  /**
   * Отображает страницу уроков курса при
   * GET-запросе на адрес /langmaster/course/lesson
   */
  // TODO: добавить @PathVariable для id урока
  @GetMapping("/course/lesson")
  public String displayLessonsPage() {
    return "pages/lessons";
  }
}
