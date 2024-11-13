# Задание 2.3. Подготовка 3rd party сервисов для связи микросервисов


> В качестве API Gateway вы можете использовать, например, Kusk Gateway.

Не получилось с ним подружиться тк не нашел нормальных описаний.
Потому использовал **Kong Gateway**.

### Через Docker Compose

Выполнить в корне:
```shell
 docker-compose up -d
```

- Kong будет поднят с следующим конфигом: **[kong.yml](../../kong.yml)**  
  Это `declarative` режим с выключенной базой (конфиг из yaml).


- Для примера настроен rate limit (не более 1 запроса в секунду)


- Сервисы проксируются по следующим путям:
  - [sh-device-core](http://localhost:8000/d/actuator/info): `/d`
  - [sh-telemetry](http://localhost:8000/t/actuator/info): `/t`
  - [sh-users](http://localhost:8000/u/actuator/info): `/d`
  - [sh-monolith](http://localhost:8000/m/api/heating/1): `/m`

### Через Helm в Minikube 

Выполнить в корне:

```bash
minikube start --memory=4096 --cpus=4
```

Загрузить образы в minikube:
```shell
./k8s/cp-images-to-minikube.sh
```

```shell
helm install smart-home-services ./k8s/helm
```
Установка Kong (команды с сайта документации)
```shell
helm repo add kong https://charts.konghq.com
helm repo update
kubectl create namespace kong
kubectl create secret generic kong-enterprise-license --from-literal=license="'{}'" -n kong
openssl req -new -x509 -nodes -newkey ec:<(openssl ecparam -name secp384r1) -keyout ./tls.key -out ./tls.crt -days 1095 -subj "/CN=kong_clustering"
kubectl create secret tls kong-cluster-cert --cert=./tls.crt --key=./tls.key -n kong
helm install kong-cp kong/kong -n kong --values ./k8s/helm/values-cp.yaml 
helm install kong-dp kong/kong -n kong --values ./k8s/helm/values-dp.yaml
```
Проверка подов:
```shell
kubectl get pod -n kong 
```

Пробросить порт Admin API:
```shell
kubectl port-forward -n kong service/kong-cp-kong-admin 8001 
```

Настроить роутинг для sh-device-core:
```shell
 curl -i -X POST \
  --url http://localhost:8001/services/ \
  --data 'name=sh-device-core' \
  --data 'url=http://sh-device-core.default.svc.cluster.local:8080'
```

Запустить тонель, чтобы заработал LoadBalancer на localhost:
```shell
minikube tunnel
```

Проверить работу Kong роута:
http://localhost/dc/actuator/info
http://localhost/dc/api/v1/devices/aaa-bbb-ccc-1/state

> PS. В реальном проекте думаю не надо использовать конфиг в базе, API настроек и прочие админки. 
> Конфигурировать всегда через yaml в коде.


###  Helm charts для Kafka и API Gateway  

Kafka как описано ранее мы не используем. Используется MQTT Moskuitto
Он и другие сервисы подняты через Helm и работают в кубе.
Это можно проверить по логам:

```shell
 kubectl logs -l app=sh-telemetry --all-containers=true -f
```
