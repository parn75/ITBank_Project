package beforechangingChatData;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

public class ClientAction implements ActionListener{
	ClientUI clientUI;
	Client client;
	
	public ClientAction(ClientUI clientUI) {
		this.clientUI = clientUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==clientUI.btnConnect || e.getSource() == clientUI.fldID) {  //접속 버튼을 클릭한 경우
			client = new Client(clientUI);
			client.start(); //클라이언트 스레드 시작
			client.send("*****[ID:"+clientUI.fldID.getText()+"]*****"); //ID 전송
			clientUI.fldID.setEnabled(false);
			clientUI.btnConnect.setEnabled(false);
		}else if(e.getSource()==clientUI.btnPersonalMsg || e.getSource()==clientUI.fldChat) { //귓속말
			
			List<String> l = clientUI.lstConnector.getSelectedValuesList();
			String[] str = new String[l.size()];
			l.toArray(str);
			String msg = clientUI.fldChat.getText();			
			client.send(new ChatData(str,msg));
			if(l.size()>0) {
				//clientUI.txtChat.append("to ");
				try {
					clientUI.doc.insertString(clientUI.doc.getLength(), "to ", clientUI.sc.getStyle("MainSytle"));
					for(int i=0;i<str.length;i++) {						
						clientUI.doc.insertString(clientUI.doc.getLength(), str[i] + ",", clientUI.sc.getStyle("MainSytle"));
					}					
					clientUI.doc.insertString(clientUI.doc.getLength(), ":"+ msg + "\n", clientUI.sc.getStyle("MainSytle"));
				}catch (BadLocationException be) {
					be.printStackTrace();
				}
				clientUI.endScroll();
			}
			clientUI.fldChat.setText("");
		}else if(e.getSource() == clientUI.photoMenuItem) { //사진 전송
			FileDialog fd = new FileDialog(clientUI, "Select Image", FileDialog.LOAD);
			URL url = null;
		    fd.setVisible(true);
		    String filename = fd.getDirectory()+fd.getFile();
		   
		    File f = new File(filename);
		    try {
				url = f.toURI().toURL();
			} catch (MalformedURLException exception) {			
				exception.printStackTrace();
			}
		    ImageIcon icon = new ImageIcon(url);
		    client.send(icon);
		}else if(e.getSource() == clientUI.fileMenuItem) { //파일 전송
			FileDialog fd = new FileDialog(clientUI, "Select Image", FileDialog.LOAD);
			URL url = null;
		    fd.setVisible(true);
		    String filename = fd.getDirectory()+fd.getFile();
		    FileTransferServer fts = new FileTransferServer(filename);
		    fts.start();
		    client.send("*****[filename:" + filename + "]*****");			
		}
		
	}

}
