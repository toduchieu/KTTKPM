package sender;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;
import receiver.Receiver;

public class Sender extends JFrame implements ActionListener {
	
	private Button btn;
	private JTextField txtTin;
	
	public Sender() {
		setTitle("Gửi thông tin");
		setSize(400, 200);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		buildUI();
		
	}
	
	public void buildUI() {
		JPanel p = new JPanel();
		JLabel lblManv = new JLabel("Nhập thông tin");
		txtTin = new JTextField(15);
		btn = new Button("Gửi");
		p.add(lblManv);
		p.add(txtTin);
		p.add(btn);
		add(p,BorderLayout.CENTER);
		btn.addActionListener(this);
		
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if(o.equals(btn)) {
			
			try {
				QueueSender();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		Receiver rcv = new Receiver();
		rcv.setVisible(true);
		}
		
	}
	
	public void QueueSender()  throws Exception{
		 
	//config environment for JMS
			BasicConfigurator.configure();
	//config environment for JNDI
			Properties settings = new Properties();
			settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
	//create context
			Context ctx = new InitialContext(settings);
	//lookup JMS connection factory
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
	//lookup destination. (If not exist-->ActiveMQ create once)
			Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
	//get connection using credential
			Connection con = factory.createConnection("admin", "admin");
	//connect to MOM
			con.start();
	//create session
			Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
	//create producer
			MessageProducer producer = session.createProducer(destination);
	//create text message
			Message msg = session.createTextMessage(txtTin.getText());
			producer.send(msg);
			Person p = new Person(1001, "Thân Thị Đẹt", new Date());
			String xml = new XMLConvert<Person>(p).object2XML(p);
			msg = session.createTextMessage(xml);
			producer.send(msg);
	//shutdown connection
			session.close();
			con.close();
			System.out.println("Finished...");
	}

}
