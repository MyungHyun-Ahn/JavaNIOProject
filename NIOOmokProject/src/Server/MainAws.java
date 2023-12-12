package Server;

public class MainAws {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("port 번호를 입력하여 다시 실행해주세요.");
		}
		AwsSelectServer awsServer = new AwsSelectServer(Integer.parseInt(args[0]));
		Thread thread = new Thread(awsServer);
		thread.start();
	}
}
