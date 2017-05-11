package kostiskag.unitynetwork.bluenode.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kostiskag.unitynetwork.bluenode.socket.trackClient.TrackerClient;

public class uploadKeyGUI {

	private JFrame frmUploadPublicKey;
	private JTextField textField;
	private JTextArea textArea;

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
		frmUploadPublicKey.setTitle("Offer/Revoke Public Key to Tracker");
		frmUploadPublicKey.setBounds(100, 100, 450, 448);
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
		btnNewButton.setBounds(291, 172, 133, 23);
		frmUploadPublicKey.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(157, 366, 154, 20);
		frmUploadPublicKey.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Tracker response");
		lblNewLabel_1.setBounds(12, 369, 133, 14);
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
		
		JButton btnNewButton_1 = new JButton("Revoke this Blue Node's Public Key");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				revoke();
			}
		});
		btnNewButton_1.setBackground(new Color(153, 51, 0));
		btnNewButton_1.setBounds(195, 319, 229, 25);
		frmUploadPublicKey.getContentPane().add(btnNewButton_1);
		
		JLabel lblif = new JLabel("<html>If you believe that your private key might be compromised you may revoke this bluenode's public key from the server in order to generate a new keypair and upload a new public key. When the public key is revoked the present bluenode may not be operational until a new one is set. In order to remove the public key you may click the button below.</html>");
		lblif.setBounds(10, 208, 410, 98);
		frmUploadPublicKey.getContentPane().add(lblif);
	}

	private void upload() {
		if (!textArea.getText().isEmpty()) {
			TrackerClient tr = new TrackerClient();
			String responce = tr.offerPubKey(textArea.getText());
			textField.setText(responce);
		}
	}
	
	private void revoke() {
		TrackerClient tr = new TrackerClient();
		String responce = tr.revokePubKey();
		textField.setText(responce);
	}
}
