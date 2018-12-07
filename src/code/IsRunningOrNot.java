/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 *
 * @author SrExtibax
 */
public class IsRunningOrNot {

    private static final int PORT = 9999;
    private static ServerSocket socket;

    public static void checkIfRunning() {
        
        try {
            
            //Bind to localhost adapter with a zero connection queue 
            socket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            
        } catch (BindException e) {
            
            System.err.println("Already running.");
            System.exit(1);
            
        } catch (IOException e) {
            
            System.err.println("Unexpected error.");
            System.exit(2);
            
        }
    }

}
