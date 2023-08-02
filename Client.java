import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;





class Client extends JFrame
{
   public Client(){

     this.setTitle("Go-Karte");
     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
     this.setSize(new Dimension(900, 700));
    //  this.setVisible(true);
     this.setResizable(true);
     
     Container cp = this.getContentPane();
     DrawCars mypanel = new DrawCars();
     
     cp.add(mypanel);
     
   }   
   
   public static void main(String[] args){
      Client w = new Client();
      w.setVisible(true);
      
  }

}