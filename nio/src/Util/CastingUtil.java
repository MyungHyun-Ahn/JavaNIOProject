package Util;

public class CastingUtil {
	public static short byteArrayToShort(byte[] arr) {
		short ret = 0;
		
		ret = (short) (((arr[0] & 0xFF) << 8) + (arr[1] & 0xFF));
		
		return ret;
	}
	
	public static byte[] shortToByteArray(short n) {
		byte[] ret = new byte[2];
		
		ret[0] = (byte)((n>>8) & 0xFF);
		ret[1] = (byte)((n>>0) & 0xFF);
		
		return ret;
	}
	
	public static byte[] combineByteArrays(byte[] a, byte[] b) {
		byte[] ret = new byte[a.length + b.length];
		
		int j = 0;
		
		for (byte k : a) {
			ret[j] = k;
			j++;
		}
		
		for (byte k : b) {
			ret[j] = k;
			j++;
		}
		
		return ret;
	}
}
