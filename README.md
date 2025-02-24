# 🚀 Quarkus + Apache Camel Application

This application demonstrates how to use **Quarkus** and **Apache Camel** to build a modular, scalable, and maintainable integration solution. It includes multiple routes for file processing, JSON transformation, error handling, and ActiveMQ integration.

---

## 📖 **Features**
- **File Listener Route**: Listens to a directory for new files, processes them, and sends the content to ActiveMQ.
- **JSON Processing Route**: Processes JSON messages, transforms them, and sends the result to ActiveMQ.
- **Error Handling Route**: Demonstrates how to handle exceptions gracefully.
- **ActiveMQ Consumer Route**: Consumes messages from ActiveMQ and logs them.
- **Configuration**: Externalized configuration using `application.properties`.

---

## **Prerequisites**
- Java 21+
- Apache ActiveMQ Artemis
- Maven 3.8+
- Quarkus
- Apache Camel

---

## **Setup**
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/quarkus-camel-poc.git
   cd quarkus-camel-poc
   ```

2. Start ActiveMQ Artemis:
    ```bash
    ./artemis run
    ```
3. Access the Web Console:

  - URL: `http://localhost:8161`
  - Username: `test` (default `admin`)
  - Password: `test` (default `admin`)

4. Update the `application.properties` file with your ActiveMQ connection details.

5. Run the application:
    ```bash
    ./mvnw quarkus:dev
    ```

## Routes 

### File Listener Route

- Endpoint: `file:src/main/resources/data?noop=true`
- **Description**: Listens for new files in the `src/main/resources/data` directory, processes them, and sends the content to the `fileMessages` queue in ActiveMQ. 
<br>📍 **Location:** `tech.poc.routes.FileListenerRoute`

### FileProcessor (File Metadata & Validation)

<br>📍 **Location:** `tech.poc.processors.FileProcessor`
- **Description**: Extracts file name, size, last modified time, validates files (rejects empty files) and sets metadata to header for logging.

### JSON Processing Route

- Endpoint: `direct:processJson`
- **Description**: Processes JSON messages, transforms them, and sends the result to the `processedMessages` queue in ActiveMQ.

### Error Handling Route

- Endpoint: `direct:handleErrors`
- **Description**: Demonstrates how to handle exceptions gracefully.

### ActiveMQ Consumer Route

- Endpoint: `jms:queue:processedMessages`
- Description: Consumes messages from the `processedMessages` queue and logs them.

## Configuration :memo:

All configuration settings are defined in `src/main/resources/application.properties`.

## Testing Routes 

### 1. File Processing Route 

**How to test:**

1. Place a test file inside `src/main/resources/data` (e.g., `test.txt`);
2. The application will automatically detect the file and send its content to ActiveMQ.
3. Check expected logs:
   ```bash
   2025-02-24 16:47:05,029 INFO  [FileListenerRoute:20] (Camel (camel-1) thread #2 - file://src/main/resources/data) File received: test.txt
   2025-02-24 16:47:05,035 INFO  [FileListenerRoute:23] (Camel (camel-1) thread #2 - file://src/main/resources/data) Sending Processed File Content: Processed File: test.txt
   Size: 30 bytes
   Last Modified: 2025-02-24T16:46:56.093995807Z
   Content:
   Hello, from file in resources!
   ```

### 2. JSON Processing Route 

**How to test:**

1. Use `Postman` or a `REST client` to send a JSON message:

   ```bash
   curl -X POST http://localhost:8080/messages/json -H "Content-Type: application/json" -d '{"name": "John Doe"}'
   ```

2. Expected console logs:
   ```bash
   2025-02-24 19:37:42,268 INFO  [JsonProcessingRoute:18] (executor-thread-1) Received JSON: {"name":"Matheus Carneiro"}
   2025-02-24 19:37:42,339 INFO  [ActiveMQConsumerRoute:15] (Camel (camel-1) thread #3 - JmsConsumer[processedMessages]) Received message directly: Hello, Matheus Carneiro
   ```

### 3. Error Handling Route 
   ```bash
   2025-02-24 19:44:54,828 INFO  [ErrorHandlingRoute:18] (executor-thread-1) Caught exception: Simulated error
   2025-02-24 19:44:54,842 INFO  [errorHandlingRoute] (executor-thread-1) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Error handled gracefully] 
   ```

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/camel-quarkus-poc-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Camel Log ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/log.html)): Prints data form the routed message (such as body and headers) to the logger
- Camel Core ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/core.html)): Camel core functionality and basic Camel languages: Constant, ExchangeProperty, Header, Ref, Simple and Tokenize
- Camel Direct ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/direct.html)): Call another endpoint from the same Camel Context synchronously
- Camel Jackson ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/jackson.html)): Marshal POJOs to JSON and back using Jackson
- Camel ActiveMQ ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/activemq.html)): Send messages to (or consume from) Apache ActiveMQ 5.x. This component extends the Camel JMS component
- Camel JMS ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/jms.html)): Send and receive messages to/from JMS message brokers
