# Задание 1.1

### Текущая архитектура

**[Context-Diagram.puml](initial-context-diagram.puml)** 
| [SVG](https://plantuml.etservice.net/svg/jLDRInjH47xFhnZtAO5SiFJ91w7LLYqAeOtuKf2Jv3HPcjqDkoTLaO9HUe5QHUXJAKY5FlKbMhTTpKNm5yp-eyxCRe9QNnl2xjcPxynytrocRBVMwjGrD8rf1pEbagysYWtRD3lLJg3zckSQxPfYpNDAxPPoJSTf5G9JRzdLqjp3GfbDgolB9KS5HljaMf_Z6vkcs2PaPkf_H5Usg_qaVkQnrt7hoj_AShWWRrcGsywKwUYvOig3XSSumdwyZt_eVOQzU1T3x6DavSP0LNTibv4d6CRlS8WHNeh_RUP61Y9eNdc-Gy3hdtWI7-4LXVs45u3d57bmtGUBWtp5KRn3FdvoieYI3kbq276Nuk_6-tcOBZn8ldR8oS-UbOFj3D1leZ2xeQiKRqrNONM98ozMLfQ9r6rGLTLUQxVEYO_YpyGxf6Jd37-ctYWXz8tO3Ra6616TOKBv57jZS17mExbFoKr4c2AaDGfn88nto7MGXtWFhmHoHi8F1SWw_oqkEGSO0Vr9P1HtnJeWn0N4xvFgKtAgsLqvyzLiNHbLLO4cvlFfaLMKlS0qV1s8eX_2OiXw9CazRDbEVM4KAmvPSKGiXmJf0g57-6lIP-P0rOR2BAJrd875IgKCZyfBuaoj31Zpu-6Op4RJSvWmVc5HL8MFbFJmHiUPzPhzr2ujpArOkJkDNd-oQR9DhOpjDbviqQJpY2qctozT2CIKiapySgEXZDvGMuHTI4vtYoceGBk1nsjGqKvR-yfqVCr1nGpJwPrRsGkpQMYP_-UwbMvICezuJ0KUfMCRiJXMEP8cZwZnl3rX7WXmYVqIdb7nwG3a8p-lZ0mTOIWrnqwNBmaEP1G356U1Ype3ihxd72C-Y3-CBqjxUQkwqd2QB4Bqn0Go1Z9pac1IfnkhSYpiGnuGYjcxPu8m8WqZ_4sdP8eawMNIt_6AqQxtn7PHO7KWwv48PMlVolq5)

Диаграмма уровня системы текущей архитектуры платформы «Тёплый дом».

- В основе системы монолитный бакенд на Java/Spring boot.
- База PostgreSQL хранит как состояние систем, так и телеметрию по датчикам, пользователей и тд
- Из задания не понятно как именно устройства подключены к системе.
  Могу предположить, что раз есть проблемы с подключением новых датчиков, то используется
  какой-то проприетарный протокол подключения датчиков к gateway-модулю и сам модуль возможно работает
  с центральным бакендом через какой-то самописный TCP/HTTP протокол.

### Текущие проблемы

Бизнес хочет функциональное расширение (много различных датчиков и устройств).

- Нагрузка может вырасти в десятки раз, текущее решение маштабируется только вертикально
- Для гибкого подключения новых датчиков и устройств нужно пересматривать архитектуру
  взаимодействия платформы и точек подключения в умных домах
- Для управления и мониторинга большим числом устройств потребуется разделить логику
  на модули/домены для более гибкой/быстрой разработки функционала.
- Текущая система согласно заданию работает синхронно (хотя не совсем понял что это значит
  в контексте IoT), нужно предусмотреть асинхронное взаимодействие компонентов.
- Текущая система деплоится целиком, что не позволит быстро и безопасно обновлять ее по частям.

### План развития

Описан в [Части 1.2](../task-1-2/README.md)