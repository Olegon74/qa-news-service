# Запуск Kafka-тестов в GitHub Actions

За запуск отвечает [workflow `kafka-integration-tests.yml`](../.github/workflows/kafka-integration-tests.yml). Он выполняет 14 интеграционных Kafka-тестов в отдельной job `kafka-integration-tests`.

## Когда запускается

- Автоматически при `push` или `pull request`, если изменились файлы в `backend/**` или сам workflow.
- Вручную: откройте в GitHub вкладку **Actions** → **Kafka integration tests** → **Run workflow**, выберите ветку и нажмите **Run workflow**.

## Что происходит в job

1. GitHub Actions получает репозиторий и устанавливает Temurin Java 21.
2. Maven запускает профиль `kafka-it`:

   ```bash
   mvn -B -f backend/pom.xml -Pkafka-it verify
   ```

3. Testcontainers поднимает временные PostgreSQL и Kafka через Docker daemon GitHub runner. Тесты запускают Spring Boot backend на случайном порту, выполняют API-запросы и читают Kafka-события тестовым consumer. Docker Compose, frontend и внешний Kafka-стенд для этой job не нужны.
4. Maven Failsafe проверяет классы с суффиксом `*IT`. При ошибке теста job завершается неуспешно.
5. Шаг загрузки артефактов выполняется и при успехе, и при ошибке. В разделе **Artifacts** запуска можно скачать `kafka-integration-test-results` с `backend/target/failsafe-reports/` и `backend/target/allure-results/`.

## Где смотреть результат

Откройте нужный запуск во вкладке **Actions** и шаг **Run Kafka and PostgreSQL integration tests**: в его логе видно число выполненных тестов и причину ошибки. Подробные XML-отчёты и исходные результаты Allure находятся в скачанном артефакте.

Ту же команду можно выполнить локально из корня репозитория при доступном Docker daemon. Подробности устройства тестов — в [руководстве по Kafka](kafka-integration-guide.md).
