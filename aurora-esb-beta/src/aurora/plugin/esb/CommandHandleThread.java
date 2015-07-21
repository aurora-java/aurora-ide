package aurora.plugin.esb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandHandleThread extends Thread {

	// {
	// String content = "";
	// try {
	// BufferedReader in = new BufferedReader(
	// new InputStreamReader(socket.getInputStream()));
	// while (true) {
	// if (!socket.isClosed()) {
	// if (socket.isConnected()) {
	// if (!socket.isInputShutdown()) {
	// if ((content = in.readLine()) != null) {
	// content += "\n";
	// System.out.println(content);
	// } else {
	//
	// }
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	ESBStarter mOwner;
	Socket mSocket;
	InputStream mInput;
	OutputStream mOutput;

	boolean mRunning = true;

	public CommandHandleThread(ESBStarter owner, Socket socket)
			throws IOException {
		mOwner = owner;
		mSocket = socket;
		mInput = mSocket.getInputStream();
		mOutput = mSocket.getOutputStream();

	}

	public void run() {
		while (mRunning) {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(mInput));
				OutputStreamWriter out = new OutputStreamWriter(mOutput);
				PrintWriter writer = new PrintWriter(out, true);
				String command = reader.readLine();
				if (command == null)
					continue;
				command = command.trim();
				if ("stop".equalsIgnoreCase(command)) {
					writer.println("Shutting down...");
					System.out.println("Shutting down...");
					mOwner.doShutdown();
					writer.println("OK");
					System.out.println("OK");
					return;
				} else if ("exit".equalsIgnoreCase(command)) {
					writer.println("Bye!");
					mOwner.removeClient(this);
					return;
				} else {
					writer.println("Unkown command:" + command);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	void closeStream(InputStream s) {
		if (s != null)
			try {
				s.close();
			} catch (IOException ex) {

			}
	}

	void closeStream(OutputStream s) {
		if (s != null)
			try {
				s.close();
			} catch (IOException ex) {

			}
	}

	public void closeClient() {
		mOwner.removeClient(this);
	}

	private void c() {

		closeStream(mInput);
		closeStream(mOutput);
		if (mSocket != null)
			try {
				mSocket.close();
			} catch (IOException ex) {

			}

	}

	public void clearUp() {
		closeStream(mInput);
		closeStream(mOutput);
		if (mSocket != null)
			try {
				mSocket.close();
			} catch (IOException ex) {

			}
	}

}
