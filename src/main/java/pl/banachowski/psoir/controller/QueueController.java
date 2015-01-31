package pl.banachowski.psoir.controller;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Controller;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

@Controller
public class QueueController {
	  private BasicAWSCredentials credentials;
	    private AmazonSQS sqs;
	    private String simpleQueue = "banachowski_projekt";
	    private static volatile  QueueController awssqsUtil = new QueueController();

	    /**
	     * instantiates a AmazonSQSClient http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/AmazonSQSClient.html
	     * Currently using  BasicAWSCredentials to pass on the credentials.
	     * For SQS you need to set your regions endpoint for sqs.
	     */
	    private   QueueController(){
	        try{
	            Properties properties = new Properties();
	            properties.put("accessKey", "AKIAJWAMQO4SWI3ITIDQ");
	            properties.put("secretKey", "BAiPQSJWKiIzKvhvjCDC7L/6txAzIE6pi27g4PW6u+OHKtmWWhKA4TOnkrREYXQwtptvBVuQilzV4lmOPvx8b9sAdFKAFNKNwTjl4YhOZtn4yEy+BuDJC3zEH1kuex1Nk6kZjK+Svx2UTT95vRT2UtcYr1f0Ajc2vn2KxH4JR4w=");
	            this.credentials = new   BasicAWSCredentials(properties.getProperty("accessKey"),
	                                                         properties.getProperty("secretKey"));
	            this.simpleQueue = "banachowski_projekt";

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

	        }catch(Exception e){
	            System.out.println("exception while creating awss3client : " + e);
	        }
	    }

	    public static QueueController getInstance(){
	        return awssqsUtil;
	    }

	    public AmazonSQS getAWSSQSClient(){
	         return awssqsUtil.sqs;
	    }

	    public String getQueueName(){
	         return awssqsUtil.simpleQueue;
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
	     * @param queueName
	     * @return
	     */
	    public String getQueueUrl(String queueName){
	    	System.out.println(queueName);
	        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
	        System.out.println(getQueueUrlRequest);
	        return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
	    }

	    /**
	     * lists all your queue.
	     * @return
	     */
	    public ListQueuesResult listQueues(){
	       return this.sqs.listQueues();
	    }

	    /**
	     * send a single message to your sqs queue
	     * @param queueUrl
	     * @param message
	     */
	    public SendMessageResult sendMessageToQueue(String queueUrl, String message){
	        SendMessageResult messageResult =  this.sqs.sendMessage(new SendMessageRequest(queueUrl, message));
	        System.out.println(messageResult.toString());
	        return messageResult;
	    }

	    /**
	     * gets messages from your queue
	     * @param queueUrl
	     * @return
	     */
	    public List<Message> getMessagesFromQueue(String queueUrl){
	       ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
	       receiveMessageRequest.setMaxNumberOfMessages(1);
	       List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
	       return messages;
	    }

	    /**
	     * deletes a single message from your queue.
	     * @param queueUrl
	     * @param message
	     */
	    public void deleteMessageFromQueue(String queueUrl, Message message){
	        String messageRecieptHandle = message.getReceiptHandle();
	        System.out.println("message deleted : " + message.getBody() + "." + message.getReceiptHandle());
	        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
	    }
}
