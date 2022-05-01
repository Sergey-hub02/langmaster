/*=================================
  КОД ДЛЯ ОТОБРАЖЕНИЯ БУРГЕР-МЕНЮ И
  ГЕНЕРАЦИИ ТЕКСТА В ПОДВАЛЕ САЙТА
=================================*/
"use strict"

/*========== БУРГЕР-МЕНЮ ==========*/
document
  .querySelector("#burger")
  .addEventListener("click", () => {
    document.querySelector("#nav").classList.toggle("active");
  });


/*========== ГЕНЕРАЦИЯ ТЕКСТА В ПОДВАЛЕ САЙТА ===========*/
document
  .querySelector("#footer-text").innerHTML = `&copy; Все права защищены ${new Date().getFullYear()}`;
