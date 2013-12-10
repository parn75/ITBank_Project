package backup1127;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
//GUI
//event

class Client extends Thread {
	private static final long serialVersionUID = 1L;
	Socket socket ; //서버와 통신

	ObjectOutputStream oos;
	ObjectInputStream ois;

	public Client(){		//객체 생성 및 배치
		try{ //서버와 연결하는 구문
			socket = new Socket("localhost", 5432);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		}catch (Exception e){
			e.getMessage();
		}
	}

	@Override
	public void run(){
		try{ 

			Object obj=null;			
			
			while((obj=ois.readObject())!=null) {				
				if(obj instanceof String) {
					ClientUI.txtChat.append((String)obj + "\n");				
				}else if (obj instanceof String[]) {					
					String[] str = (String[])obj;					
					for(int i=0;i<str.length;i++) {						
						if(!ClientUI.listModel.contains(str[i])) ClientUI.listModel.addElement(str[i]);
					}
				}
			}
			
		}catch (IOException | ClassNotFoundException e){
			e.getMessage();
		}
	}
	
	public void send(Object obj) {
		try {
			oos.writeObject(obj);			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new Client();
	}
}