package code;

import java.io.IOException;

/**
 * Case que inicia el Server Socket para evitar que el programa se pueda 
 * ejecutar varias veces al mismo tiempo
 * @author SrExtibax
 */

public class MyServerSocket extends Thread{
    
    UI ui;
    
    @Override
    public void run(){
        
        try{
            
            new java.net.ServerSocket(9000).accept();
            
            }catch (IOException e) {
                
                System.exit(0);
              
            }
        
    }
    
    /**
     * Metodo para recibir al UI
     * @param ui 
     */
    
    public void MyServerSocket(UI ui){
        
        this.ui = ui;
        
    }
    
}
