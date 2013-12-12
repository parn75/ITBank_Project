package network12_12;

import java.awt.FileDialog;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;

//Ŭ���̾�Ʈ���� �����͸� �޾Ƽ� ó���ϴ� Ŭ����
class ServerThread extends Thread{
	ServerUI serverUI;
	Server server;//������ ����
	Socket socket ; //Ŭ���̾�Ʈ
	ObjectOutputStream oos;
	ObjectInputStream ois;
	int thisRoomNum = -1;

	public ServerThread(Server server, Socket socket, ServerUI serverUI){
		this.server = server ; //������ ����
		this.socket = socket ; //client
		this.serverUI = serverUI;

		try{
		    oos = new ObjectOutputStream(socket.getOutputStream());
		    ois = new ObjectInputStream(socket.getInputStream());
		}catch (IOException ioe){
			ioe.getMessage();
		}
		
	}//������

	//Ŭ���̾�Ʈ���� ��ü�� �������ִ� �޼ҵ�	(�ؽ�Ʈ�� ��쿡�� String ��ü�� ����)
	public void send(Object obj) {
		try {
			oos.writeObject(obj);
			oos.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}	
	public boolean isContain(String[] str, String search) {
		for(int i=0;i<str.length;i++) if(str[i].equals(search)) return true;
		return false;
	}
	public void toSelected(ChatData fromChat) { //����Ʈ���� ���õ� Ŭ���̾�Ʈ Ȥ�� õü �޼��� ����		
		fromChat.from = this.getName()+":";
		if(fromChat.type == ChatType.Whisper && fromChat!=null && fromChat.to.length>0 && isContain(fromChat.to, "To All")==true) {   //chatdata�� to �ʵ忡 ���𰡰� ���� ��� => Ŭ���̾�Ʈ���� �޼��� ���۽� ����Ʈ���� ���� ����� ���� => �� �ӼӸ��� ���
			fromChat.type = ChatType.Broadcast;
			server.broadcast(fromChat);
			return;			
		}else if (fromChat!=null && fromChat.to.length>0 && isContain(fromChat.to, "To All")==false) {			
			fromChat.from = "from " + this.getName() + ":";  //�ӼӸ��� ������ ����  ���� ���´��� ����(ex - from com1:)
			server.send(fromChat);
		}
		else{  //��ü �޼���
			System.out.println("sending broad");			
			server.broadcast(fromChat);				
		}
	}
	
	public void identifyID(ChatData cd) { //ID�� �߷����� ó���ϴ� �޼ҵ�
		String ID = (String) cd.data;  //<= REALID �κ��� �и��Ͽ� ID�� ����
		serverUI.listModel.addElement(ID);  //����Ʈ�� ID�߰�
		this.setName(ID); //Thread�� �̸��� ID�� ���� => ID�ߺ� �Ұ�
		server.clientMap.put(ID, this);  
		server.reverseClientMap.put(this, ID);
		server.sendConnectorList(); //�� Ŭ���̾�Ʈ�� ���� ������ ����Ʈ ����
		server.broadcast(new ChatData(ChatType.Broadcast,"Server:", "["+ID+"]���� �����ϼ̽��ϴ�."));
	}
	
	public void getFile(String filename, String from) { //������ �޴� �޼ҵ� //�ʿ� ����
		int confirm = JOptionPane.showConfirmDialog(serverUI, from+"���� ������ �����̽��ϴ�. �����Ͻðڽ��ϱ�?", "choose one", JOptionPane.YES_NO_OPTION);
		if(confirm == JOptionPane.OK_OPTION) {
			FileDialog fd = new FileDialog(serverUI, "Save as", FileDialog.SAVE);
		    fd.setVisible(true);
		    String fname = fd.getDirectory()+fd.getFile();
		    System.out.println(fname);
		    
		    try {
		    	if(fname!= null && fd.getDirectory()!= null && fd.getFile()!=null && fname!="") {
		    		FileTransferClient ftc;				
					ftc = new FileTransferClient(InetAddress.getLocalHost(), fname);
					ftc.start();				    	
		    	} else {
					serverUI.doc.insertString(serverUI.doc.getLength(), "������ ��ҵǾ����ϴ�!", serverUI.sc.getStyle("MainSytle"));
				} 
		    }catch (UnknownHostException|BadLocationException e) {					
					e.printStackTrace();
			}		   	   
		}//end if confirm
	}
	
	public void showImage(ChatData cd){ //�ʿ� ����
		System.out.println("Got ImageIcon");
		ImageIcon icon = (ImageIcon)cd.data; 
	    StyleConstants.setIcon(serverUI.chatStyle.imageStyle, icon);
	    
	    try {
	    	//StyledEditorKit sek; //�̹��� ũ�⸦ �����ϱ����ؼ��� ������Ŷ�� �̿��ؾ� �ҵ�	
	    	serverUI.doc.insertString(serverUI.doc.getLength(), "Image from " + cd.from  + "\n", serverUI.chatStyle.mainStyle);
	    	serverUI.doc.insertString(serverUI.doc.getLength(), "ignored text\n", serverUI.chatStyle.imageStyle);	    	   	
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}	    
	    serverUI.endScroll();
		
	}
	
	private void join(ChatData cd) {
		if (serverUI.roomList.joinRoom((int)cd.data, this.getName()) == true) {			
			ChatData rd = new ChatData(ChatType.Join, "Success");
			thisRoomNum = (int)cd.data;
			server.waitingRoomList[(int)cd.data].addPlayer(this.getName());
			server.send(rd, this);
		}else {
			//String[] to = {this.getName()};
			ChatData rd = new ChatData(ChatType.Join, "���� ����á���ϴ�.");
			server.send(rd, this);
		}
	}
	

	private void waitingRoomChat(ChatData cd) { //���� �ȿ��� ä��
		System.out.println("���� ä�� ������");
		ChatData rd = new ChatData(ChatType.WaitingRoomChat, cd.data);
		String[] to = server.waitingRoomList[thisRoomNum].getPlayers();		
		rd.setTo(to); 
		rd.setFrom(this.getName() + ":");
		server.send(rd);
	}
	

	private void waitingRoomExit(ChatData cd) { //���� ����
		serverUI.roomList.deletePlayer(thisRoomNum, this.getName());		
	}

	//Ŭ���̾�Ʈ�κ��� �޼����� �޴� ������
	@Override
	public void run(){
		try{
			Object obj=null;
			while((obj=ois.readObject())!=null) {
				ChatData cd = (ChatData)obj;
				
				switch(cd.type) {				
				case ConnectorList:	break;				
				case ID: identifyID(cd); break;
				case File: 				
				case Image:
				case Whisper: 				
				case Broadcast: toSelected(cd);	break;
				case Join: join(cd); break;
				case WaitingRoomChat: waitingRoomChat(cd); break;
				case WaitingRoomExit: waitingRoomExit(cd); break;
				default:
					break;

				}
			}		
		}catch (IOException | ClassNotFoundException e){			
				server.removeThread( this.getName() ); //������ ���� ���(���� �� ���� ���) ������ �� ����Ʈ ����
				//server.broadcast("[" + this.getName() + "]���� �����̽��ϴ�.");
				server.broadcast(new ChatData(ChatType.Broadcast, "Server: ", "[" + this.getName() + "]���� �����̽��ϴ�."));
				System.out.println(socket.getInetAddress() + "�� ������ ����Ǿ����ϴ�.");		
		}	
	}


}

class WaitingRoomList {
	int roomNum;
	int playerNum=0;
	String[] players = new String[4];
	
	public int getRoomNum() {
		return roomNum;
	}
	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}
	public String[] getPlayers() {
		return players;
	}
	public void setPlayers(String[] players) {
		this.players = players;
	}
	public void addPlayer(String player) {
		this.players[playerNum] = player;
		playerNum++;
	}
	public void deletePlayer(String player) {
		for(int i=0;i<players.length;i++) {
			if(players[i].equals(player)) {
				for(int j=i;j<players.length-1;j++) 
					if(players[j+1] != null || players[j+1] !="None") players[j] = players[j+1];
					else players[j] = "None";
				playerNum--;					
			}
		}
	}
}