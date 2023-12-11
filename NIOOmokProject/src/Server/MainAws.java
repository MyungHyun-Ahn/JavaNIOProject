package Server;

public class MainAws {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("port 번호를 입력하여 다시 실행해주세요.");
		}
		new OmokServer(Integer.parseInt(args[0]));
	}
}
