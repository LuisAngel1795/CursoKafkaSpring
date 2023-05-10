package com.devs4j.kafka;

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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
public class CursoKafkaSpringApplication implements CommandLineRunner {
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	private static final Logger log = LoggerFactory.getLogger(CursoKafkaSpringApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CursoKafkaSpringApplication.class, args);
	}
@KafkaListener(topics="devs4j-topic", containerFactory = "listenerContainerFactory", groupId = "devs4j",
		properties = {"max.poll.interval.ms:4000", "max.poll.records:10"})
	public void listen(List<ConsumerRecord<String,String>> messages){
		messages.stream().map(message->{
			log.info("Partition:{} Offset:{} Key:{} Value:{}",message.partition(),
					message.offset(), message.key(), message.value());
			return message;
		}).collect(Collectors.toList());
		log.info("BATCH COMPLETE");
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * Callback Asincrono
		 */
//		CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("devs4j-topic", "Sample Message ");
//		future.whenComplete((result,exception)->{
//			if(exception!=null){
//				log.error("Error sending message: {}",exception.getMessage());
//			}else{
//				log.info("Message send successfully: {}", result.getRecordMetadata().offset());
//			}
//		});
		/**
		 * Callback sincrono
		 */
		//kafkaTemplate.send("devs4j-topic", "Sample Message ").get(100, TimeUnit.MILLISECONDS);
		/**
		 * envio de batch de msjs (10 mensajes)
		 */
		for(int i=0; i<100;i++){
			kafkaTemplate.send("devs4j-topic","Key:"+(i+1) ,String.format("Sample message %d", i));
		}
	}
}
