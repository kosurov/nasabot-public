# Телеграм бот NASA Daily Content

## Описание:
Телеграм бот можно попробовать по ссылке [https://t.me/NASADailyContentBot](https://t.me/NASADailyContentBot). Если не работает, то надо его немного пошевелить - [нажать сюда](https://nasadailybot.herokuapp.com/). 
По запросу может прислать картинку или ссылку на видео с сайта NASA с описанием. Может перевести описание на русский язык. 

## Что реализовано
- Работа [Чтение данныех API NASA](https://github.com/kosurov/nasa-api.git) переложена на бот (Java, Spring Boot)
- Бот высылает картинку или ссылку на видео с описанием
- Описание можно перевести на русский язык. Используется `Yandex Translation API`
- Токен для `Yandex Translation API` генерируется автоматически
- Т.к. проект выложен на `heroku`, предусмотрена имитация активности бота, чтобы он не отключался через 30 мин
- Общение с ботом: сначала любое сообщение, затем через клавиатуру
