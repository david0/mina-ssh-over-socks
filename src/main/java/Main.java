import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClient.DirEntry;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;
import org.eclipse.jgit.internal.transport.sshd.JGitSshClient;
import org.eclipse.jgit.transport.sshd.ProxyData;

public class Main {

	public static void main(String[] args) throws IOException {
		JGitSshClient client = (JGitSshClient) ClientBuilder.builder().factory(JGitSshClient::new).build();
		client.start();

		String proxyHost = "localhost";
		int proxyPort = 3333;
		client.setProxyDatabase(remote -> new ProxyData(new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort))));

		ClientSession session = createSession(client);
		try (SftpClient sftp = DefaultSftpClientFactory.INSTANCE.createSftpClient(session)) {
			for (DirEntry entry : sftp.readEntries("/etc")) {
				System.out.println(entry.getFilename());
			}
		}

	}

	private static ClientSession createSession(JGitSshClient client) throws IOException {
		ClientSession session = client
				.connect("root", "target", 22)
				.verify(30, TimeUnit.SECONDS)
				.getSession();
		session.auth().verify(30, TimeUnit.SECONDS);
		return session;
	}
}
