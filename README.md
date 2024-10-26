Compose Multiplatform client for [mygame-backend repository](https://github.com/Cheboksary/ru.mygame.mygame-backend).

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

***
This is a Kotlin Multiplatform project targeting Android, Web.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

**Note:** Compose/Web is Experimental and may be changed at any time. Use it only for evaluation purposes.
We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.
