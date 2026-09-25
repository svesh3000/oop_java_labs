# Repository for oop labs and course work
# Cource work: Data Aggregator — Multi-threaded REST API Aggregator

[![Java 17](https://img.shields.io/badge/Java-17-blue)](https://openjdk.org/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-orange)](https://maven.apache.org/)

Консольное Java-приложение для агрегации данных из нескольких открытых REST API. Умеет получать JSON-ответы, преобразовывать их в плоские таблицы и сохранять в файлы форматов **JSON** или **CSV**. Поддерживает многопоточный периодический опрос источников с контролем параллелизма и интервалов между запросами.

## Возможности

- Получение данных из трёх встроенных API: **JokeAPI**, **Scryfall**, **Open-Meteo**.
- Два режима работы: **автоматический** (запуск из CLI) и **интерактивный** (меню в консоли).
- Сохранение записей в **JSON** или **CSV** с автоматическим определением формата по расширению.
- Многопоточный опрос источников с ограничением числа одновременно выполняемых задач (`n`) и интервалом между повторными опросами одного API (`t`).
- Потокобезопасная запись в файл через `BlockingQueue` и отдельный поток-писатель.
- Атомарная замена файла: запись через временный файл и `Files.move` с `ATOMIC_MOVE`, чтобы старые данные не терялись при сбоях.
- Корректное завершение работы через shutdown hook при `Ctrl+C`.
- Добавление нового API без изменения основной логики: достаточно реализовать `ApiDefinition` и зарегистрировать его в `ApiRegistry`.

## Стек технологий

| Технология | Назначение |
|---|---|
| **Java 17** | Язык и стандартная библиотека |
| **Maven** | Сборка проекта, управление зависимостями |
| **OkHttp** | HTTP-клиент для запросов к REST API |
| **Jackson** | Парсинг JSON и сериализация записей |
| **Apache Commons CSV** | Чтение и запись CSV |
| **JUnit 5** | Модульное тестирование |
| **Mockito** | Мокирование зависимостей в тестах |
| **MockWebServer** | Тестирование HTTP-клиента без реальной сети |
| **JaCoCo** | Измерение покрытия тестами |

## Архитектура

Приложение разделено на слои по ответственности. Основные пакеты:

| Пакет | Назначение |
|---|---|
| `com.svesh.course_work.api` | Описание REST API: интерфейс `ApiDefinition`, реестр `ApiRegistry`, спецификации параметров `ParamSpec`, резолвер `ParamResolver` |
| `com.svesh.course_work.api.impl` | Конкретные API: `JokeApi`, `ScryfallApi`, `OpenMeteoApi` |
| `com.svesh.course_work.ingest` | Получение и обработка данных: `HttpService`, `JsonParser`, `IngestService` |
| `com.svesh.course_work.models` | Модель данных: `DataRecord` |
| `com.svesh.course_work.io` | Форматы и преобразование: `OutputFormat`, `CsvConverter`, `CsvTable` |
| `com.svesh.course_work.io.storage` | Сохранение и чтение: `Storage`, `JsonStorage`, `CsvStorage`, `AtomicFileWriter` |
| `com.svesh.course_work.io.filters` | Фильтрация записей по источнику |
| `com.svesh.course_work.io.viewer` | Вывод записей в консоль |
| `com.svesh.course_work.parallel` | Многопоточный опрос: `PollingService` |
| `com.svesh.course_work.app` | Прикладной слой: `AppContext`, `AppRunner`, `OutputPathResolver` |
| `com.svesh.course_work.app.cli` | Разбор аргументов командной строки |
| `com.svesh.course_work.app.modes` | Режимы работы: `AutomaticMode`, `InteractiveMode` |
| `com.svesh.course_work.app.help` | Вывод справки |

Схема обработки данных:

```
User
  │
  ▼
Mode (Automatic / Interactive)
  │
  ▼
RequestBuilder ──► ApiRegistry
  │
  ▼
IngestService ──► HttpService
  │              └─► JsonParser
  ▼
DataRecord
  │
  ▼
StorageFactory ──► Storage (JsonStorage / CsvStorage)
  │                    │
  │                    └─► AtomicFileWriter
  ▼
Output File
```

При параллельном опросе:

```
PollingService
  ├── ScheduledExecutorService (пул поллеров, размер n)
  │       ├── pollOnce(req) ──► IngestService.fetchOne ──► BlockingQueue.put
  │       ├── pollOnce(req)
  │       └── ...
  └── ExecutorService (поток-писатель)
          └── queue.poll ──► storage.write (батч до 10 записей)
```

Каждый поллер после завершения запроса планирует следующий запуск через `t` секунд. Запись выполняется единственным потоком-писателем, что делает её потокобезопасной без дополнительных блокировок.

## Требования

- **Java 17** или выше
- **Apache Maven 3.8** или выше

## Сборка

```bash
mvn clean package
```

После успешной сборки Maven создаёт артефакт проекта и прогоняет автоматические тесты.

## Запуск

### Интерактивный режим

```bash
java -jar course_work.jar --interactive
```

После запуска отображается консольное меню:

```
===== MAIN MENU =====
1. Export data from API
2. View all records
3. View records by source
4. Start polling
5. Stop polling
0. Exit
```

### Автоматический режим

```bash
java -jar course_work.jar --automatic --api joke --format json
java -jar course_work.jar --automatic --api joke scryfall --format csv --out result
```

Флаги автоматического режима:

| Флаг | Описание |
|---|---|
| `--api` | Одно или несколько имён API (обязательно, дубликаты допустимы) |
| `--format` | Формат вывода: `json` или `csv` (обязательно) |
| `--out` | Путь к выходному файлу (опционально, по умолчанию `output.<format>`) |
| `--n` | Максимальное число одновременно выполняемых задач (опционально, по умолчанию `1`) |
| `--t` | Интервал между повторными опросами одного API в секундах (опционально, по умолчанию `5`) |

Пример многопоточного режима:

```bash
java -jar course_work.jar --automatic --api joke joke scryfall --format json --n 3 --t 10
```

Здесь три поллера параллельно опрашивают указанные источники с интервалом 10 секунд после завершения каждого запроса.

## Формат сохранения данных

### JSON

```json
[
  {
    "id": 1,
    "source": "joke",
    "timestamp": "2026-02-04T11:56:23+00:00",
    "data": {
      "type": "single",
      "joke": "..."
    }
  }
]
```

### CSV

Денормализованная таблица, где вложенные поля выносятся в колонки с точкой-разделителем, а массивы разворачиваются в дополнительные строки:

```
id,source,timestamp,data.type,data.joke,data.latitude,data.longitude
1,joke,2026-02-04T11:56:23+00:00,single,"...",,
2,open-meteo,2026-02-04T11:58:21+00:00,,,53.0,35.9
```

## Тестирование

```bash
mvn test
```

Для полного цикла сборки и тестирования:

```bash
mvn clean test
```

Отчёт о покрытии формируется в `target/site/jacoco/index.html`.

Тесты покрывают бизнес-логику: парсинг и валидацию параметров, разбор CLI, преобразование JSON в CSV, сохранение и чтение файлов, HTTP-клиент (через `MockWebServer`), многопоточный опрос. Сетевые вызовы в тестах отсутствуют.
