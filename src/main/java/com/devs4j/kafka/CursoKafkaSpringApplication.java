package com.devs4j.kafka;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class CursoKafkaSpringApplication{
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private MeterRegistry meterRegistry;
	private static final Logger log = LoggerFactory.getLogger(CursoKafkaSpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CursoKafkaSpringApplication.class, args);
	}

	@KafkaListener(topics="devs4j-topic", containerFactory = "listenerContainerFactory", groupId = "devs4j",
			properties = {"max.poll.interval.ms:4000", "max.poll.records:50"})
	public void listen(List<ConsumerRecord<String,String>> messages){
		messages.stream().map(message->{
//			log.info("Partition:{} Offset:{} Key:{} Value:{}",message.partition(),
//					message.offset(), message.key(), message.value());
			return message;
		}).collect(Collectors.toList());
//		log.info("BATCH COMPLETE");
	}
@Scheduled(fixedDelay = 2000, initialDelay = 100)
	public void sendKafkaMessages(){
	for(int i=0; i<100;i++){
		kafkaTemplate.send("devs4j-topic","Key:"+(i+1) ,String.format("Sample message %d", i));
	}
	}

	@Scheduled(fixedDelay = 2000, initialDelay = 500)
	public void printMetrics(){
		List<Meter> metrics = meterRegistry.getMeters();
		metrics.stream().map(metric->{
				log.info("Meter {}",metric.getId().getName());
				return metric;
		}).collect(Collectors.toList());
		Double count = meterRegistry.get("kafka.producer.record.send.total").functionCounter().count();
		log.info("Count {}", count);
	}

}
