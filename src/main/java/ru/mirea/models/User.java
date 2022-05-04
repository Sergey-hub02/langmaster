package ru.mirea.models;

/**
 * Класс, представляющий пользователя приложения
 */
public class User {
  private int id;
  private String name;
  private String email;
  private String password;
  private String registrationDate;

  /**
   * Пустой конструктор
   */
  public User() {}

  /**
   * Возвращает id пользователя
   */
  public int getId() {
    return this.id;
  }

  /**
   * Устанавливает новое значение для id пользователя
   * @param id        новый id пользователя
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Возвращает имя пользователя
   */
  public String getName() {
    return this.name;
  }

  /**
   * Устанавливает новое значение для имени пользователя
   * @param name      новое имя пользователя
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Возвращает адрес электронной почты пользователя
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * Устанавливает новое значение для адреса электронной почты пользователя
   * @param email         новый адрес электронной почты пользователя
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Возвращает пароль пользователя
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Устанавливает новое значения для пароля пользователя
   * @param password        новый пароль пользователя
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Возвращает дату регистрации аккаунта
   */
  public String getRegistrationDate() {
    return this.registrationDate;
  }

  /**
   * Устанавливает новую дату регистрации аккаунта
   * @param registrationDate      новая дата регистрации аккаунта
   */
  public void setRegistrationDate(String registrationDate) {
    this.registrationDate = registrationDate;
  }

  /**
   * Выводит значение полей объекта
   */
  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", registrationDate='" + registrationDate + '\'' +
      '}';
  }
}
