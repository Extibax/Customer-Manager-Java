package code;

import java.awt.AWTException;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.ImageIcon;

/* 
 * Clase qie se encarga de crear el Icono oculto, Hereda de Thread para que
 * Las notificaciones de escritorio no se pierda una que otra si se ejecutan al mismo tiempo
 * @author SrExtibax
 */

public class HiddenIcon extends Thread{
    
    TrayIcon trayIcon;
    
    
        public void run(UI ui){
        
            if(!SystemTray.isSupported()){
                System.out.println("We can't show the Tray Icon");
                
            }
        
            final PopupMenu popup = new PopupMenu();


            trayIcon = new TrayIcon(CreateIcon("/img/logo.jpg", "Administrador de clientes"));
            trayIcon.setImageAutoSize(true);
            final SystemTray tray = SystemTray.getSystemTray();
            //trayIcon.setToolTip("Version 1.6.21\nProject Jarvis");

            //Add components/ Menu items
            MenuItem AbrirItem = new MenuItem("Abrir");
            MenuItem AboutItem = new MenuItem("Acerca de..");
            MenuItem ExitItem = new MenuItem("Cerrar");

            //Populate the pop up menu
            popup.add(AbrirItem);
            popup.add(AboutItem);
            popup.addSeparator();
            popup.add(ExitItem);

            trayIcon.setPopupMenu(popup);
            trayIcon.setToolTip("AEM Consultores - Clientes");

            trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {

                        ui.setVisible(true);
                        ui.setExtendedState(MAXIMIZED_BOTH);
                    }
                }

            });

            AbrirItem.addActionListener((ActionEvent e) -> {
                ui.setVisible(true);
            });

            AboutItem.addActionListener((ActionEvent e) -> {
                ui.setExtendedState(MAXIMIZED_BOTH);
                ui.dialogAboutMe.setLocationRelativeTo(ui.pnlContenedorBandeja);
                ui.dialogAboutMe.setVisible(true);
            });

            ExitItem.addActionListener((ActionEvent e) -> {
                System.exit(0);
            });

            try 
            {
                tray.add(trayIcon);

            }catch (AWTException e) 
            {

            }
    }
    
    /**
     * Metodo para el icono del TrayIcon
     * @param path
     * @param desc
     * @return 
     */
        
    protected static Image CreateIcon(String path, String desc){
        
        URL ImageURL = UI.class.getResource(path);
        return (new ImageIcon(ImageURL, desc)).getImage();
        
    }
    
}
