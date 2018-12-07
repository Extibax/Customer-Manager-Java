package code;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

/**
 * Clase de mi JButton personalizado
 * @author SrExtibax
 */

public class CustomButton extends JButton implements MouseListener{
    
    /**
     * Contructor del JButton
     * @param a 
     */
    
    public CustomButton(Action a){
        
        setContentAreaFilled(false);
        setOpaque(true);
        setBackground(new Color(255, 255, 255));
        setBorder(new LineBorder(new Color(47, 43, 45), 2, true));
        setFocusable(false);
        setPreferredSize(new Dimension(0, 30));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("SansSerif", 1, 14));
        setForeground(new Color(0, 0, 0));
        setAction(a);
        addMouseListener(this);
        
    }

    /**
     * cambia el color del JButton al tener el mouse encima
     * @param evt 
     */
    
    @Override
    public void mouseEntered(MouseEvent evt) {
        
        setBackground(new Color(195, 204, 209));
    
    }
    
    /**
     * restablece el color del mouse al salir el mouse de encima
     * @param evt 
     */
    
    @Override
    public void mouseExited(MouseEvent evt) {
        
        setBackground(new Color(255, 255, 255));
    
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    
}
