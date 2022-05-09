package ru.mirea.models;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public class Course {
  private int id;

  @NotEmpty(message = "Обязательное поле!")
  private String title;

  @NotEmpty(message = "Обязательное поле!")
  private String description;

  private MultipartFile image;

  /**
   * Пустой конструктор
   */
  public Course() {}

  /**
   * Возвращает id курса
   */
  public int getId() {
    return this.id;
  }

  /**
   * Устанавливает новое значение для id курса
   * @param id        новый id курса
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Возвращает название курса
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Устанавливает новое название курса
   * @param title         название курса
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Возвращает описание курса
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Устанавливает новое описание курса
   * @param description         описание курса
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Возвращает название картинки для курса
   */
  public MultipartFile getImage() {
    return this.image;
  }

  /**
   * Устанавливает новое название для картинки курса
   * @param image         название изображения для курса
   */
  public void setImage(MultipartFile image) {
    this.image = image;
  }
}
