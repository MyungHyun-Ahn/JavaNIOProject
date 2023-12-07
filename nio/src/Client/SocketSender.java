package Client;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

import Packet.PacketMessage;
import Util.SerializeUtil;

public class SocketSender implements Runnable {
	private SocketChannel channel = null;
	private Queue<PacketMessage> outPacketQueue = null;
	
	public SocketSender(SocketChannel channel) {
		this.outPacketQueue = new LinkedList<PacketMessage>();
		this.channel = channel;
	}

	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		while (true) {
			PacketMessage msg = outPacketQueue.poll();
			
			if (msg == null)
			{
				try {
					this.wait();
				}
				catch (InterruptedException e) {
					return;
				}
				continue;
			}
			
			if (!Write(msg)) return;
		}
	}

	public boolean Write(PacketMessage msg) {
		ByteBuffer buf = SerializeUtil.makeFullPacket(msg);
		
		try {
			channel.write(buf); // 보낸다!
			buf.rewind(); // bytebuf 의 position 초기화 
		} 
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Send Success!");
		buf.clear();
		return true;
	}
	 
	public synchronized void sendPacket(PacketMessage msg) {
		this.outPacketQueue.offer(msg);
		this.notify();
	}
}
