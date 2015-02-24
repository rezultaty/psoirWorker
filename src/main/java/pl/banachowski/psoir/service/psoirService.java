package pl.banachowski.psoir.service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Maciek on 2015-02-22.
 */
@Service
public class PsoirService {
    private BasicAWSCredentials credentials;
    private AmazonSQS sqs;
    @Value("${sec.accessKey}")
    private String ACCESS_KEY;
    @Value("${sec.secretKey}")
    private String SECRET_KEY;

    private String QUEUE_NAME="mb_sqs";
    private final static Logger LOG = Logger.getLogger(PsoirService.class.getName());
    private AmazonSimpleDBClient dbClient;
    private final String LOG_DOMAIN_NAME = "log";

    @PostConstruct
    public void init(){
        dbClient = getInstance();
        ListDomainsResult listDomainsResult =dbClient.listDomains();
        if(!listDomainsResult.getDomainNames().contains(LOG_DOMAIN_NAME)){
            createDomain();
        }
    }

    private AmazonSimpleDBClient getInstance(){
        Properties properties = new Properties();
        properties.put("accessKey", ACCESS_KEY);
        properties.put("secretKey", SECRET_KEY);
        BasicAWSCredentials credentials;
        credentials = new BasicAWSCredentials(properties.getProperty("accessKey"),
                properties.getProperty("secretKey"));
        AmazonSimpleDBClient dbclient = new AmazonSimpleDBClient(credentials);
        return dbclient;
    }

    public void createDomain(){
        CreateDomainRequest createDomainRequest = new CreateDomainRequest(LOG_DOMAIN_NAME);
        dbClient.createDomain(createDomainRequest);
    }

    public void saveLog(String log, Date data){
        String logId = UUID.randomUUID().toString();
        List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
        attributes.add(new ReplaceableAttribute("message",log,Boolean.FALSE));
        attributes.add(new ReplaceableAttribute("data",data.toString(),Boolean.FALSE));

        PutAttributesRequest putAttributesRequest = new PutAttributesRequest(LOG_DOMAIN_NAME,logId, attributes);
        dbClient.putAttributes(putAttributesRequest);
    }

    public List<String> listAll(){
        List<String> logs = new ArrayList<String>();
        SelectRequest query = new SelectRequest("SELECT * FROM log");
        SelectResult result = dbClient.select(query);
        LOG.info("There was: "+result.getItems().size()+ " results");


        for (Item item : result.getItems()) {
            LOG.info("Msg was: "+item.getAttributes().get(0));
        }


        return logs;
    }



}
