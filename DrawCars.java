import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

public class DrawCars extends JPanel implements ActionListener, KeyListener {

   JButton quitb = new JButton("Quit");

   PrintWriter writer;
   BufferedReader serverReader;

   private final int TOTAL_IMAGES = 16; // number of images
   private final int ANIMATION_DELAY = 50; // millisecond delay
   private int width; // image width
   private int height; // image height

   // Add the first Car
   private final static String IMAGE_NAME = "\\r"; // base image name
   protected ImageIcon images[]; // array of images
   private int currentRedImage = 12; // current image index

   // Add Second car
   private final static String RED_CAR = "\\b";
   protected ImageIcon blueImages[];
   private int currentBlueImage = 12;

   private Timer animationTimer; // Timer drives animation
   int redCarX = 500, redCarY = 500;
   int blueCarX = 500, blueCarY = 550;
   int dsplmnt = 3;
   boolean[] keyArray = new boolean[4];

   public String car;
   int x = 0;
   int y = 0;

   // constructor initializes cars by loading images
   public DrawCars() {
      try (Socket socket = new Socket("localhost", 12345);) {

         writer = new PrintWriter(socket.getOutputStream(), true);
         serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String clientName = "red";
         writer.println(clientName);

         writer.println("get");
         String s = serverReader.readLine();
         System.out.println(s.split(",")[0]);
         if (Integer.parseInt(s.split(",")[0]) == 0) {
            this.car = "red";
            writer.println("500,500,12");
         } else {
            this.car = "blue";
            writer.println("500,550,12");
         }
         socket.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      System.out.println(this.car);

      this.add(quitb); // Add the quit button to the panel.
      quitb.setBounds(20, 0, 80, 60); // Set the location and size of the quit button.
      quitb.addActionListener(this);
      // this.add(label);

      // animationTimer.start();
      addKeyListener(this);
      setFocusable(true);
      setFocusTraversalKeysEnabled(false);

      images = new ImageIcon[TOTAL_IMAGES];
      blueImages = new ImageIcon[TOTAL_IMAGES];

      // load 16 images
      for (int count = 0; count < images.length; count++) {
         images[count] = new ImageIcon(getClass().getResource("redCar" + IMAGE_NAME + count + ".png"));
         // all images have the same width and height
         width = images[0].getIconWidth(); // get icon width
         height = images[0].getIconHeight(); // get icon height
      }

      for (int count = 0; count < blueImages.length; count++) {
         blueImages[count] = new ImageIcon(getClass().getResource("blueCar" + RED_CAR + count + ".png"));
         width = images[0].getIconWidth(); // get icon width
         height = images[0].getIconHeight(); // get icon height

      }

      animationTimer = new Timer(ANIMATION_DELAY, this);
      animationTimer.start(); // start Timer

   }// end LogoAnimatorJPanel constructor

   public void paintComponent(Graphics g) {
      super.paintComponent(g); // call superclass paintComponent

      if (redCarY > 200 && redCarY < 250) {
         x = 1;
      }
      if (blueCarY > 200 && blueCarY < 250) {
         y = 1;
      }
      if (redCarX <= 416 && redCarY > 473 && x == 1) {
         String message = "Game Over !! Red Car Wins";
         g.setFont(new Font("Algerian", Font.PLAIN, 30));
         g.setColor(Color.RED);
         g.drawString(message, 50, 100);

      } else if (blueCarX <= 416 && blueCarY > 473 && y == 1) {
         String message = "Game Over !! Blue Car Wins";
         g.setFont(new Font("Algerian", Font.PLAIN, 30));
         g.setColor(Color.BLUE);
         g.drawString(message, 50, 100);
      } else {
         Color c1 = Color.green;
         g.setColor(c1);
         g.fillRect(150, 200, 550, 300); // grass

         Color c2 = Color.black;
         g.setColor(c2);
         g.drawRect(50, 100, 750, 500); // outer edge
         g.drawRect(150, 200, 550, 300); // inner edge

         Color c3 = Color.yellow;
         g.setColor(c3);
         g.drawRect(100, 150, 650, 400); // mid-lane marker

         Color c4 = Color.white;
         g.setColor(c4);
         g.drawLine(425, 500, 425, 600); // start lin
         String s = redCarX + "," + redCarY + "," + currentRedImage + "," + blueCarX + "," + blueCarY + ","
               + currentBlueImage;
         try (Socket socket = new Socket("localhost", 12345);) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientName = car;
            writer.println(clientName);
            writer.println("get");
            s = serverReader.readLine();
            socket.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
         redCarX = Integer.parseInt(s.split(",")[0]);
         redCarY = Integer.parseInt(s.split(",")[1]);
         currentRedImage = Integer.parseInt(s.split(",")[2]);
         blueCarX = Integer.parseInt(s.split(",")[3]);
         blueCarY = Integer.parseInt(s.split(",")[4]);
         currentBlueImage = Integer.parseInt(s.split(",")[5]);

         images[currentRedImage].paintIcon(this, g, redCarX, redCarY);
         blueImages[currentBlueImage].paintIcon(this, g, blueCarX, blueCarY);
      }
   } // end method paintComponent

   // this method to prevent the cars to go out of the track
   public boolean outBound(int c_x, int c_y) {

      int x1 = 115;
      int y1 = 180;
      int x2 = 670;
      int y2 = 485;

      int f = 0;
      int h = 0;

      if ((x1 <= c_x) && (c_x <= x2))
         if ((y1 <= c_y) && (c_y <= y2))
            f = 1;

      x1 = 30;
      y1 = 90;
      x2 = 770;
      y2 = 555;
      if ((x1 <= c_x) && (c_x <= x2))
         if ((y1 <= c_y) && (c_y <= y2))
            h = 1;

      if (f == 1)
         return true;
      else if (f == 0 && h == 0)
         return true;
      else
         return false;

   }

   public void actionPerformed(ActionEvent e) {

      if (e.getSource() == quitb) // If source is the quit button, then continue.
         System.exit(0); // Exit the program.

      if (redCarX == blueCarX && redCarY == blueCarY) {
         redCarX = 500;
         redCarY = 500;
         blueCarX = 500;
         blueCarY = 550;
      }

      repaint(); // repaint animator
   }

   public int[] get_xy(int x, int y, int d) {
      System.out.println(x + "," + y + "," + d);
      int f[] = new int[2];
      f[0] = x;
      f[1] = y;
      if (d == 8) {
         y = y + 2 * dsplmnt;
      } else if (d == 7) {
         x = x + 1 * dsplmnt;
         y = y + 2 * dsplmnt;
      } else if (d == 6) {
         x = x + 2 * dsplmnt;
         y = y + 2 * dsplmnt;
      } else if (d == 5) {
         x = x + 2 * dsplmnt;
         y = y + 1 * dsplmnt;
      } else if (d == 4) {
         x = x + 2 * dsplmnt;
         // y=y+1*dsplmnt;
      } else if (d == 3) {
         x = x + 2 * dsplmnt;
         y = y - 1 * dsplmnt;
      } else if (d == 2) {
         x = x + 2 * dsplmnt;
         y = y - 2 * dsplmnt;
      } else if (d == 1) {
         x = x + 1 * dsplmnt;
         y = y - 2 * dsplmnt;
      } else if (d == 0) {
         // x=x+2*dsplmnt;
         y = y - 2 * dsplmnt;
      } else if (d == 15) {
         x = x - 1 * dsplmnt;
         y = y - 2 * dsplmnt;
      } else if (d == 14) {
         x = x - 2 * dsplmnt;
         y = y - 2 * dsplmnt;
      } else if (d == 11) {
         x = x - 2 * dsplmnt;
         y = y + 1 * dsplmnt;
      } else if (d == 12) {
         x = x - 2 * dsplmnt;
         // y=y-2*dsplmnt;
      } else if (d == 13) {
         x = x - 2 * dsplmnt;
         y = y - 1 * dsplmnt;
      } else if (d == 10) {
         x = x - 2 * dsplmnt;
         y = y + 2 * dsplmnt;
      } else {
         x = x - 1 * dsplmnt;
         y = y + 2 * dsplmnt;
         System.out.println("9" + x + "," + y);
      }

      if (outBound(x, y) == false) {
         f[0] = x;
         f[1] = y;
      }
      return f;
   }

   public void keyPressed(KeyEvent e) {
      int c = e.getKeyCode();

      if (c == KeyEvent.VK_LEFT) {
         keyArray[0] = true;
      }
      if (c == KeyEvent.VK_UP) {
         keyArray[1] = true;
      }

      if (c == KeyEvent.VK_DOWN) {
         keyArray[2] = true;
      }

      if (c == KeyEvent.VK_RIGHT) {
         keyArray[3] = true;
      }

      try (Socket socket = new Socket("localhost", 12345);) {

         writer = new PrintWriter(socket.getOutputStream(), true);
         serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String clientName = car;
         writer.println(clientName);
         if (keyArray[0] && car == "red") {
            if (currentRedImage == 0) {
               currentRedImage = 15;
            } else {
               currentRedImage = currentRedImage - 1;
            }
            writer.println(redCarX + "," + redCarY + "," + currentRedImage);
         }
         if (keyArray[1] && car == "red") {
            int temp[] = new int[2];
            temp = get_xy(redCarX, redCarY, currentRedImage);
            redCarX = temp[0];
            redCarY = temp[1];
            writer.println(redCarX + "," + redCarY + "," + currentRedImage);
         }
         if (keyArray[3] && car == "red") {
            if (currentRedImage == 15) {
               currentRedImage = 0;
            } else {
               currentRedImage = currentRedImage + 1;
            }
            writer.println(redCarX + "," + redCarY + "," + currentRedImage);
         }
         if (keyArray[2] && car == "red") {
            int temp[] = new int[2];
            System.out.println(currentRedImage);
            if (currentRedImage >= 8)
               temp = get_xy(redCarX, redCarY, Math.abs(currentRedImage - 8));
            else if (currentRedImage == 1)
               temp = get_xy(redCarX, redCarY, 9);
            else
               temp = get_xy(redCarX, redCarY, Math.abs(currentRedImage + 8));
            redCarX = temp[0];
            redCarY = temp[1];
            writer.println(redCarX + "," + redCarY + "," + currentRedImage);
         }
         // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         if (keyArray[0] && car == "blue") {
            if (currentBlueImage == 0) {
               currentBlueImage = 15;
            } else {
               currentBlueImage = currentBlueImage - 1;
            }
            writer.println(blueCarX + "," + blueCarY + "," + currentBlueImage);
         }
         if (keyArray[1] && car == "blue") {
            int temp[] = new int[2];
            temp = get_xy(blueCarX, blueCarY, currentBlueImage);
            blueCarX = temp[0];
            blueCarY = temp[1];
            writer.println(blueCarX + "," + blueCarY + "," + currentBlueImage);
         }
         if (keyArray[3] && car == "blue") {
            if (currentBlueImage == 15) {
               currentBlueImage = 0;
            } else {
               currentBlueImage = currentBlueImage + 1;
            }
            writer.println(blueCarX + "," + blueCarY + "," + currentBlueImage);
         }
         if (keyArray[2] && car == "blue") {
            int temp[] = new int[2];
            System.out.println(currentBlueImage);
            if (currentBlueImage >= 8)
               temp = get_xy(blueCarX, blueCarY, Math.abs(currentBlueImage - 8));
            else if (currentBlueImage == 1)
               temp = get_xy(blueCarX, blueCarY, 9);
            else
               temp = get_xy(blueCarX, blueCarY, Math.abs(currentBlueImage + 8));
            blueCarX = temp[0];
            blueCarY = temp[1];
            writer.println(blueCarX + "," + blueCarY + "," + currentBlueImage);
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }

   }

   public void keyTyped(KeyEvent e) {

   }

   public void keyReleased(KeyEvent e) {
      int c = e.getKeyCode();

      if (c == KeyEvent.VK_LEFT) {
         keyArray[0] = false;
      }
      if (c == KeyEvent.VK_UP) {
         keyArray[1] = false;
      }

      if (c == KeyEvent.VK_DOWN) {
         keyArray[2] = false;
      }

      if (c == KeyEvent.VK_RIGHT) {
         keyArray[3] = false;
      }

   }
}
