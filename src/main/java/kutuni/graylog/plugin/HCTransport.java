package kutuni.graylog.plugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.BooleanField;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.NumberField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.inputs.MisfireException;
import org.graylog2.plugin.inputs.annotations.ConfigClass;
import org.graylog2.plugin.inputs.annotations.FactoryClass;
import org.graylog2.plugin.inputs.codecs.CodecAggregator;
import org.graylog2.plugin.inputs.transports.Transport;
import org.graylog2.plugin.journal.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;


public class HCTransport implements Transport {

    private static final Logger LOG = LoggerFactory.getLogger(HCTransport.class.getName());
    private static final String CK_CONFIG_GRUP_NAME = "grupName";
    private static final String CK_CONFIG_GRUP_PASS = "grupPasswd";
    private static final String CK_CONFIG_MULTICAST = "multicasting";
    private static final String CK_CONFIG_INTERFACE = "interface";
    private static final String CK_CONFIG_PORT = "port";
    private static final String CK_CONFIG_AUTOPORT = "autoport";
    private static final String CK_CONFIG_QUEUE_NAME = "queue_name";
    

    private final Configuration configuration;
    private final MetricRegistry metricRegistry;
    private ServerStatus serverStatus;
    private MessageInput messageInput;
	HazelcastInstance hcInstance = null;
	
    @AssistedInject
    public HCTransport(@Assisted Configuration configuration,
                                MetricRegistry metricRegistry,
                                ServerStatus serverStatus) {
        this.configuration = configuration;
        this.metricRegistry = metricRegistry;
        this.serverStatus = serverStatus;
        
        LOG.info("HC Plugin Transporter Initialized.");
    }


    @Override
    public void setMessageAggregator(CodecAggregator codecAggregator) {
    }

    

    @Override
    public void launch(MessageInput messageInput) throws MisfireException {
    	this.messageInput=messageInput;
    	LOG.info("HC Network Plugin Initializing...");
    	startHCInstance();
    }
    
    private void startHCInstance()
    {
    	new Thread()
    	{
    	    public void run() {
	        	LOG.info("HC Network Plugin Initializing...");
	        	int port=7501;
	        	String loggerName="loggerQ";
	        	try
	        	{
	        		port=configuration.getInt(CK_CONFIG_PORT);
	        		loggerName=configuration.getString(CK_CONFIG_QUEUE_NAME);
	        		LOG.debug("port"+port);
	        		LOG.debug("loggerName:"+loggerName);
	        		LOG.debug("Multicast enable"+configuration.getBoolean(CK_CONFIG_MULTICAST));
	        		LOG.debug("Interface:"+configuration.getString(CK_CONFIG_INTERFACE));
	        		
	        	}
	        	catch (Exception e){}


	        	//System.setProperty("hazelcast.prefer.ipv4.stack", "true");
	        	com.hazelcast.config.Config cfg = new com.hazelcast.config.Config();                  
	        	NetworkConfig network = cfg.getNetworkConfig();
	        	
	        	network.setPort(port);
	        	network.setReuseAddress( true );
	        	JoinConfig join = network.getJoin();
	        	join.getMulticastConfig().setEnabled(true);// configuration.getBoolean(CK_CONFIG_MULTICAST) )
	            //.addTrustedInterface( configuration.getString(CK_CONFIG_INTERFACE) );
	        	//join.getMulticastConfig().setMulticastGroup("224.2.2.3").setMulticastPort(54327);

	        	
	        	join.getTcpIpConfig().setEnabled(false);
	        	network.getInterfaces().setEnabled(true).addInterface(configuration.getString(CK_CONFIG_INTERFACE));
	        	hcInstance = Hazelcast.newHazelcastInstance(cfg);
	        	LOG.info("HC Cluster Ready");
	    		final IQueue<Map<String, String>> queue = hcInstance.getQueue(loggerName);
	    		queue.addItemListener(new ItemListener<Map<String,String>>() {
					
					@Override
					public void itemRemoved(ItemEvent<Map<String, String>> arg0) {
					}
					
					@Override
					public void itemAdded(ItemEvent<Map<String, String>> arg0) {
		    			Map<String, String> mp;
		    			try {
		    				mp = queue.take();
		    				LOG.debug("queue take message:"+mp);
		    				Map<String, Object> eventdata = createEvent();
		                    eventdata.putAll(mp);
		                    publishToGLServer(eventdata);
		    			} catch (Exception e) {
		    				LOG.error(e.getMessage(),e);
		    			}
						
					}
					
				},true );
    	    }
    	}.start();
    }
    

    private Map<String, Object> createEvent() {
        Map<String, Object> eventData = Maps.newHashMap();
        eventData.put("version", "1.1");
        //graylog needs a short_message as part of every event
        //eventData.put("short_message", "JAVA");
        return eventData;
    }


    private void publishToGLServer(Map<String, Object> eventData) throws IOException {
        synchronized (HCTransport.this) {
        	ObjectMapper mapper = new ObjectMapper();
        	ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            mapper.writeValue(byteStream, eventData);
            RawMessage rm=new RawMessage(byteStream.toByteArray());
            messageInput.processRawMessage(rm);
            LOG.debug("Published Message:"+rm);
            byteStream.close();
        }
    }

	  @Override
	  public void stop() 
	  {
		  try
		  {
			  hcInstance.shutdown();
		  }
		  catch (Exception e)
		  {
			  e.printStackTrace();
		  }
	    }
	  
	    @Override
	    public MetricSet getMetricSet() {
	        return null;
	    }

    @FactoryClass
    public interface Factory extends Transport.Factory<HCTransport> {

        @Override
        HCTransport create(Configuration configuration);

        @Override
        Config getConfig();
    }

    @ConfigClass
    public static class Config implements Transport.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
        	String hc="Hazelcast Configuration";
            final ConfigurationRequest cr = new ConfigurationRequest();

            cr.addField(new TextField(CK_CONFIG_GRUP_NAME,
                    "Grup Name",
                    "dev",
                    hc,
            		ConfigurationField.Optional.OPTIONAL));

            cr.addField(new TextField(CK_CONFIG_GRUP_PASS,
                    "Grup Password",
                    "dev",
                    hc,
                    ConfigurationField.Optional.OPTIONAL,
                    TextField.Attribute.IS_PASSWORD));

            cr.addField(new TextField(CK_CONFIG_MULTICAST,
                    "Multicasting",
                    "false",
                    hc,
                    ConfigurationField.Optional.OPTIONAL));

            cr.addField(new TextField(CK_CONFIG_INTERFACE,
                    "Network Interface",
                    "192.168.0.*",
                    hc,
                    ConfigurationField.Optional.OPTIONAL));

            cr.addField(new TextField(CK_CONFIG_QUEUE_NAME,
                    "Queue Name",
                    "loggerQ",
                    hc,
                    ConfigurationField.Optional.OPTIONAL));

            cr.addField(new NumberField(CK_CONFIG_PORT,
                    "Port Number",
                     5701,
                     hc,
                    ConfigurationField.Optional.OPTIONAL));
            
            cr.addField(new BooleanField(CK_CONFIG_AUTOPORT,
                    "Autoport Increase",
                    false,
                    hc ));
            return cr;
        }
    }
}
