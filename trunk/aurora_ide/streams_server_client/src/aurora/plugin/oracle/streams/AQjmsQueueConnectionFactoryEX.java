package aurora.plugin.oracle.streams;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import oracle.jms.AQjmsQueueConnectionFactory;

public class AQjmsQueueConnectionFactoryEX extends AQjmsQueueConnectionFactory {
 
	private static final long serialVersionUID = 7754779809387544710L;
	private AQjmsQueueConnectionFactory connectionFactory;
	private StreamsListener streamsListener;

	public AQjmsQueueConnectionFactoryEX(
			ConnectionFactory connectionFactory) {
		this.connectionFactory = (AQjmsQueueConnectionFactory)connectionFactory;
	}

	public AQjmsQueueConnectionFactoryEX(ConnectionFactory aqConnectionFactory,
			StreamsListener streamsListener) {
		this(aqConnectionFactory);
		this.streamsListener = streamsListener;
	}

	public Connection createConnection() throws JMSException {
		return connectionFactory.createConnection(streamsListener.getStreamUser(),streamsListener.getPassword());
	}
}
