package br.edu.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
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
public class Cliente{
	
	public static final String CONECTADO = "<!+1...con!>";
	public static final String DESCONECTADO = "<!-1...descon!>";
	public static final String ONLINE = "<!+1...on!>";
	public static final String OFFLINE = "<!-1...on!>";
	public static final String DIGITANDO = "<!dig...!>";
	public static final String NAO_DIGITANDO = "<!no_dig...!>";
	public static final String ERR_SERVIDOR = "<!Err...servidor!>";
	
	static class TelaConexaoServidor extends JFrame {

		private static final long serialVersionUID = 1L;
		private JTextField _portaField;
		private JTextField _localServerField;
		private JTextField _clienteField;
		private JButton _btnGo;

		public TelaConexaoServidor() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(300,280);
			setResizable(false);
			setLocationRelativeTo(null);
			setLayout(null);
			
			JPanel panel = new JPanel(new BorderLayout(0, 0));
			panel.setBorder(BorderFactory.createTitledBorder("Porta do Servidor"));
			panel.setBounds(6, 6, 150, 70);
			add(panel);
			
			_portaField = new JTextField(10);
			panel.add(_portaField, BorderLayout.CENTER);
			
			JPanel panel_1 = new JPanel(new BorderLayout(0, 0));
			panel_1.setBorder(BorderFactory.createTitledBorder("Local do Servidor"));
			panel_1.setBounds(6, 88, 150, 70);
			add(panel_1);
			
			_localServerField = new JTextField(10);
			panel_1.add(_localServerField, BorderLayout.CENTER);
			
			JPanel panel_2 = new JPanel(new BorderLayout(0, 0));
			panel_2.setBorder(BorderFactory.createTitledBorder("Nome do Cliente"));
			panel_2.setBounds(6, 170, 150, 70);
			add(panel_2);
			
			_clienteField = new JTextField(10);
			panel_2.add(_clienteField, BorderLayout.CENTER);
			
			_btnGo = new JButton("GO");
			_btnGo.setBounds(176, 82, 90, 90);
			add(_btnGo);
			
			setVisible(true);
			
		}

	}
	
	 static class TelaChat extends JFrame {

			private static final long serialVersionUID = 1L;
			
			private JTextField _textField;
			private JButton _btnEnviar;
			private JButton _btnSair;
			private JTextArea _textArea;
			private JLabel _labelConectados;
			private JLabel _labelOnline;
			private JLabel _labelSituacao;

			public TelaChat(String nome) {
				super(nome);
				setSize(450, 360);
				JPanel panel = new JPanel(null);
				panel.setBorder(BorderFactory.createTitledBorder("Campo de Texto"));
				panel.setPreferredSize(new Dimension(400, 80));
				add(panel, BorderLayout.SOUTH);
				
				_textField = new JTextField(18);
				_textField.setBounds(18, 28, 274, 38);
				panel.add(_textField);
				
				_btnEnviar = new JButton("Enviar");
				_btnEnviar.setBounds(304, 28, 62, 38);
				panel.add(_btnEnviar);
				
				_btnSair = new JButton("Sair");
				_btnSair.setBounds(367, 28, 50, 38);
				panel.add(_btnSair);
				
				JPanel panel_1 = new JPanel(null);
				panel_1.setPreferredSize(new Dimension(400, 100));
				panel_1.setBorder(BorderFactory.createTitledBorder("Status do Chat"));
				add(panel_1, BorderLayout.NORTH);
				
				JPanel panel_2 = new JPanel(new BorderLayout(0, 0));
				panel_2.setBorder(BorderFactory.createTitledBorder("Conectados"));
				panel_2.setBounds(8, 21, 88, 70);
				panel_1.add(panel_2);
				
				_labelConectados = new JLabel("1");
				_labelConectados.setHorizontalAlignment(SwingConstants.CENTER);
				_labelConectados.setFont(new Font("Arial", Font.BOLD, 26));
				_labelConectados.setForeground(Color.RED);
				panel_2.add(_labelConectados);
				
				JPanel panel_3 = new JPanel(new BorderLayout(0, 0));
				panel_3.setBorder(BorderFactory.createTitledBorder("Online"));
				panel_3.setBounds(108, 21, 78, 70);
				panel_1.add(panel_3);
				
				_labelOnline = new JLabel("0");
				_labelOnline.setHorizontalAlignment(SwingConstants.CENTER);
				_labelOnline.setForeground(Color.RED);
				_labelOnline.setFont(new Font("Arial", Font.BOLD, 26));
				panel_3.add(_labelOnline);
				
				JPanel panel_4 = new JPanel(new BorderLayout(0, 0));
				panel_4.setBorder(BorderFactory.createTitledBorder("Situação do Chat"));
				panel_4.setBounds(197, 20, 225, 70);
				panel_1.add(panel_4);
				
				_labelSituacao = new JLabel("Nada acontecendo");
				_labelSituacao.setHorizontalAlignment(SwingConstants.CENTER);
				_labelSituacao.setForeground(Color.RED);
				_labelSituacao.setFont(new Font("Arial", Font.BOLD, 18));
				panel_4.add(_labelSituacao, BorderLayout.CENTER);
				
				JScrollPane scrollPane = new JScrollPane(_textArea);
				scrollPane.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
						"Chat",
						TitledBorder.CENTER,
						TitledBorder.DEFAULT_POSITION));
				add(scrollPane, BorderLayout.CENTER);
				
				_textArea = new JTextArea();
				_textArea.setEditable(false);
				_textArea.setBackground(new Color(245, 245, 245));
				_textArea.setLineWrap(true);
				_textArea.setWrapStyleWord(true);
				
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				setVisible(true);
			}

			public void atualizarLabelConectados(String conectado) {
				int novo = Integer.parseInt(conectado);
				
				if(novo <= 1)
					_labelConectados.setForeground(Color.red);
				else
					_labelConectados.setForeground(Color.blue);
				_labelConectados.setText(conectado);
			}
			
			public void atualizarLabelOnline(String online) {
				
				int novo = Integer.parseInt(online);
				
				if(novo <= 1)
					_labelOnline.setForeground(Color.red);
				else
					_labelOnline.setForeground(Color.green);
				_labelOnline.setText(online);
			}
			
			public void atualizarLabelSituacao(String status) {
				if(status.equals(Cliente.DIGITANDO))
					_labelSituacao.setText("Algem está digitando...");
				else
					_labelSituacao.setText("Nada acontecendo");
			}	
	
	 }
	
	
	
	
	
	
	private TelaChat telaChat;
	private Socket socket;
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private BufferedWriter bufferedWriter;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	
	public Cliente(String nome, String host, int porta) throws UnknownHostException, IOException {
		socket = new Socket(host, porta);
		telaChat = new TelaChat(nome);
	}
	
	public void adicionarEventos() {
		
		telaChat.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {	sair();	} catch (IOException e1) {}
				System.exit(0);
			}
		});
		
		telaChat._textField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				try {enviar(OFFLINE);} catch (IOException e) {}				
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				try {	enviar(ONLINE);	} catch (IOException e) {}				
			}
		});
		
		telaChat._textField.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					try {
						enviar(telaChat._textField.getText());
					} catch (IOException ex) {}
				else
					try { enviar(DIGITANDO); } catch (IOException ex) {}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try { enviar(NAO_DIGITANDO); } catch (IOException e1) {}
			}
		});
		
		telaChat._btnEnviar.addActionListener(ActionEvent->{
			try {	enviar(telaChat._textField.getText());	} catch (IOException e) {}
		});
		
		telaChat._btnSair.addActionListener(ActionEvent->{
			try {	sair();	} catch (IOException e) {}
		});
	}
	
	public void conectar() throws IOException {
		outputStream = socket.getOutputStream();
		outputStreamWriter = new OutputStreamWriter(outputStream);
		bufferedWriter = new BufferedWriter(outputStreamWriter);
		bufferedWriter.write(telaChat.getTitle() + "\r\n");
		bufferedWriter.flush();
	}
	
	public void enviar(String msg) throws IOException {

		if (!socket.isClosed()) {

			if (msg.equals(DIGITANDO)) {
				bufferedWriter.write(msg + "\r\n");
			} else if (msg.equals(NAO_DIGITANDO)) {
				bufferedWriter.write(msg + "\r\n");
			} else if (msg.equals(DESCONECTADO)) {
				bufferedWriter.write(msg + "\r\n");
			} else if (msg.equals(CONECTADO)) {
				bufferedWriter.write(msg + "\r\n");
			} else if (msg.equals(ONLINE)) {
				bufferedWriter.write(msg + "\r\n");
			} else if (msg.equals(OFFLINE)) {
				bufferedWriter.write(msg + "\r\n");
			} else {
				bufferedWriter.write(msg + "\r\n");
				telaChat._textArea.append(telaChat.getTitle() + " ->>> " + msg + "\r\n");
				telaChat._textField.setText("");
			}

			bufferedWriter.flush();
		}
	}
	
	private void atualizarConectados(String msg) {
		String string[] = msg.split(":");
		if(msg.contains(CONECTADO))
			telaChat._textArea.append(string[0] + " Entrou no Chat \r\n");
		else
			telaChat._textArea.append(string[0] + " Saiu do Chat \r\n");
		telaChat.atualizarLabelConectados(string[2]);
	}
	
	private void atualizarOnline(String msg) {
		String string[] = msg.split(":");
		telaChat.atualizarLabelOnline(string[1]);
	}
	
	public void ouvir() throws IOException {
		
		inputStream = socket.getInputStream();
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		String msg = "";

		while (!ERR_SERVIDOR.equals(msg)) {
			if (socket.isClosed())
				break;
			if (bufferedReader.ready()) {
				msg = bufferedReader.readLine();
				if (msg.equals(ERR_SERVIDOR))
					telaChat._textArea.append("Servidor caiu! \r\n");
				else if (msg.contains(CONECTADO)) 
					atualizarConectados(msg);
				else if (msg.contains(DESCONECTADO))
					atualizarConectados(msg);
				else if (msg.contains(DIGITANDO))
					telaChat.atualizarLabelSituacao(DIGITANDO);
				else if (msg.contains(NAO_DIGITANDO))
					telaChat.atualizarLabelSituacao("");
				else if (msg.contains(ONLINE))
					atualizarOnline(msg);
				else if (msg.contains(OFFLINE))
					atualizarOnline(msg);
				else
					telaChat._textArea.append(msg + "\r\n");
			}
		}
		telaChat.atualizarLabelConectados("0");
		telaChat.atualizarLabelOnline("0");
		telaChat._textArea.append("Você Saiu do Chat \r\n");
	}
	
	public void sair() throws IOException {
		enviar(DESCONECTADO);
		outputStream.close();;
		outputStreamWriter.close();;
		bufferedWriter.close();;
		inputStream.close();;
		inputStreamReader.close();;
		bufferedReader.close();;
		socket.close();
	}
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {}
		
		TelaConexaoServidor telaConexaoServidor = new TelaConexaoServidor();
		telaConexaoServidor._clienteField.setText("Cliente");
		telaConexaoServidor._portaField.setText("9000");
		telaConexaoServidor._localServerField.setText("localhost");
		
		telaConexaoServidor._btnGo.addActionListener(ActionEvent->{
			
			String nome = telaConexaoServidor._clienteField.getText();
			String porta = telaConexaoServidor._portaField.getText();
			String host = telaConexaoServidor._localServerField.getText();
			
			if(nome.trim().equals("") 
					|| porta.trim().equals("") || host.trim().equals(""))
				Mensagem.aviso("Algum campo se encontra vazio");
			
			else {
				
				try {

					telaConexaoServidor.setVisible(false);
					telaConexaoServidor.dispose();
					
					Cliente cliente = new Cliente(nome, host, Integer.parseInt(porta));
					cliente.adicionarEventos();
					cliente.conectar();
					
					Runnable r = new Runnable() {
						@Override
						public void run() {
							try {
								cliente.ouvir();
							} catch (IOException e) {}
						}
					};
					
					new Thread(r).start();
					
					cliente.enviar(CONECTADO);
					
				} catch (NumberFormatException e) {
					Mensagem.err("Porta não é um numero valido!\n Encerrando Aplicação");
					System.exit(0);
				} catch (UnknownHostException e) {
					Mensagem.err("Servidor não encontrado!\n Encerrando Aplicação");
					System.exit(0);
				} catch (IOException e) {
					Mensagem.err("Servidor indisponivel!\n Encerrando Aplicação");
					System.exit(0);
				}
			}
		});
	}
	
}
