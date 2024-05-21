/**
 *  @author  jorge.iglesias@es.ibm.com
 *  @version 1.0
 */

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.commons.lang3.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.print.attribute.IntegerSyntax;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class KafkaExampleCart {

    private static final Logger logger = LoggerFactory.getLogger(KafkaExampleCart.class);

    private static final String APP_NAME = "kafka-cart-1.0-SNAPSHOT-jar-with-dependencies";
    private static final String CONSUMER = "consumer";
    private static final String PRODUCER = "producer";
    private static final String ARG_TOPIC = "topic";
    private static final String ARG_MODE = "mode";
    private static final String ARG_SCHEMA = "schema";
    private static final String ARG_LENGTH = "length";
    private static final int DEFAULT_LENGTH = 10;

    private final String topic;
    private final String mode;
    private final int length;

    private final Properties props;

    public KafkaExampleCart(String topic_name, String mode_type, int loop_length) throws Exception {

        props = new Properties();
        try {
            InputStream is = new FileInputStream("kafka.properties");
            props.load(is);
            is.close();                
            logger.info("✅ Kafka properties file loaded.");
        } catch (Exception e) {
            logger.error("❌ " + e.getMessage());
        }
     
        this.topic = topic_name;
        this.mode = mode_type;
        this.length = loop_length;
    }

    public void consume() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        logger.info("🚀 Consumer started.");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("%s [%d] offset=%d, key=%s, value=\"%s\"\n",
								  record.topic(), record.partition(),
								  record.offset(), record.key(), record.value());
			}
        }
    }

    public void produce() {
        Producer<String, String> producer = new KafkaProducer<>(props);
        logger.info("🚀 Producer started.");

        try {
            Random rn = new Random();

            for (int i = 0; i < length; i++) {

                String idref = RandomStringUtils.randomAlphabetic(3);
                String quantity = RandomStringUtils.randomNumeric(2);

                String jsonString = "{\"idref': \"" + idref.toUpperCase() + "\",\"quantity\": " + quantity + "}";
                logger.info("📩 Message [" + (i+1) + "]: " + jsonString);

                producer.send(new ProducerRecord<>(topic, Integer.toString(i), jsonString));
                Thread.sleep(1000);       
            }
        } catch (InterruptedException e) {
            logger.error("❌ " + e.getMessage());
        }

        producer.close();
    }

    public static void main(String[] args) {
        CommandLine cl = setCommandLine(args);

        logger.info("Application: {}", APP_NAME);
        try {
            String topic_name = cl.getOptionValue(ARG_TOPIC);
            String mode_type = cl.getOptionValue(ARG_MODE);
            int length = DEFAULT_LENGTH;

            if (cl.getOptionValue(ARG_LENGTH)!=null) length = new Integer(cl.getOptionValue(ARG_LENGTH)).intValue();

            KafkaExampleCart c = new KafkaExampleCart(topic_name,mode_type,length);
            if (c.mode.equals(CONSUMER)) c.consume();
            if (c.mode.equals(PRODUCER)) c.produce();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("❌ " + e.getMessage());
        } finally {
            logger.info("🍻 Exiting the program.");
            System.exit(0);
        }
    } 

    public static CommandLine setCommandLine(String[] args) {
        CommandLine cl = null;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        Option t = Option.builder("t")
            .required(true)
            .argName(ARG_TOPIC)
            .longOpt(ARG_TOPIC)
            .hasArg()
            .desc("The Kafka topic to which messages are produced or from which messages are consumed.")
            .build();
        Option m = Option.builder("m")
            .required(true)
            .argName(ARG_MODE) 
            .longOpt(ARG_MODE)
            .hasArg()
            .desc("Specifies whether the process acts as a producer or consumer. Acceptable values: " + CONSUMER + " or " + PRODUCER)
            .build();
        Option l = Option.builder("l")
            .required(false)
            .argName(ARG_LENGTH) 
            .longOpt(ARG_LENGTH)
            .hasArg()
            .desc("The number of messages to produce. Default is 10.")
            .build();

        options.addOption(t); 
        options.addOption(m);
        options.addOption(l);

        try {
            cl = parser.parse(options, args);

            if (!cl.getOptionValue(ARG_MODE).equals(CONSUMER) && !cl.getOptionValue(ARG_MODE).equals(PRODUCER)) {
                throw new ParseException("Process argument can only have one of these two values:" + CONSUMER + " or " + PRODUCER);
            }
         
        } catch (ParseException e) {
            System.out.println("\nParse error: ");
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar " + APP_NAME + ".jar", "", options, "", true);     
            System.exit(-1);
        }

        return cl;
    }

}
