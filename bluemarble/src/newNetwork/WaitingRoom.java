package newNetwork;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

public class WaitingRoom extends JFrame implements ActionListener, WindowListener{
	public boolean debug = true;
	private final int XSIZE = 500, YSIZE = 550;
	private static final long serialVersionUID = -8584629477552591872L;
	
	JLabel lblHost = new JLabel("Host Name ");
	JTextField hostname = new JTextField();
	JLabel lblID = new JLabel("ID ");
	JTextField fldID = new JTextField();
	JButton btnConnect = new JButton("Connect");	
	JButton btnPersonalMsg = new JButton("Send");	
	JTextField fldChat = new JTextField();
	JList<String> lstConnector = new JList<String>();
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	JPanel pnlBroadcast = new JPanel();
	JPanel pnlConnect = new JPanel();
	
	//ä�� �� �̹��� ǥ�� �κ�, ��ũ�� ���� �� ��Ÿ�� ����
	BufferStrategy bs;
	JPanel middleP = new JPanel();
	JPanel middleP2 = new JPanel();
	StyleContext sc = new StyleContext();
    final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
	JTextPane txtChat = new JTextPane(doc);
	JScrollPane sp;
	JScrollPane roomScroll;
	ChatDocStyles chatStyle;
	
	//�޴��� ����
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("����");	
	JMenuItem photoMenuItem = new JMenuItem("���� ������");
	JMenuItem fileMenuItem = new JMenuItem("���� ����");
	
	ClientAction clientAction;
	Client client;
	WaitingRoomStatus wrs;
	JLabel[] players = new JLabel[4];

	private void start() {		
		btnConnect.addActionListener(this);		
		btnPersonalMsg.addActionListener(this);
		photoMenuItem.addActionListener(this);
		fileMenuItem.addActionListener(this);
		fldID.addActionListener(this);
		fldChat.addActionListener(this);		
	}
	
	public void endScroll() {  //Scroll�� ���� �Ʒ��� ������ ���� �޼ҵ�		
		int pos = doc.getLength();		
		txtChat.setCaretPosition(pos);
		//txtChat.requestFocus();
	}
	
	private void init() {
		Container con = this.getContentPane();
		con.setLayout(new BorderLayout());
		pnlConnect.setLayout(new BoxLayout(pnlConnect,BoxLayout.X_AXIS));
		pnlConnect.add(lblHost);
		pnlConnect.add(hostname);
		pnlConnect.add(lblID);
		pnlConnect.add(fldID);
		pnlConnect.add(btnConnect);
		con.add(pnlConnect, "North");		
	
		middleP.setLayout(new GridLayout(5,1));

		
		for(int i=0;i<4;i++) {
			if(wrs.playerNames[i]!=null) players[i] = new JLabel(wrs.playerNames[i]); else players[i] = new JLabel("None");
			middleP.add(players[i]);
		}
		
		txtChat.setEditable(false);
		middleP.add(txtChat);
		sp = new JScrollPane(txtChat);  //Adding Scroll Bar		
		middleP.add(sp);

		con.add(middleP, "Center");	
	
		chatStyle = new ChatDocStyles(sc);

		pnlBroadcast.setLayout(new BoxLayout(pnlBroadcast,BoxLayout.X_AXIS));		
		pnlBroadcast.add(fldChat);
		pnlBroadcast.add(btnPersonalMsg);		
		con.add(pnlBroadcast,"South");
		
		listModel.addElement("To All");
		lstConnector = new JList<String>(listModel);
		con.add(lstConnector, "East");
		lstConnector.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
		fileMenu.add(photoMenuItem);
		fileMenu.add(fileMenuItem);
		menuBar.add(fileMenu);		
		this.setJMenuBar(menuBar);	
	}
	
	public WaitingRoom(WaitingRoomStatus wrs, Client client) {
		super(wrs.title);
		this.wrs = wrs;
		this.client = client;
		init();
		start();
		this.setSize(XSIZE,YSIZE);
		this.setResizable(false);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.addWindowListener(this);
		setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("windowClosing");
		client.startWaitingRoom = false;
		client.inWaitingRoom = false;
		client.clientUI.setVisible(true);
		this.dispose();
				
	}

	@Override
	public void windowClosed(WindowEvent e) {		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}