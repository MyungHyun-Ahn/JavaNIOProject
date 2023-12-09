package Util;

// Casting Util
public class CastingUtil {
	// byte[] -> short 타입 변경
	public static short byteArrayToShort(byte[] arr) {
		short ret = 0;
		
		ret = (short) (((arr[0] & 0xFF) << 8) + (arr[1] & 0xFF));
		
		return ret;
	}
	
	// short -> byte[] 타입 변경
	public static byte[] shortToByteArray(short n) {
		byte[] ret = new byte[2];
		
		ret[0] = (byte)((n>>8) & 0xFF);
		ret[1] = (byte)((n>>0) & 0xFF);
		
		return ret;
	}
	
	// 2개의 바이트 배열을 이어 붙이는 함수
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
