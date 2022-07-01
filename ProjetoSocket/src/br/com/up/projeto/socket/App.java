package br.com.up.projeto.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class App {

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(1236);
		ServidorSocket servidor = new ServidorSocket(serverSocket);
		servidor.iniciaServidor();
	}

}
