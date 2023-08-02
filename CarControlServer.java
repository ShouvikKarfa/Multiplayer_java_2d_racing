import java.io.*;
import java.net.*;

public class CarControlServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is running. Waiting for clients to connect...");

            Car redCar = new Car("Red Car");
            Car blueCar = new Car("Blue Car");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                Thread clientThread = new ClientHandler(clientSocket, redCar, blueCar);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private String clientName;
    private Car redCar;
    private Car blueCar;

    public ClientHandler(Socket clientSocket, Car redCar, Car blueCar) {
        this.clientSocket = clientSocket;
        this.redCar = redCar;
        this.blueCar = blueCar;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            
            clientName = reader.readLine();
            if(redCar.getX()!=0 && blueCar.getX()==0)
            clientName="blue";
            System.out.println("Client " + clientName + " connected.");

            while (true) {
                String message = reader.readLine();
                if (message == null) break;

                String[] parts = message.split(",");
                
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int d = Integer.parseInt(parts[2]);

                    if (clientName.equalsIgnoreCase("red")) {
                        redCar.setCoordinates(x, y, d);
                    } else if (clientName.equalsIgnoreCase("blue")) {
                        blueCar.setCoordinates(x, y, d);
                    } else {
                        System.out.println("Unknown client name: " + clientName);
                    }
                } else if (parts.length == 1 && parts[0].equalsIgnoreCase("get")) {
                    String response = redCar.getX()+","+ redCar.getY() + ","+redCar.getD()+","+ blueCar.getX() + "," + blueCar.getY()+","+blueCar.getD();
                    writer.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Car {
    private int x;
    private int y;
    private int d;
    private String carName;

    public Car(String carName) {
        this.carName = carName;
    }

    public void setCoordinates(int x, int y,int d) {
        this.x = x;
        this.y = y;
        this.d = d;
        System.out.println("Car position updated for " + this.carName + ": (" + x + ", " + y +","+d+ ")");
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getD()
    {
        return d;
    }
}
