// Benjamin Tang
// ECE 309 Lab #7
// 10/5/2016

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class NekoTheCat implements MouseListener, ActionListener, Runnable {
	Image catRight1 = new ImageIcon(getClass().getResource("Neko1.gif")).getImage();
	Image catRight2 = new ImageIcon(getClass().getResource("Neko2.gif")).getImage();
	Image catLeft1 = new ImageIcon(getClass().getResource("Neko3.gif")).getImage();
	Image catLeft2 = new ImageIcon(getClass().getResource("Neko4.gif")).getImage();
	Image redBall = new ImageIcon(getClass().getResource("red-ball.gif")).getImage();
	Image cat1 = catRight1;
	Image cat2 = catRight2;
	Image currentImage = catRight1;
	JFrame gameWindow = new JFrame("Neko The Cat!");
	JFrame replayWindow = new JFrame("Play again?");
	JPanel gamePanel = new JPanel();
	JPanel replayPanel = new JPanel();
	JButton replayButton = new JButton("Replay");
	JButton exitButton = new JButton("Exit Game");
	int catxPosition = 1;
	int catyPosition = 50;
	int catWidth = catRight1.getWidth(gamePanel);
	int catHeight = catRight1.getHeight(gamePanel);
	int ballxPosition = 0;
	int ballyPosition = 0;
	int ballSize = redBall.getWidth(gamePanel);
	int sleepTime = 100; // pause time between image repaints (in ms)
	int xBump = 10; // amount cat image is moved each repaint.
	boolean catIsRunningToTheRight = true; // initially
	boolean catIsRunningToTheLeft = false;// initially
	boolean ballHasBeenPlaced = false;// initially
	Graphics g;
	AudioClip soundFile = Applet.newAudioClip(getClass().getResource("spacemusic.au"));

	public NekoTheCat() {
		// TODO Auto-generated constructor stub
		gameWindow.getContentPane().add(gamePanel, "Center");
		gamePanel.setBackground(Color.white);
		gameWindow.setSize(1600, 1000);
		gameWindow.setVisible(true);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		replayPanel.setLayout(new GridLayout(1,2));
	    replayPanel.add(replayButton);
		replayPanel.add(exitButton);
		replayWindow.setSize(800, 200);
		replayWindow.getContentPane().add(replayPanel, "Center");
		replayWindow.setLocation(400,500);
		replayWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		replayButton.setFont (new Font("default",Font.BOLD,20));
		replayButton.addActionListener(this);
		exitButton.setFont (new Font("default",Font.BOLD,20));
		exitButton.addActionListener(this);  
		
		g = gamePanel.getGraphics();

		// show game instructions on the screen
		g.setFont(new Font("Times Roman", Font.BOLD, 20));
		g.drawString("Neko the cat is looking for it's red ball!", 500, 500);
		g.drawString("Click the mouse to place Neko's ball.", 500, 520);
		g.drawString("Can you move the ball to keep Neko from getting it?", 500, 540);
		g.drawString("(Pull window larger to make the game easier)", 500, 560);
		g.drawString("THIS FANTASTIC GAME MADE BY BENJAMIN TANG", 500, 580);
		System.out.println("THIS FANTASTIC GAME MADE BY BENJAMIN TANG");

		gamePanel.addMouseListener(this); // call me!
		soundFile.loop();
		System.out.println("Thread has entered run()");
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			while ((catxPosition > 0) && (catxPosition < gamePanel.getSize().width)) {
				// 1. Blank out the last image
				g.setColor(Color.white);
				g.fillRect(catxPosition, catyPosition-10, catWidth, catHeight+15);
				// 2. Bump the location for the new image
				catxPosition += xBump;
				// 3. Select the next image.
				if (currentImage == cat1)
					currentImage = cat2;
				else
					currentImage = cat1;
				// 4. Draw the next cat image
				g.drawImage(currentImage, catxPosition, catyPosition, gamePanel);
				// 5. Pause briefly to let human eye see the new image!
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException ie) {
				}
				// 6. Let Neko chase the ball!
				if (ballHasBeenPlaced) { // Checks to see if ball has been
											// placed by user
					// If cat is BELOW the ball (cat y pos. > ball y), move up one (y-axis)
					if (catyPosition > ballyPosition)
						catyPosition -= 10;
					// If cat is ABOVE the ball (cat y pos. < ball y), move down one (y-axis)
					if (catyPosition < ballyPosition)
						catyPosition += 10;
					// If the cat is moving to the left + ball is to the right (x-axis)
					if ((ballxPosition > (catxPosition + currentImage.getWidth(gamePanel)) && catIsRunningToTheLeft))
						reverseLR();
					// If the cat is moving to the right + ball is to the left (x-axis)
					if ((ballxPosition < catxPosition) && (catIsRunningToTheRight))
						reverseRL();
				}
				// 7. End the game when Neko gets the ball!
				if ((Math.abs(catyPosition - ballyPosition) < 10) && (Math.abs(catxPosition - ballxPosition) < 10)) { // x and y within 10 pixels
					gamePanel.removeMouseListener(this); // disable user controls
					gamePanel.setBackground(Color.white);
					soundFile.stop(); // stop the music
					g.setColor(Color.RED);
					g.setFont(new Font("Arial", Font.PLAIN, 50));
					g.drawString("At last, I have my ball!", 50, 50);
					replayWindow.setVisible(true);
					return;
				}
			} // bottom of inner while loop
				// turn Neko around
			if (catxPosition > gamePanel.getSize().width) {
				reverseRL();
				catxPosition = gamePanel.getSize().width - 1;
			}
			if (catxPosition < 0) {
				reverseLR();
				catxPosition = 1;
			}
		} // bottom of outer while(true) loop
	} // end of run() method

	private void reverseRL() {
		xBump = -xBump; // reverse increment
		cat1 = catLeft1;
		cat2 = catLeft2;
		catIsRunningToTheLeft = true;
		catIsRunningToTheRight = false;
	}

	private void reverseLR() {
		xBump = -xBump;
		cat1 = catRight1;
		cat2 = catRight2;
		catIsRunningToTheRight = true;
		catIsRunningToTheLeft = false;
	}

	public void mouseClicked(MouseEvent me) {
		ballHasBeenPlaced = true;
		g.setColor(Color.white); // set to background color
		g.fillRect(ballxPosition, ballyPosition, ballSize, ballSize); // x,y,width,height
		ballxPosition = me.getX();
		ballyPosition = me.getY();
		System.out.println("Mouse clicked at x=" + ballxPosition + ",y=" + ballyPosition);
		g.drawImage(redBall, ballxPosition, ballyPosition, gamePanel);
	}

	public static void main(String[] args) {
		new NekoTheCat();
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == replayButton)
			new NekoTheCat();
		if(ae.getSource() == exitButton)
			System.exit(0);
	}
}
