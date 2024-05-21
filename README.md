# Kafka Producer and Consumer example

This project is a Java application designed to interact with a Kafka cluster, functioning as both a producer and a consumer of messages for a specified topic. The messages adhere to a JSON schema representing a shopping cart with product details.

## Description of a Java Process Interacting with Kafka Cluster as Producer and Consumer

This Java process demonstrates how to interact with an Apache Kafka cluster by acting as both a producer and a consumer for a specific topic. The topic in question involves product cart data represented in a JSON schema. The schema includes two fields: `idref` for the product reference ID and `quantity` for the number of products being purchased.

## Features

- **Producer**: Generates random product cart entries and sends them to a specified Kafka topic.
- **Consumer**: Listens to a specified Kafka topic and processes incoming messages.

## Message Schema

Each message follows a JSON format with the following structure:

```json
{
  "idref": "ABC",
  "quantity": 10
}
```

## How It Works

1. **Producer Mode**: 
   - Generates random `idref` values (three uppercase alphabetical characters).
   - Generates random `quantity` values (integer between 1 and 99).
   - Sends the generated messages to the specified Kafka topic.

2. **Consumer Mode**: 
   - Listens to the specified Kafka topic.
   - Processes and logs the received messages.

## Usage

### Command-Line Arguments

- `mode` (required): Specifies whether the process acts as a producer or consumer. Acceptable values: `producer`, `consumer`.
- `topic` (required): The Kafka topic to which messages are produced or from which messages are consumed.
- `loops` (optional): The number of messages to produce. Default is `10`.

### Example Commands

- Run as a producer:
  ```bash
     java -jar target/kafka-cart-1.0-SNAPSHOT-jar-with-dependencies.jar \
        -m producer -t topic_name -l 15
  ```
  This command will produce 15 messages to the `topic_name`.

- Run as a consumer:
  ```bash
     java -jar target/kafka-cart-1.0-SNAPSHOT-jar-with-dependencies.jar \
        -m consumer -t topic_name
  ```
  This command will consume messages from the `topic_name`.

### Default Behavior

If the number of loops is not specified when running in producer mode, it defaults to producing 10 messages.

## Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Apache Kafka cluster up and running.
- Kafka client libraries included in your classpath.

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/jorgeiglesiasfernandez/java-kafka-example-cart.git
   ```

2. Build the project:
   ```bash
   cd java-kafka-example-cart
   mvn clean compile assembly:single
   ```

3. Run the application using the provided jar file:
   ```bash
   java -jar kafka-cart-1.0-SNAPSHOT-jar-with-dependencies.jar \
    [-l <length>] -m <mode> -t <topic>
   ```

## Configuration

Ensure your Kafka cluster configuration (e.g., bootstrap servers) is correctly set up in the application's properties file or passed as environment variables.

## Contributing

Feel free to submit issues and pull requests. Contributions are welcome!

## License

This project is licensed under the MIT License. See the LICENSE file for details.