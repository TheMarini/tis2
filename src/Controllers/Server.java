package Controllers;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class Server implements Container {

	static Service listaIC;

	public void handle(Request request, Response response) {
		try {

			String path = request.getPath().getPath();
			String method = request.getMethod();
			String mensagem;

			if (path.startsWith("/adicionarIC") && "POST".equals(method)) {
				mensagem = listaIC.addIC(request);
				this.enviaResposta(Status.CREATED, response, mensagem);
			} else {
				this.naoEncontrado(response, path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void naoEncontrado(Response response, String path) throws Exception {
		JSONObject error = new JSONObject();
		error.put("error", "Nao encontrado.");
		error.put("path", path);
		enviaResposta(Status.NOT_FOUND, response, error.toString());
	}

	private void enviaResposta(Status status, Response response, String str) throws Exception {

		PrintStream body = response.getPrintStream();
		long time = System.currentTimeMillis();

		response.setValue("Content-Type", "application/json");
		response.setValue("Server", "Tinfo - Controle de Ativos de TI (1.0)");
		response.setValue("Access-Control-Allow-Origin", "null");
		response.setDate("Date", time);
		response.setDate("Last-Modified", time);
		response.setStatus(status);

		if (str != null)
			body.println(str);
		body.close();
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		// json.put("id", id);
		// json.put("text", text);
		return json;
	}

	public static void main(String args[]) throws Exception {

		listaIC = new Service();

		int porta = 3000;

		Container container = new Server();
		ContainerSocketProcessor servidor = new ContainerSocketProcessor(container);
		Connection conexao = new SocketConnection(servidor);
		SocketAddress endereco = new InetSocketAddress(porta);
		conexao.connect(endereco);
		System.out.println("Tecle ENTER para interromper o servidor...");
		System.out.println("Abra o html da pagina no navegador para verificar o funcionamento do servidor ");
		System.in.read();

		conexao.close();
		servidor.stop();

	}

}
