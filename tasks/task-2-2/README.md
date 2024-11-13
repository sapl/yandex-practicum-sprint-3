# Задание 2.2. Новые микросервисы и интеграция с монолитом

> 1. Выберите язык программирования.

Kotlin+Spring Boot+Gradle

> 2. Создайте проект.

В реальном проекте сервисы будут каждый в своем репозитории
В задании новые сервисы положил в папку `services`

> 3. Реализуйте логику микросервисов.

См. мое [описание в Задании 1 ](../task-1-2/README.md) про назначение сервисов.
Сервис `sh-device-core` является частью условного IoT Hub.
В качестве MQTT-брокера сообщений используется Mosquitto. В реальном проекте
можно как целиком работать только через MQTT/Mosquitto, настроив нужные топики и маршрутизацию так и подключить
дополнительно
Kafka если требуется более сложная настройка потоков данных дальше между серверами.

> 4-5. Выберите и разверните СУБД, настройте интеграцию.

- Основная база всех сервисов: `MongoDb`.  
  Состояния, телеметрия, конфигурация устройств могут иметь произвольные поля потому нужна гибкость.
- Для сервиса телеметрии `sh-telemetry` выбрана база  `ClickHouse`.  
  Подходит для хранения большого количества данных во времени, аналитики (например посчитать средний расход воды
  помесячно)
  Логи с устройств также можно хранить в ней.

> 6-7. Настройте интеграцию с монолитом. Взаимодействие через REST API:

- Интеграция монолита с новыми сервисами реализована в классе
  [ACLServiceImpl](../../smart-home-monolith/src/main/java/ru/yandex/practicum/smarthome/service/ACLServiceImpl.java)
- Для эмуляции Legacy TCP-коннектора с текущими устройствами систем отопления
  реализован
  класс [HeatingSystemTCPConnector](../../smart-home-monolith/src/main/java/ru/yandex/practicum/smarthome/service/HeatingSystemTCPConnector.java)
- Миграция пройдет/прошла бы например так:
    - Мы подняли новые сервисы, IoT-хаб, с MQTT каналом коммуникации с устройствами
    - Новые устройства сразу работают напрямую с этим хабом, старые обновляют прошивку (переключаются поэтапно на MQTT)
    - Монолит перестает у себя хранить состояния и телеметрию
    - Вместо этого он вызывает соотвествующие API методы в новых микросервисах
      (получение статуса, телеметрии, отправки команд)
    - Какое-то время устройства со старой прошивкой работают через старый TCP-коннектор с монолитом,
      Получаемые сообщения пересылаются через ACL в нужный топик MQTT.
      И наоборот команды на изменение состояния проходят через `sh-device-core` -> `command topic` -> `Legacy Connector`

> 9. Взаимодействие с монолитом через Kafka:

- Вместо Kafka используется MQTT брокер Mosquitto
- Старый монолит работает через ACL сервис с топиками
- Если приходит сообщение обновления состояния (в топик `devices/{id}/command`)
  то Legacy Connector отправляет это командой на устройство
- Если от устройства в коннектор приходит телеметрия
  то ACL отправляет сообщение в топик `devices/{id}/telemetry`
- Связка числового ID Heating System и строкового Device ID в IoT Hub хранится в базе миграции

### Разворачивание стенда на Docker Compose

Разверните сервисы и базы через Compose, выполнив в корне:

```shell
 docker-compose up -d
```

### Тестирование API

1. Проверить, что сервисы подняты:  
   [sh-users](http://localhost:8091/actuator/info), [sh-device-core](http://localhost:8092/actuator/info), [sh-telemetry](http://localhost:8093/actuator/info)


2. Подписаться на MQTT топики чтобы видеть, что ходит по MQTT:

```shell
docker exec mosquitto mosquitto_sub -h localhost -t "devices/+/#" -v
```

3. Отправить команду включения системы обогрева на 24 градуса:

```shell
curl -XPOST "http://localhost:8092/api/v1/devices/aaa-bbb-ccc-1/state" \
    -H "Content-Type: application/json" \
    -d '{ "isOn": true, "targetTemperature": 24 }'
```
Выключить через старый монолит
```shell
curl -XPOST "http://localhost:8090/api/heating/1/turn-off" 
```

4. Проверить статус (для эмуляции устройство принимает команду через `10 секунд`,
   отправляет обновление в топик `devices/{id}/state`):  
   http://localhost:8092/api/v1/devices/aaa-bbb-ccc-1/state
   Проверить состояние через старый монолит:
   http://localhost:8090/api/heating/1


5. Получить телеметрию с датчика температуры (для эмуляции отправляется раз в 30 секунд в топик):  
   http://localhost:8093/api/v1/devices/aaa-bbb-ccc-1/telemetry?sensorName=temperature&pageSize=5&timeFrom=2024-11-12T11:44:26Z

6. Второе устройство (умная лампочка) отправило изменение состояния - включено:
```shell
 docker exec mosquitto mosquitto_pub -h localhost -t "devices/aaa-bbb-ccc-2/state" -m '{"isOn": true, "brightness": 75}'
```
Бакенд принял и обновил состояние:
http://localhost:8092/api/v1/devices/aaa-bbb-ccc-2/state

> Здесь все примеры запросов: [New Services](../../http/services.http), [Old Monolith](../../http/monolith.http)

### 8-13. Развёртывание в Minikube

Запуск кластера minikube:

```bash
minikube start --memory=4096 --cpus=4
```

Загрузить docker-образы в minikube, выполнить в корне:

```curl 
./k8s/cp-images-to-minikube.sh
```

Развернуть сервисы в кластере через Helm-чарт:

```shell
helm install smart-home-services ./k8s/helm
```

Подождать пока все поднимется, проверить логи:

```shell
kubectl get pod
kubectl logs -l app=sh-telemetry --all-containers=true -f
```

Пробросить порт сервиса телеметрии

```shell
kubectl port-forward svc/sh-telemetry 8083:8080
```

По ссылке будет лог телеметрии c первого устройства:  
http://localhost:8083/api/v1/devices/aaa-bbb-ccc-1/telemetry?sensorName=temperature&pageSize=5&timeFrom=2024-11-12T11:44:26Z

> **Задание 2.2. Дополнительная часть**

Нет уж, спасибо.