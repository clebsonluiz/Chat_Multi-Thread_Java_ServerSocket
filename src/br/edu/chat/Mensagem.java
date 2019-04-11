package br.edu.chat;

import javax.swing.JOptionPane;

public class Mensagem {

	
	public static void aviso(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void err(String msg) {
		JOptionPane.showMessageDialog(null, msg, "Erro", JOptionPane.ERROR_MESSAGE);
	}
}
