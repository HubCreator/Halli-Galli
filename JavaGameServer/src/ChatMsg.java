
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
	public String userName;
	public String data;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public String room_dst;
	
	public static class ChatMsgBuilder {
		private static final long serialVersionUID = 1L;
		public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
		public String userName;
		public String data;
		public ImageIcon img;
		public MouseEvent mouse_e;
		public String room_dst;

		
		public ChatMsgBuilder(String code, String userName) {
			this.code = code;
			this.userName = userName;
		}
		public ChatMsgBuilder data(String data) {this.data = data; return this;}
		public ChatMsgBuilder img(ImageIcon img) {this.img = img; return this;}
		public ChatMsgBuilder mouse_e(MouseEvent mouse_e) {this.mouse_e = mouse_e; return this;}
		public ChatMsgBuilder room_dst(String room_dst) {this.room_dst = room_dst; return this;}
		public ChatMsg build() {
			ChatMsg chatmsg = new ChatMsg();
			chatmsg.code = this.code;
			chatmsg.userName = this.userName;
			chatmsg.data = this.data;
			chatmsg.img = this.img;
			chatmsg.mouse_e = this.mouse_e;
			chatmsg.room_dst = this.room_dst;
			return chatmsg;
		}
	}

	
}