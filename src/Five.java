
//创建窗口

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JToolBar;

public class Five extends JFrame {
	private JToolBar toolbar;
	private JButton startButton, backButton, exitButton, aboutButton;
	private ChessBoard boardPanel;
	JCheckBox computerFirst;// 复选框

	public Five() {
		super("五子棋人机对战");
		toolbar = new JToolBar();
		computerFirst = new JCheckBox("计算机先手");
		startButton = new JButton("开始");
		backButton = new JButton("悔棋");
		exitButton = new JButton("退出");
		aboutButton = new JButton("关于");
		toolbar.add(startButton);
		toolbar.add(backButton);
		toolbar.add(exitButton);
		toolbar.add(aboutButton);
		toolbar.add(computerFirst);
		this.add(toolbar, BorderLayout.NORTH);

		boardPanel = new ChessBoard(this);
		this.add(boardPanel, BorderLayout.CENTER);
		this.setLocation(200, 200);
		this.pack();
		this.setResizable(false);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // 窗体设置
		this.setVisible(true);

		ActionMonitor monitor = new ActionMonitor(); // 监听按钮
		startButton.addActionListener(monitor);
		backButton.addActionListener(monitor);
		exitButton.addActionListener(monitor);
		aboutButton.addActionListener(monitor);
	}

	class ActionMonitor implements ActionListener { // 内部监听类
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == startButton) {
				boardPanel.restartGame();
			} else if (e.getSource() == backButton) {
				boardPanel.goback();
			} else if (e.getSource() == exitButton) {
				System.exit(0);
			} else if (e.getSource() == aboutButton) {
				boardPanel.about();
			}
		}
	}

	public static void main(String[] args) { // 开始
		new Five();
	}
}