package kostiskag.unitynetwork.bluenode.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

import kostiskag.unitynetwork.bluenode.socket.trackClient.TrackerClient;

import java.awt.Font;
import java.awt.TextArea;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class uploadKeyGUI {

	private JFrame frmUploadPublicKey;
	private JTextField textField;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					uploadKeyGUI window = new uploadKeyGUI();
					window.frmUploadPublicKey.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public uploadKeyGUI() {
		initialize();
	}
	
	public void setVisible() {
		frmUploadPublicKey.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUploadPublicKey = new JFrame();
		frmUploadPublicKey.setTitle("Upload Public Key to Tracker");
		frmUploadPublicKey.setBounds(100, 100, 450, 287);
		frmUploadPublicKey.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmUploadPublicKey.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("<html>In order to upload this bluenode's public key to the network, you should provide its session ticket generated by the tracker. A tracker's admin should be ble to send your session ticket when requested.</html>");
		lblNewLabel.setBounds(10, 11, 414, 56);
		frmUploadPublicKey.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Upload Public Key");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				upload();
			}			
		});
		btnNewButton.setBounds(307, 172, 117, 23);
		frmUploadPublicKey.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(109, 213, 154, 20);
		frmUploadPublicKey.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Tracker response");
		lblNewLabel_1.setBounds(10, 216, 89, 14);
		frmUploadPublicKey.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Paste your session ticket here:");
		lblNewLabel_2.setForeground(new Color(153, 51, 0));
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(10, 66, 191, 14);
		frmUploadPublicKey.getContentPane().add(lblNewLabel_2);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setBounds(10, 91, 414, 70);
		frmUploadPublicKey.getContentPane().add(textArea);
	}
	
	private void upload() {
		TrackerClient tr = new TrackerClient();
		String responce = tr.offerPubKey(textArea.getText());
		textField.setText(responce);
	}
}
