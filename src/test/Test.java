package test;

import java.util.ArrayList;
import java.util.List;
import com.ichat.mode.ChatMsgEntity;
import com.ichat.util.Out;

public class Test {
	public static void main(String[] args) {
		ChatMsgEntity msg=new ChatMsgEntity("2134", "��������", true);
		List<ChatMsgEntity> list=new ArrayList<ChatMsgEntity>();
		list.add(msg);
		msg.setText("��������22");
		Out.println(list.get(0).getText());
	}
}
