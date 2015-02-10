package pl.banachowski.psoir.controller;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;

@Component
public class QueueController {
    private BasicAWSCredentials credentials;
    private AmazonSQS sqs;
    @Value("${sec.accessKey}")
    private String ACCESS_KEY;
    @Value("${sec.secretKey}")
    private String SECRET_KEY;
    //    @Value("${sec.queueName}")
    private String QUEUE_NAME="mb_sqs";

    //private static volatile QueueController awssqsUtil = new QueueController();

    /**
     * instantiates a AmazonSQSClient http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSClient.html
     * Currently using  BasicAWSCredentials to pass on the credentials.
     * For SQS you need to set your regions endpoint for sqs.
     */
    @PostConstruct
    public void qQueueController() {
        System.out.println("Sth: "+ACCESS_KEY);
        try {
            Properties properties = new Properties();
            properties.put("accessKey", ACCESS_KEY);
            properties.put("secretKey", SECRET_KEY);
            this.credentials = new BasicAWSCredentials(properties.getProperty("accessKey"),
                    properties.getProperty("secretKey"));


            this.sqs = new AmazonSQSClient(this.credentials);
            /**
             * My queue is in singapore region which has following endpoint for sqs
             * https://sqs.ap-southeast-1.amazonaws.com
             * you can find your endpoints here
             * http://docs.aws.amazon.com/general/latest/gr/rande.html
             *
             * Overrides the default endpoint for this client ("sqs.us-east-1.amazonaws.com")
             */
            this.sqs.setEndpoint("https://sqs.us-west-2.amazonaws.com");
            /**
             You can use this in your web app where    AwsCredentials.properties is stored in web-inf/classes
             */
            //AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());

        } catch (Exception e) {
            System.out.println("exception while creating awss3client : " + e);
        }
    }

//    public static QueueController getInstance() {
//        return awssqsUtil;
//    }

    public AmazonSQS getAWSSQSClient() {
        return sqs;
    }

    public String getQueueName() {
        return QUEUE_NAME;
    }

    /**
     * Creates a queue in your region and returns the url of the queue
     * @param queueName
     * @return
     */
//	    public String createQueue(String queueName){
//	        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
//	        String queueUrl = this.sqs.createQueue(createQueueRequest).getQueueUrl();
//	        return queueUrl;
//	    }

    /**
     * returns the queueurl for for sqs queue if you pass in a name
     *
     * @param queueName
     * @return
     */
    public String getQueueUrl(String queueName) {
        queueName = "mb_sqs";
        System.out.println(queueName);
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
        System.out.println(getQueueUrlRequest);
        return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
    }

    /**
     * lists all your queue.
     *
     * @return
     */
    public ListQueuesResult listQueues() {
        return this.sqs.listQueues();
    }

    /**
     * send a single message to your sqs queue
     *
     * @param queueUrl
     * @param message
     */
    public SendMessageResult sendMessageToQueue(String queueUrl, String message) {
        SendMessageResult messageResult = this.sqs.sendMessage(new SendMessageRequest(queueUrl, message));
        System.out.println(messageResult.toString());
        return messageResult;
    }

    /**
     * gets messages from your queue
     *
     * @param queueUrl
     * @return
     */
    public List<Message> getMessagesFromQueue(String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(1);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    /**
     * deletes a single message from your queue.
     *
     * @param queueUrl
     * @param message
     */
    public void deleteMessageFromQueue(String queueUrl, Message message) {
        String messageRecieptHandle = message.getReceiptHandle();
        System.out.println("message deleted : " + message.getBody() + "." + message.getReceiptHandle());
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }
}
