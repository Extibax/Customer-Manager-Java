package code;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 * Clase que se encarga de otorgar las acciones abstractas para cada boton
 * de obligacion
 * @author SrExtibax
 */

public class PdfAction extends AbstractAction{
    
    private Pdf personPdf;
    private UI frame;
    
    
    public PdfAction(UI frame, Pdf personPdf){
        
        super(personPdf.getNombrePdf());
        this.personPdf = personPdf;
        this.frame = frame;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
            try {
                
                frame.openPdf(MyConnection.get(), personPdf.getNombrePdf());
                
                try {
                    
                    Desktop.getDesktop().open(new File("New.pdf"));
                    
                } catch (IOException ex) {
                    
                }
                
            } catch (SQLException | ClassNotFoundException ex) {
                
                Logger.getLogger(PdfAction.class.getName()).log(Level.SEVERE, null, ex);
            
            }
        
    }
    
}