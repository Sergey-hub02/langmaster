package ru.mirea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.mirea.dao.CourseDAO;
import ru.mirea.dao.UserDAO;
import ru.mirea.models.Course;
import ru.mirea.models.User;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * Обработчик HTTP-запросов приложения
 */
@Controller
@RequestMapping("/langmaster")
public class LangMasterController {
  private User user = null;

  private UserDAO userDAO;
  private CourseDAO courseDAO;
  private ServletContext servletContext;

  /* Объект, позволяющий хэшировать пароли */
  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Внедрение зависимостей
   * @param userDAO                   объект, позволяющий работать с пользователями
   * @param courseDAO                 объект, позволяющий работать с курсами
   * @param servletContext            контекст приложения
   */
  @Autowired
  public LangMasterController(UserDAO userDAO, CourseDAO courseDAO, ServletContext servletContext) {
    this.userDAO = userDAO;
    this.courseDAO = courseDAO;
    this.servletContext = servletContext;
  }

  /**
   * Отображает главную страницу при GET-запросе
   * на адрес /langmaster
   */
  @GetMapping()
  public String displayIndexPage(Model model) {
    model.addAttribute("user", this.user);

    // Если пользователь авторизован, то проверяем, является ли он админом
    if (this.user != null) {
      final int userId = this.user.getId();

      model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
      model.addAttribute("currentCourses", this.courseDAO.getCurrentCourses(userId));
    }

    model.addAttribute("allCourses", this.courseDAO.getAllCourses());
    return "index";
  }

  /**
   * Отображает страницу профиля пользователя при
   * GET-запросе на адрес /langmaster/profile
   */
  @GetMapping("/profile/{name}")
  public String displayProfilePage(@PathVariable("name") String name, Model model) {
    final int userId = this.user.getId();
    model.addAttribute("user", this.userDAO.getUser(name));

    if (this.user != null) {
      model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
      model.addAttribute("createdCourses", this.courseDAO.getCreatedCourses(userId));
    }

    model.addAttribute("currentCourses", this.courseDAO.getCurrentCourses(userId));
    return "pages/profile";
  }

  /**
   * Отображает страницу регистрации при
   * GET-запросе на адрес /langmaster/register
   */
  @GetMapping("/register")
  public String displayRegisterPage(Model model) {
    model.addAttribute("user", new User());
    return "pages/register";
  }

  /**
   * Забирает данные из формы регистрации пользователя
   * и добавляет запись о нём в БД
   */
  @PostMapping("/register")
  public String processRegister(@ModelAttribute("user") @Valid User user,
                                BindingResult bindingResult) {
    // Отображение ошибок под полями ввода
    if (bindingResult.hasErrors())
      return "pages/register";

    /* Проверка на существование пользователя с указанным именем */
    this.user = this.userDAO.getUser(user.getName());

    if (this.user != null) {
      // Отображение ошибки уникальности имени пользователя
      bindingResult.rejectValue("name",
        "error.user",
        "Имя " + user.getName() + " уже занято!");
      return "pages/register";
    }

    /* Добавление пользователя в БД и авторизация */
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    this.userDAO.registerUser(user);
    this.user = this.userDAO.getUser(user.getName());

    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу авторизации при
   * GET-запросе на адрес /langmaster/login
   */
  @GetMapping("/login")
  public String displayLoginPage(Model model) {
    // Здесь используется ненастоящий адрес электронной почты, чтобы не возникла ошибка
    User user = new User();
    user.setEmail("placeholder@mail.com");

    model.addAttribute("user", user);
    return "pages/login";
  }

  /**
   * Получает данные из формы и выполняет авторизацию пользователя
   */
  @PostMapping("/login")
  public String processLogin(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult) {
    // Отображение ошибок под полями ввода
    if (bindingResult.hasErrors())
      return "pages/login";

    User dbUser = this.userDAO.getUser(user.getName());

    // Незакодированный пароль
    String userPassword = user.getPassword();

    // Сравнение введённого пароля с закодированным
    if (dbUser != null
      && passwordEncoder.matches(userPassword, dbUser.getPassword())) {
      this.user = dbUser;
      return "redirect:/langmaster";
    }

    // Отображение ошибки с логином или паролем
    bindingResult.rejectValue("password",
      "error.user",
      "Неправильный логин или пароль!");
    return "pages/login";
  }

  /**
   * Выполняет выход из аккаунта
   */
  @PostMapping("/logout")
  public String processLogout() {
    this.user = null;
    return "redirect:/langmaster";
  }

  /**
   * Отображает страницу курса при
   * GET-запросе на адрес /langmaster/course
   */
  @GetMapping("/course/{courseId}")
  public String displayCoursePage(@PathVariable("courseId") int courseId, Model model) {
    final int userId = this.user.getId();
    model.addAttribute("user", this.user);

    if (this.user != null) {
      model.addAttribute("userIsAdmin", this.userDAO.isAdmin(userId));
    }

    model.addAttribute("course", this.courseDAO.getCourse(courseId));
    return "pages/course";
  }

  /**
   * Отображает страницу создания курса
   * @param model         модель
   */
  @GetMapping("/course/new")
  public String displayCourseCreationPage(Model model) {
    if (this.user == null)
      return "redirect:/langmaster/login";

    if (!this.userDAO.isAdmin(this.user.getId()))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    model.addAttribute("user", this.user);
    model.addAttribute("course", new Course());

    return "pages/courseCreation";
  }

  /**
   * Обрабатывает данные из формы и создаёт курс
   * @param course              объект курса
   * @param bindingResult       результат обработки полей формы
   */
  @PostMapping("/course/new")
  public String processCourseCreation(@ModelAttribute("course") @Valid Course course,
                                      BindingResult bindingResult) {
    if (bindingResult.hasErrors())
      return "pages/courseCreation";

    // Создание изображения
    MultipartFile courseImage = course.getImage();

    if (courseImage != null || !courseImage.isEmpty()) {
      // Генерация пути и названия картинки
      String imageName = this.servletContext.getRealPath("/")
        + "resources/images/"
        + courseImage.getOriginalFilename();

      try {
        // Загрузка картинки на сервер
        courseImage.transferTo(new File(imageName));
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    this.courseDAO.createCourse(this.user.getId(), course);
    return "redirect:/langmaster";
  }

  @PostMapping("/course/{courseId}/assign")
  public String processAssignCourse(@PathVariable("courseId") int courseId) {
    // Страницу нельзя отобразить, если пользователь не авторизован
    if (this.user == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Невозможно найти ресурс!");

    this.courseDAO.assignCourse(this.user.getId(), courseId);
    return "redirect:/langmaster";
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
