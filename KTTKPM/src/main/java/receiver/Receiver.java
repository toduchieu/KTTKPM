package receiver;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class Receiver extends JFrame {
	
	private String text;
	private JTextField txtTin;
	public Receiver() {
		setTitle("Nhận thông tin");
		setSize(400, 200);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		buildUI();
	}
	
	public void buildUI() {
		JPanel p = new JPanel();
		JLabel lblManv = new JLabel("Thông tin nhận ");
		txtTin = new JTextField(15);
		p.add(lblManv);
		p.add(txtTin);
		add(p,BorderLayout.CENTER);
		try {
			String tam = queueReceiver();
			txtTin.setText(queueReceiver());
			txtTin.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String queueReceiver() throws Exception {
				BasicConfigurator.configure();
				Properties settings = new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				Context ctx = new InitialContext(settings);
				Object obj = ctx.lookup("ConnectionFactory");
				ConnectionFactory factory = (ConnectionFactory) obj;
				Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
				Connection con = factory.createConnection("admin", "admin");
				con.start();
				Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
		
				MessageConsumer receiver = session.createConsumer(destination);
				System.out.println("Ty was listened on queue...");
				receiver.setMessageListener(new MessageListener() {
								
					public void onMessage(Message msg) {
						try {
							if (msg instanceof TextMessage) {
								TextMessage tm = (TextMessage) msg;
								String txt = tm.getText();
								text = tm.getText();
								System.out.println("Nhan Duoc " + txt);
								txtTin.setText(queueReceiver());
							} else if (msg instanceof ObjectMessage) {								
								ObjectMessage om = (ObjectMessage) msg;
								System.out.println(om);
							}
						} catch (Exception e) {
							e.printStackTrace();
							}
						}
					});
					return text;
				}
}
