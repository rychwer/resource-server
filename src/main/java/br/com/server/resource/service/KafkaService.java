package br.com.server.resource.service;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
public abstract class KafkaService<K, T extends SpecificRecordBase> {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.acks}")
    private String acks;

    @Value("${kafka.retries}")
    private String retries;

    @Value("${kafka.schema.registry.url}")
    private String schemaRegistryUrl;

    private Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("acks", acks);
        properties.setProperty("retries", retries);

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", schemaRegistryUrl);


        return properties;
    }

    protected abstract String getTopic();

    protected abstract T configureProducer(K object);

    protected abstract Callback notificationCallback();

    @Async
    public void sendNotification(K object) {

        KafkaProducer<String, T> kafkaProducer = new KafkaProducer<String, T>(getKafkaProperties());
        ProducerRecord<String, T> producerRecord = new ProducerRecord<String, T>(getTopic(), configureProducer(object));
        kafkaProducer.send(producerRecord, notificationCallback());

        kafkaProducer.flush();
        kafkaProducer.close();
    }

}
