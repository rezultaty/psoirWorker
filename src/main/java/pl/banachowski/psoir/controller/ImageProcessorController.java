package pl.banachowski.psoir.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import pl.banachowski.psoir.service.PsoirService;

@RestController
public class ImageProcessorController {

	private static final String UPLOADS = "uploads";
	private static final String RESULT = "procesingDone/";
    @Value( "${sec.accessKey}" )
    private String ACCESS_KEY;
    @Value( "${sec.secretKey}" )
    private String SECRET_KEY;
    @Value( "${sec.bucketName}" )
    private String BUCKET_NAME;

	@Autowired
	private QueueController queueController;

	@Autowired
	private FilterController filterController;

    @Autowired
    private PsoirService psoirService;

	String queueUrl;

	@PostConstruct
	public void postConstruct() {
		queueUrl = queueController.getQueueUrl(queueController.getQueueName());
	}

	@RequestMapping("/healthCheck")
	public String healthCheck() {
		return "Working";
	}


	@RequestMapping("/generate")
	public String processImages() throws IOException {
		AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(
				ACCESS_KEY, SECRET_KEY));

		ObjectListing objectListing = s3Client
				.listObjects(new ListObjectsRequest().withBucketName(
						BUCKET_NAME).withPrefix(UPLOADS));
		List<S3ObjectSummary> objectSummaries = objectListing
				.getObjectSummaries();

		System.out.println("Filtr ");


		List<Message> messages = queueController
				.getMessagesFromQueue(this.queueUrl);

		for (Message message : messages) {
			System.out.println(message.getBody() + " ");
		}
		System.out.println("Lista plikow");
		//

		for (Message filterNameMessage : messages) {
			for (S3ObjectSummary objectSummary : objectSummaries) {
				try {
					S3Object object = s3Client.getObject(new GetObjectRequest(
							BUCKET_NAME, filterNameMessage.getBody()));
					InputStream is = object.getObjectContent();

					BufferedImage buffImg = ImageIO.read(is);
					buffImg = filterController.scale(buffImg);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(buffImg, "jpg", os);
					InputStream bis = new ByteArrayInputStream(os.toByteArray());

					ObjectMetadata objectMetadata = object.getObjectMetadata();
					objectMetadata.setContentLength(os.size());
					
					String key = object.getKey();
					s3Client.putObject(
							BUCKET_NAME,
							RESULT + key, bis, objectMetadata);

                    psoirService.saveLog("Pomyslnie zakończono przetwarzanie", new Date());
				} catch (Exception e) {
					e.printStackTrace();
                    psoirService.saveLog("Blad przetwarzania", new Date());
				}
			}
			queueController.deleteMessageFromQueue(queueUrl, filterNameMessage);
		}

		return "Done";
	}
}
