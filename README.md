Compose Multiplatform client for [mygame-backend repository](https://github.com/Cheboksary/ru.mygame.mygame-backend).

This is a Kotlin Multiplatform project targeting Android, Web.

# Описание игры
Один ведущий, один врун, от 2 до 6 добряков и 4 раунда.

***Каждый раунд***

*В первую фазу:*
**Ведущий** объясняет выданное игрой случайное слово с какими-либо ограничениями (например не используя существительные).

*Во вторую фазу:*
**Игроки** вписывают ответы. Случайно выбранный игрой **врун** знает слово **ведущего** и должен отвести **добряков** от верного ответа не раскрыв себя.

- За раунд **добряки** получат 1 балл если дадут одинаковый *верный* ответ.

- **Врун** получит 1 балл если **добряки** и **врун** дадут одинаковый *неверный* ответ.

- **Никто** не получит балл если **добряки** дадут разные ответы.

***В конце игры***

Игра объявляет победителя(ей) и раскрывает вруна. Игроки и ведущий могут посмотреть счет и ответы всех игроков в каждом раунде.

>[!NOTE]
> Во вторую фазу любой игрок может инициировать открытое голосование против игрока, которого считает вруном.
> Игра продолжается если голосование не состоялось.
> Добряки проигрывают ***с позором*** если голосование состоялось и игроки ошиблись, в противном случае ***с позором*** (но по-доброму) проигрывает врун.
>
> Это еще не реализовано в приложении, поэтому игроки могут голосовать любым удобным им способом. Если они признают голосование состоявшимся - врун честно раскрывается. Лобби придется пересоздать.

# Как поиграть

Веб-версия приложения доступна на [бесплатном хостинге](https://mygame-kotlin-wasm.onrender.com/) и корректно работает только в ***десктопных версиях браузеров***

>[!NOTE]
> Сервер игры тоже развернут на бесплатном хостинге и приостанавливается при отсутствии активности, если вы получаете `timeout cancellation` при создании лобби - попробуйте еще раз.
>
> Кроме того, убедиться что сервер отвечает можно, если вы видите `Hello world!` по этой [ссылке](https://ru-mygame-mygame-backend.onrender.com/).

Игрок создавший лобби - ведущий. Лобби получает уникальный код по которому подключаются игроки.

>[!IMPORTANT]
> В текущей версии приложения отсутствует полноценная библиотека загадываемых слов (сейчас их 6).
