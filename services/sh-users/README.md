# User Management Service

Отвечает за регистрацию пользователей, настройки аккаунта, хранит сущности 'Дом' (location)
пользователя, к которому привязываются устройства и таким образом разделяются права доступа.

```env

DB_CONNECTION_URL=mongodb://localhost:27017/sh_users
```

### Ссылки

- Health check:
  http://localhost:8080/actuator/health

- Open API Doc UI:
  http://localhost:8080/redoc.html

- Open API Yaml:
  http://localhost:8080/v3/api-docs.yaml