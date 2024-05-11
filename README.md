## **Технологии, использованные в Rangiffler**

- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
- [Spring OAuth 2.0 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web)
- [Spring actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Spring gRPC by https://github.com/yidongnan](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html)
- [Spring web-services](https://docs.spring.io/spring-ws/docs/current/reference/html/)
- [Apache Kafka](https://developer.confluent.io/quickstart/kafka-docker/)
- [Docker](https://www.docker.com/resources/what-container/)
- [Docker-compose](https://docs.docker.com/compose/)
- [Postgres](https://www.postgresql.org/about/)
- [React](https://ru.reactjs.org/docs/getting-started.html)
- [GraphQL](https://graphql.org/)
- [Thymeleaf](https://www.thymeleaf.org/)
- [Jakarta Bean Validation](https://beanvalidation.org/)
- [JUnit 5 (Extensions, Resolvers, etc)](https://junit.org/junit5/docs/current/user-guide/)
- [Retrofit 2](https://square.github.io/retrofit/)
- [Allure](https://docs.qameta.io/allure/)
- [Selenide](https://selenide.org/)
- [Selenoid & Selenoid-UI](https://aerokube.com/selenoid/latest/)
- [Allure-docker-service](https://github.com/fescobar/allure-docker-service)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Gradle 7.6](https://docs.gradle.org/7.6/release-notes.html)
- And much more:)

# Rangiffler
Ты попал на страницу дипломного проекта QA.GURU Advanced.

Для начала описание проекта:  
**Rangiffler** - произошло от названия северных оленей - Rangifer. Этот проект про путешествия, а северный олень - рекордсмен по
преодолеваемым расстояниям на суше. Ты можешь делиться фотками, ставить лайки, 
добавлять друзей, все это реализовано в этом проекте.  
Проект реализован на микросервисной архитектуре.  
Кратко о структуре проекта:  
- **rangiffler-gql-client** - фронтенд
- **rangiffler-auth** - сервис, отвечающий за авторизацию пользователей;  
- **rangiffler-gateway** - сервис, являющийся входной точкой в проект, который общается со всеми сервисами, также, 
            является аггрегатором, т.е. собирает все необходимые данные для отправки на frontend  
- **rangiffler-userdata** - сервис, отвечающий за взаимодейтсвие между пользователями (отправка запросов на дружбу, прием заявок на дружбу, отклонение)  
- **rangiffler-geo** - сервис справочник, хранит данные о стране (код, флаг) и предоставляет эти данные по необходимости  
- **rangiffler-photo** - сервис, отвечающий за добавление фото, проставление лайков к фото друзей  
- **rangiffler-e-2-e-tests** - проект с интеграционными тестами
- **rangiffler-grpc-common** - проект с proto файлами и сгенерированными протокопмилятором классами

Ниже приведена диаграмма сервисов, важно, сервисы не общаются между собой, все взаимодействие происходит через аггрегатор в gateway.

<img src="диаграмма сервисов.png" width="800">

### Запуск проекта
#### 1. Установить docker (Если не установлен)

Мы будем использовать docker для БД (Postgres), кроме того, будем запускать микросервисы в едином docker network при
помощи docker-compose

[Установка на Windows](https://docs.docker.com/desktop/install/windows-install/)

[Установка на Mac](https://docs.docker.com/desktop/install/mac-install/) (Для ARM и Intel разные пакеты)

[Установка на Linux](https://docs.docker.com/desktop/install/linux-install/)

После установки и запуска docker daemon необходимо убедиться в работе команд docker, например `docker -v`:


#### 3. Установить Java версии 17 или новее. Это необходимо, т.к. проект не поддерживает версии <17

Версию установленной Java необходимо проверить командой `java -version`

```posh
java -version
openjdk version "19.0.1" 2022-10-18
OpenJDK Runtime Environment Homebrew (build 19.0.1)
```

Если у вас несколько версий Java одновременно - то хотя бы одна из них должна быть 17+

#### 4. Запустить проект

Перейти в папку с проектом и выполнить скрипт:

```posh
rangiffler % bash localenv.sh
```
Описание команд в скрипте:

```posh
echo '### Stop and remove containers ###'
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
```
Останавливает и удаляет все существующие контейнеры
___

```posh
echo "### Pull containers ###"
# Пулл контейнеров
docker pull mysql:8.0.33
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2
```
Пуллит необходимые контейнеры для работы проета:  
1. База данных Mysql
2. Zookeeper
3. Kafka
___

```posh
echo '### Create volume ###'
if ! docker volume inspect mysqldata &> /dev/null; then
    docker volume create mysqldata
fi
```
Создает необходимый volume, если отсутствует
___

```posh
echo '### Docker run containers ###'
docker run --name rangiffler-all -p 3306:3306 -e MYSQL_ROOT_PASSWORD=secret -v mysqldata:/var/lib/mysql -d mysql:8.0.33
docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2
docker run --name=kafka -e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format='{{ .NetworkSettings.IPAddress }}'):2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
-p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```
Запускает необходимые контейнеры
___

```posh
echo "### Create databases ###"
sleep 10
docker exec -i rangiffler-all mysql -uroot -psecret -e "CREATE DATABASE IF NOT EXISTS rangiffler_auth;"
docker exec -i rangiffler-all mysql -uroot -psecret -e "CREATE DATABASE IF NOT EXISTS rangiffler_geo;"
docker exec -i rangiffler-all mysql -uroot -psecret -e "CREATE DATABASE IF NOT EXISTS rangiffler_photo;"
docker exec -i rangiffler-all mysql -uroot -psecret -e "CREATE DATABASE IF NOT EXISTS rangiffler_userdata;"
```
Создает необходимые базы данных
___
```posh
echo '### Run frontend ###'
cd rangiffler-gql-client
npm i
npm run dev 
```
Собирает и запускает фронтенд
___

**Запуск java приложений**

После того, как фронтенд и docker контейнеры запущены, можно запускать java приложения одним из 2 способов:

**1.**
- Необходимо прописать run конфигурацию для всех сервисов rangiffler-* - Active profiles local. Для этого зайти в меню Run -> Edit Configurations -> выбрать main класс -> указать Active profiles: local
[Инструкция](https://stackoverflow.com/questions/39738901/how-do-i-activate-a-spring-boot-profile-when-running-from-intellij).
- Запустить сервис Rangiffler-auth c помощью gradle (ниже) или командой Run в IDE (просто перейдя к main-классу приложения RangifflerAuthApplication выбрать run в IDEA (предварительно удостовериться что
  выполнен предыдущий пункт))

```posh
rangiffler % cd rangiffler-auth
rangiffler-auth % gradle bootRun --args='--spring.profiles.active=local'
```

- Запустить в любой последовательности другие сервисы: rangiffler-geo, rangiffler-photo, rangiffler-gateway, rangiffler-userdata

**2.**
Запустить скрипт в корне проекта, который делает тоже самое
```posh
rangiffler % bash run_apps.sh
```

# Запуск Rangiffler в докере:

#### 1. Создать бесплатную учетную запись на https://hub.docker.com/ (если отсутствует)

#### 2. Создать в настройках своей учетной записи access_token

[Инструкция](https://docs.docker.com/docker-hub/access-tokens/).

#### 3. Выполнить docker login с созданным access_token (в инструкции это описано)

#### 4. Прописать в etc/hosts элиас для Docker-имени (адрес папки для windows C:\WINDOWS\system32\drivers\etc\hosts)

127.0.0.1 frontend.rangiffler.dc   
127.0.0.1 auth.rangiffler.dc   
127.0.0.1 gateway.rangiffler.dc   
127.0.0.1 userdata.rangiffler.dc   
127.0.0.1 allure 

Последний alias allure нужен для просмотра результатов выполнения тестов в docker


```posh
rangiffler % vi /etc/hosts
```

```posh
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting.  Do not change this entry.
##
127.0.0.1 frontend.rangiffler.dc   
127.0.0.1 auth.rangiffler.dc   
127.0.0.1 gateway.rangiffler.dc   
127.0.0.1 userdata.rangiffler.dc   
127.0.0.1 allure 
```

#### 5. Перейти в корневой каталог проекта

```posh
rangiffler % cd rangiffler
```

#### 6. Запустить все сервисы:

```posh
rangiffler % bash docker-compose-dev.sh
```

Текущая версия docker-compose-dev.sh удалит все старые Docker контейнеры в системе, поэтому если у вас есть созданные
контейнеры для других проектов - отредактируйте строку ```posh docker rm $(docker ps -a -q)```, чтобы включить в grep
только те контейнеры, что непосредственно относятся к rangiffler.

Rangiffler при запуске в докере будет работать для вас по адресу http://frontend.rangiffler.dc:80, этот порт НЕ НУЖНО
указывать в браузере, таким образом переходить напрямую по ссылке http://frontend.rangiffler.dc

Если при выполнении скрипта вы получили ошибку

```
* What went wrong:
Execution failed for task ':rangiffler-auth:jibDockerBuild'.
> com.google.cloud.tools.jib.plugins.common.BuildStepsExecutionException: 
Build to Docker daemon failed, perhaps you should make sure your credentials for 'registry-1.docker.io...
```

То необходимо убедиться, что в `$USER/.docker/config.json` файле отсутствует запись `"credsStore": "desktop"`
При наличии такого ключа в json, его надо удалить

# Запуск e-2-e тестов в Docker network изолированно Rangiffler в докере:

#### 1. Перейти в корневой каталог проекта

```posh
rangiffler % cd rangiffler
```

#### 2. Запустить все сервисы и тесты:

```posh
rangiffler % bash docker-compose-e2e.sh
```
**Важно** При запуске с windows при старте контенера rangiffler-e-2-e, может возникнуть ошибка в логах контенера:
```
./gradlew: not found
```
Для ее решения необходимо запустить команду, которая заменяет окончания строк с unix на windows:
```posh
rangiffler % sed -i -e 's/\r$//' gradlew
```

#### 3. Selenoid UI доступен по адресу: http://localhost:9090/

#### 4. Allure доступен по адресу: http://allure:5050/allure-docker-service/projects/rangiffler-e-2-e-tests/reports/latest/index.html
<img src="alllure results.png" width="800">

### Путешествуй, be like Rangiffler!
<img src="rangiffler.png" width="800">
