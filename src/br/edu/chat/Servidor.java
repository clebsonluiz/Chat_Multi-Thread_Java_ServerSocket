package br.edu.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * @author Clébson Luiz
 * 
 * @category Educação
 * 
 * @version 1.0
 * 
 * Inspirado no tutorial de Multi-Chat com Sockets do Devmedia 
 * 
 * @see
 * <a href = "https://www.devmedia.com.br/como-criar-um-chat-multithread-com-socket-em-java/33639">
 * Link do Post do DevMedia </a> 
 * */

public class Servidor extends Thread{
	
	static class TelaInterruptor extends JFrame{
		
		static final long serialVersionUID = 1L;
		
		private JTextArea textArea;
		
		public TelaInterruptor(String porta) {
			super(porta);
			setLayout(new BorderLayout(10,10));
			setSize(new Dimension(300, 250));
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			JLabel label = new JLabel("Conectado na "+porta);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			
			add(label, BorderLayout.NORTH);
			
			JButton btn = new JButton("Fechar Servidor");
			btn.setFont(new Font("Arial", Font.BOLD, 15));
			
			btn.setForeground(Color.WHITE);
			btn.setBackground(new Color(255, 40, 40));
			
			btn.addActionListener((ActionEvent)->{
				JOptionPane.showMessageDialog(this, "Servidor Fechado");
				System.exit(0);
			});
			
			add(btn, BorderLayout.CENTER);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					JOptionPane.showMessageDialog(null, "Servidor Fechado");
					System.exit(0);
				}
			});
			
			textArea = new JTextArea();
			textArea.setEditable(false);
			textArea.setBackground(new Color(245, 245, 245));
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);

			JScrollPane scroll = new JScrollPane(textArea);
			scroll.setMinimumSize(new Dimension(250, 100));
			scroll.setPreferredSize(scroll.getMinimumSize());
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			add(scroll, BorderLayout.SOUTH);
			setVisible(true);
		}
		
		void novaConexao(String msg) {
			textArea.append("<Ouve uma requisição> \r\n");
			textArea.append("Dados do socket: "+ msg + "\r\n\n");
			requestFocusInWindow();
		}
	}
	
	
	static class TelaServidor extends JFrame{
		
		private static final long serialVersionUID = 1L;
		
		JTextField textField;
		JButton btnSelecionarPorta;
		
		public TelaServidor() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(220,110);
			setResizable(false);
			setLocationRelativeTo(null);
			
			textField = new JTextField();
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.setColumns(16);
			
			btnSelecionarPorta = new JButton("Selecionar porta");

			add(textField);
			add(btnSelecionarPorta);
			
			setVisible(true);
		}
	}
	
	public class Usuario {
		
		String nome;
		Socket socket;
		BufferedReader bfr;
		BufferedWriter bfw;
		
		public Usuario(Socket socket) throws IOException  {
			this.socket = socket;
			InputStream is = this.socket.getInputStream();
			InputStreamReader isr =	new InputStreamReader(is);
			this.bfr = new BufferedReader(isr);
			
			OutputStream os = this.socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			this.bfw = new BufferedWriter(osw);
			
			this.nome = this.bfr.readLine();
		}
	}
	
	static List<Usuario> clientes = new ArrayList<>();
	static int online = 0;
	
	public Servidor(Socket socket) throws IOException {
		Usuario user = new Usuario(socket);
		clientes.add(user);
	}
	
	private void atualizarChatOnline(String msg) throws IOException {
		if(msg.equals(Cliente.ONLINE))	online = online + 1;
		else if(msg.equals(Cliente.OFFLINE))	online = online - 1;
		
		for(Usuario cliente: clientes) 
			if(cliente.socket.isConnected())
				if(!cliente.socket.isClosed()) {
					cliente.bfw.write(msg + ":" + online + "\r\n");
					cliente.bfw.flush();
				}
	}
	
	private void atualizarChatConectado(Usuario usuario,String msg) throws IOException {
		if(msg.equals(Cliente.CONECTADO)) {
			
			for(Usuario cliente: clientes) {
				if(cliente.socket.isConnected())
					if(!cliente.socket.isClosed()) {
						cliente.bfw.write(
								usuario.nome + "->:" + msg + ":" + clientes.size() +"\r\n");
						System.out.println(usuario.nome + "->:" + msg + ":" + clientes.size());
						cliente.bfw.flush();
					}
			}
			
		}else if(msg.equals(Cliente.DESCONECTADO)) {
			clientes.remove(usuario);
			
			for(Usuario cliente: clientes) {
				if(cliente.socket.isConnected())
					if(!cliente.socket.isClosed()) {
						cliente.bfw.write(
								usuario.nome + "->:" + msg + ":" + clientes.size() +"\r\n");
						System.out.println(usuario.nome + "->:" + msg + ":" + clientes.size());
						cliente.bfw.flush();
					}
			}
		}
	}
	
	public void atualizarChat(Usuario usuario, String msg) throws IOException {
		for(Usuario cliente: clientes) {
			if(cliente.socket.isConnected()) {
				if(!cliente.socket.isClosed()) {
					if(msg.equals(Cliente.ONLINE) || msg.equals(Cliente.OFFLINE)) 
						atualizarChatOnline(msg);
					else if(msg.equals(Cliente.ERR_SERVIDOR)) {
						cliente.bfw.write(msg + " \r\n");
						cliente.bfw.flush();
					}
					else if(!cliente.equals(usuario)) {
						cliente.bfw.write(usuario.nome + "->" + msg + " \r\n");
						cliente.bfw.flush();
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {

			Usuario usuario = clientes.get(clientes.size()-1);
			String msg = "";
			
			while((msg = usuario.bfr.readLine()) != null) {
				if(msg.equals(Cliente.CONECTADO) || msg.equals(Cliente.DESCONECTADO))
					atualizarChatConectado(usuario, msg);
				else
					atualizarChat(usuario, msg);
				System.out.println(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {atualizarChat(null, Cliente.ERR_SERVIDOR);} catch (IOException e1) {}
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {}
		
		TelaServidor telaServidor = new TelaServidor();
		telaServidor.textField.setText("9000");
		telaServidor.btnSelecionarPorta.addActionListener(ActionEvent->{
			try {
							
				int porta = Integer.parseInt(telaServidor.textField.getText());
				telaServidor.dispose();
				ServerSocket serverSocket = new ServerSocket(porta);
				
				TelaInterruptor telaInterruptor = new TelaInterruptor("Porta :"+porta);
				
				new Thread(() -> {
					while (true) {
						try {
							System.out.println("Esperando...");
							Socket socket = serverSocket.accept();
							telaInterruptor.novaConexao(socket.toString());
							Thread servidor = new Servidor(socket);
							servidor.start();
						} catch (IOException e) {}
					}
				}).start();
				
			} catch (NumberFormatException e) {
				Mensagem.err("Valor não é valido para Ser porta de servidor");
			} catch (IOException e) {
				Mensagem.err("Erro ao iniciar servidor, encerrando aplicação");
				System.exit(0);
			} 
		});
	}
}
