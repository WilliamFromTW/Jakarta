package inmethod.jakarta.pdf.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import inmethod.commons.util.AppDataConfig;
import inmethod.jakarta.pdf.EncryptPDF;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AppEncryptPDF {

	private JFrame frame;
	private JTextField txtSource;
	private JTextField txtDest;
	private JPasswordField passwordUser;
	private JPasswordField passwordOwner;
	private AppDataConfig aAppDataConfig;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppEncryptPDF window = new AppEncryptPDF();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppEncryptPDF() {
		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		aAppDataConfig = new AppDataConfig("JakartaPDFConfig");
		if( aAppDataConfig.getKeyValue("source")==null)
		  aAppDataConfig.setKeyValue("source",".\\source");
		if( aAppDataConfig.getKeyValue("dest")==null)
		  aAppDataConfig.setKeyValue("dest",".\\dest");
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 560, 356);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel label = new JLabel("來源路徑(必)");
		label.setFont(new Font("Dialog", Font.BOLD, 16));
		label.setBounds(12, 75, 181, 31);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("目標路徑(必)");
		label_1.setFont(new Font("Dialog", Font.BOLD, 16));
		label_1.setBounds(12, 107, 181, 20);
		frame.getContentPane().add(label_1);

		JLabel lblPdf = new JLabel("PDF文件保全設定");
		lblPdf.setHorizontalAlignment(SwingConstants.CENTER);
		lblPdf.setForeground(Color.BLUE);
		lblPdf.setFont(new Font("Dialog", Font.BOLD, 24));
		lblPdf.setBounds(162, 12, 258, 57);
		frame.getContentPane().add(lblPdf);

		JLabel label_2 = new JLabel("設定文件開啟密碼(選)");
		label_2.setFont(new Font("Dialog", Font.BOLD, 16));
		label_2.setBounds(12, 168, 181, 20);
		frame.getContentPane().add(label_2);

		JLabel label_3 = new JLabel("設定文件保全密碼(必)");
		label_3.setFont(new Font("Dialog", Font.BOLD, 16));
		label_3.setBounds(12, 200, 181, 20);
		frame.getContentPane().add(label_3);

		txtSource = new JTextField();
		txtSource.setText(aAppDataConfig.getKeyValue("source"));
		txtSource.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(txtSource.getText()));
				chooser.setDialogTitle("請選擇目錄");
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getSelectedFile());
					txtSource.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		txtSource.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtSource.setBounds(132, 75, 388, 31);
		frame.getContentPane().add(txtSource);
		txtSource.setColumns(10);

		txtDest = new JTextField();
		txtDest.setText(aAppDataConfig.getKeyValue("dest"));
		txtDest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(txtDest.getText()));
				chooser.setMultiSelectionEnabled(false);
				chooser.setDialogTitle("請選擇目錄");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// disable the "All files" option.
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					System.out.println("getCurrentDirectory(): " + chooser.getSelectedFile());
					txtDest.setText(chooser.getSelectedFile().toString());
				}
			}
		});
		txtDest.setFont(new Font("Dialog", Font.PLAIN, 12));
		txtDest.setColumns(10);
		txtDest.setBounds(132, 106, 388, 31);
		frame.getContentPane().add(txtDest);

		passwordUser = new JPasswordField();
		passwordUser.setFont(new Font("Dialog", Font.PLAIN, 16));
		passwordUser.setBounds(204, 164, 146, 28);
		frame.getContentPane().add(passwordUser);

		passwordOwner = new JPasswordField();
		passwordOwner.setFont(new Font("Dialog", Font.PLAIN, 16));
		passwordOwner.setBounds(204, 196, 146, 28);
		frame.getContentPane().add(passwordOwner);

		JButton btnNewButton = new JButton("確定");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean bSuccess = true;
				if (txtSource.getText().trim().equals("") || txtDest.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "請輸入來源路徑或是目標路徑");
					bSuccess = false;
				}
				System.out.println();
				if (passwordOwner.getPassword().length == 0) {
					JOptionPane.showMessageDialog(null, "請輸入文件保全密碼");
					bSuccess = false;
				}
				if (bSuccess) {
					
					 final JDialog loading = new JDialog(frame);
					    JPanel p1 = new JPanel(new BorderLayout());
					    p1.add(new JLabel("Please wait..."), BorderLayout.CENTER);
					    loading.setUndecorated(true);
					    loading.getContentPane().add(p1);
					    loading.pack();
					    loading.setLocationRelativeTo(frame);
					    loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					    loading.setModal(true);

					    SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
					        @Override
					        protected String doInBackground() throws InterruptedException {
					            /** Execute some operation */
					        	boolean  bSuccess = false;
								aAppDataConfig.setKeyValue("source", txtSource.getText());
								aAppDataConfig.setKeyValue("dest", txtDest.getText());
								
								if (passwordUser.getPassword().length == 0)
									bSuccess = EncryptPDF.getInstance().encryptFile(txtSource.getText(), txtDest.getText(), null,
											toBytes(passwordOwner.getPassword()));
								else
									bSuccess = EncryptPDF.getInstance().encryptFile(txtSource.getText(), txtDest.getText(),
											toBytes(passwordUser.getPassword()), toBytes(passwordOwner.getPassword()));
								if (bSuccess)
									JOptionPane.showMessageDialog(null, "PDF轉換成功");
								else
									JOptionPane.showMessageDialog(null, "PDF轉換失敗");					        	
					        	return null;
					        }
					        @Override
					        protected void done() {
					            loading.dispose();
					        }
					    };
					    worker.execute();
					    loading.setVisible(true);
					    try {
					        worker.get();
					    } catch (Exception e1) {
					        e1.printStackTrace();
					    }					
					

				}

			}
		});
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 16));
		btnNewButton.setForeground(Color.BLUE);
		btnNewButton.setBounds(216, 263, 104, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblsnapshot = new JLabel("3.1-SNAPSHOT 2017/12/05");
		lblsnapshot.setBounds(379, 298, 167, 13);
		frame.getContentPane().add(lblsnapshot);
	}

	private byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}
}
