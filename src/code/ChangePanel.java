package code;

import javax.swing.JPanel;

/**
 * Esta clase se encarga de hacer los cambios de paneles entre si
 * @author SrExtibax
 */

public class ChangePanel {
    
    private JPanel container;
    private JPanel content;

/**
 * Pide el panel contenedor y el panel a cambiar
 * @param container
 * @param content 
 */
    
    public ChangePanel(JPanel container, JPanel content) {
        this.container = container;
        this.content = content;
        this.container.removeAll();
        this.container.revalidate();
        this.container.repaint();
        
        this.container.add(this.content);
        this.container.revalidate();
        this.container.repaint();
    }

}
