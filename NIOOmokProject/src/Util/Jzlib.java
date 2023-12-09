package Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;

public class Jzlib {
	// 데이터를 압축
	@SuppressWarnings("deprecation")
	public static byte[] compress(byte[] data) {
		// System.out.println("compress : "+data.length);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ZOutputStream zOut = new ZOutputStream(out, JZlib.Z_BEST_COMPRESSION);
			ObjectOutputStream objOut = new ObjectOutputStream(zOut);
			objOut.writeObject(data);
			objOut.close();
			
			// System.out.println(out.toByteArray().length);
			return out.toByteArray();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error : Jzlib.compress");
			return null;
		}
	}
	
	// 데이터 압축 풀기
	@SuppressWarnings("deprecation")
	public static byte[] decompress(byte[] data) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ZInputStream zIn = new ZInputStream(in);
			ObjectInputStream objIn = new ObjectInputStream(zIn);
			byte[] ret = (byte[]) objIn.readObject();
			objIn.close();
			return ret;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error : Jzlib.decompress");
			return null;
		}
	}
}
