package Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import Packet.PacketMessage;

// Serialize Util - 직렬화/역직렬화 유틸
public class SerializeUtil {
	public static byte[] serialize(PacketMessage data) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(data);
			byte[] serializedBytes = Jzlib.compress(baos.toByteArray());
			
			return serializedBytes;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error : serialize");
			return null;
		}
	}
	
	public static PacketMessage deserialize(byte[] bytes) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Jzlib.decompress(bytes));
		    ObjectInputStream ois = new ObjectInputStream(bais)) {
	        PacketMessage ret = (PacketMessage) ois.readObject();
	        
	        return ret;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error : deserialize");
			return null;
		}
	}
	
	// 패킷 헤더를 만들고 완성된 패킷을 반환
	public static ByteBuffer makeFullPacket(PacketMessage msg) {
		ByteBuffer ret = null;
		if( msg.getSerializedBytes() == null ) msg.setSerializedBytes(serialize(msg));
		
		byte[] sendData = msg.getSerializedBytes();
		byte[] size = CastingUtil.shortToByteArray((short)sendData.length);
		byte[] alldata = CastingUtil.combineByteArrays(size, sendData);
		ret = ByteBuffer.allocateDirect(alldata.length);
		ret.put(alldata);
		ret.flip();
		
		return ret;
	}
}
