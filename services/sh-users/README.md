# User Management Service

Отвечает за регистрацию пользователей, настройки аккаунта, хранит сущности 'Дом' (location)
пользователя к которому привязываются устройства и таким образом разделяются права доступа.

```env

DB_CONNECTION_URL=mongodb://localhost:27017/sh_users
```

### Ссылки

- Health check:
  http://localhost:8080/actuator/health

- Swagger Doc:
  http://localhost:8081/swagger-ui/index.html#

- Open API yaml:
  [openapi.yaml](doc/openapi.yaml)