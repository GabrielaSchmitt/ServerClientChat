package br.com.up.projeto.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class ClienteHandler implements Runnable {
	
	public static ArrayList<ClienteHandler> clientHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String clientUser;
	
	public ClienteHandler(Socket socket) {
		try {
			this.socket = socket;
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientUser = socket.getRemoteSocketAddress().toString().replaceAll("/", "");
			clientHandlers.add(this);
		}catch(IOException e) {
			fechar(socket, bufferedReader, bufferedWriter);
		}
	}
	
	public void broadcastMensagem(String messageToSend, boolean isError) {
		for(ClienteHandler clienteHandler : clientHandlers) {
			try {
				if(isError == true) {
					if(clienteHandler.clientUser.equals(clientUser)) {
						clienteHandler.bufferedWriter.write(messageToSend);
						clienteHandler.bufferedWriter.newLine();
						clienteHandler.bufferedWriter.flush();
					}
				}else if(!clienteHandler.clientUser.equals(clientUser)) {
					clienteHandler.bufferedWriter.write(messageToSend);
					clienteHandler.bufferedWriter.newLine();
					clienteHandler.bufferedWriter.flush();
				}
			}catch(IOException e) {
				fechar(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	
	public void removerClienteHandler() {
		clientHandlers.remove(this);
	}
	
	public void fechar(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
		removerClienteHandler();
		try {
			if(bufferedReader != null) {
				bufferedReader.close();
			}
			if(bufferedWriter != null) {
				bufferedWriter.close();
			}
			if(socket != null) {
				socket.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String messageFromClient;
		String erro;
		int codigoErro;
		JSONObject jsonObject = null;
		JSONObject errorJsonObject = new JSONObject();
		String msgToSend;
		
		while(socket.isConnected()) {
			try {
				messageFromClient = bufferedReader.readLine();
				
				jsonObject = new JSONObject(messageFromClient);
				
				String identificador = jsonObject.getString("Identificador");			            
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Date data = formatter.parse(jsonObject.getString("Data"));
				String mensagem = jsonObject.getString("Mensagem");
				
				if(identificador.equals("")) {
					erro = "Identificador vazio";
					codigoErro = 2;
					errorJsonObject.put("Erro", erro);
					errorJsonObject.put("CodigoErro", codigoErro);
					
					msgToSend = errorJsonObject.toString();					
					
					broadcastMensagem(msgToSend, true);
					
					fechar(socket, bufferedReader, bufferedWriter);
					break;
				}else if(mensagem.equals("")) {
					erro = "Mensagem vazia";
					codigoErro = 4;
					errorJsonObject.put("Erro", erro);
					errorJsonObject.put("CodigoErro", codigoErro);
					
					msgToSend = errorJsonObject.toString();
					
					broadcastMensagem(msgToSend, true);
					
					fechar(socket, bufferedReader, bufferedWriter);
					break;
				}else {
					msgToSend = jsonObject.toString();
										
					broadcastMensagem(msgToSend, false);
				}				
			}catch(IOException e) {
				fechar(socket, bufferedReader, bufferedWriter);
				break;
			}catch(JSONException e) {
				try {
					erro = "Erro na formatação do objeto json";
					codigoErro = 1;
					errorJsonObject.put("Erro", erro);
					errorJsonObject.put("CodigoErro", codigoErro);
					
					msgToSend = errorJsonObject.toString();					
					
					broadcastMensagem(msgToSend, true);
					
					fechar(socket, bufferedReader, bufferedWriter);
					break;
				}catch(JSONException e1) {
					e1.printStackTrace();
				}				
			} catch (ParseException e) {
				try {
					erro = "Data vazia";
					codigoErro = 3;
					errorJsonObject.put("Erro", erro);
					errorJsonObject.put("CodigoErro", codigoErro);
					
					msgToSend = errorJsonObject.toString();					
					
					broadcastMensagem(msgToSend, true);
					
					fechar(socket, bufferedReader, bufferedWriter);
					break;
				}catch(JSONException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		
	}

}
