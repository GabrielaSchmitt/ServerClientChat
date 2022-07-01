package br.com.up.projeto.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {
	
	private ServerSocket serverSocket;
	
	public ServidorSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void iniciaServidor() {			
		try {		
			while(!serverSocket.isClosed()) {				
				try {
					Socket socket = serverSocket.accept();
					System.out.println("Novo cliente conectado.");
					ClienteHandler clienteHandler = new ClienteHandler(socket);
					
					Thread thread = new Thread(clienteHandler);
					thread.start();					
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void fechaServidorSocket() {
		try {
			if(serverSocket != null) {
				serverSocket.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
