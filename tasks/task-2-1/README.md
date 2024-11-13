# Задание 2.1. Создание и документирование API

Документация Open API генерируется из-кода, через аннотации.
Это позволяет держать ее актуальной и не тратить время на разбирательстся с форматом openapi.yaml  
При этом на этапе черновика/ТЗ обычно описываю будущие API просто в Markdown или Confluense.

- Разверите сервисы через Compose, выполнив в корне:
```shell
 docker-compose up -d
```
- Сервисы будут доступны на своих портах:
    - [sh-users](http://localhost:8091/actuator/info) (8091)
    - [sh-device-core](http://localhost:8092/actuator/info) (8092)
    - [sh-telemetry](http://localhost:8093/actuator/info) (8093)
- Посмотреть документацию OpenAPI можно по ссылкам:
    - sh-users: [UI](http://localhost:8091/redoc.html) |  [Yaml](http://localhost:8091/v3/api-docs)
    - sh-device-core: [UI](http://localhost:8092/redoc.html) |  [Yaml](http://localhost:8092/v3/api-docs)
    - sh-telemetry: [UI](http://localhost:8093/redoc.html) |  [Yaml](http://localhost:8093/v3/api-docs)
