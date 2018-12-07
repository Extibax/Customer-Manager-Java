package code;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 * Clase que se encarga de otorgar las acciones abstractas para cada boton de
 * obligacion
 * @author SrExtibax
 */

public class ObligationAction extends AbstractAction{
    
    private final Obligation personObligacion;
    private final UI ui;
    
    /**
     * 
     * @param ui
     * @param personObligacion 
     */
    
    public ObligationAction(UI ui, Obligation personObligacion){
        
        super(personObligacion.getObligacion());
        this.personObligacion = personObligacion;
        this.ui = ui;
        
    }
    
    /**
     * 
     * @param e 
     */
    
    @Override
    public void actionPerformed(ActionEvent e){
        
        try {
            
            try {
                
                ui.idObligaciones = personObligacion.getId();
                ui.dialogObligacion.setVisible(true);
                ui.loadPopupPnl(MyConnection.get(), ui.idObligaciones);
                
            }catch (ClassNotFoundException ex) {
                
                Logger.getLogger(ObligationAction.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        } catch (SQLException ex) {
            
            Logger.getLogger(ObligationAction.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
    
}
