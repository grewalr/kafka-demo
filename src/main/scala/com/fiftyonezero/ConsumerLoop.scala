package com.fiftyonezero

import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConversions._

class ConsumerLoop(id: Integer, groupId: String, topic: String) extends Runnable {

  //  val topic = "grewalr-test"
  val brokers: String = "ec2-18-130-168-20.eu-west-2.compute.amazonaws.com:9092," +
    "ec2-18-130-185-43.eu-west-2.compute.amazonaws.com:9092," +
    "ec2-35-176-127-158.eu-west-2.compute.amazonaws.com:9092"

  val props: Properties = createConsumerConfig(brokers, groupId)
  val consumer = new KafkaConsumer[String, String](props)

  def createConsumerConfig(brokers: String, groupId: String): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    //    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId) // Not mandatory for demo purposes
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "largest")
    props
  }

  override def run(): Unit = {
    try {
      consumer.subscribe(Collections.singletonList(topic))

      while (true) {
        val records = consumer.poll(10)

        for (record <- records) {
          println(s"Received message: (${record.key()},${record.value()}) at offset ${record.offset()}")
        }
      }
    } finally {
      consumer.close()
    }
  }

  // The shutdown hook will be invoked when stop the process,
  // which will halt the consumer threads using wakeup and wait
  // for them to shutdown
  def shutdown(): Unit = consumer.wakeup() //
}
