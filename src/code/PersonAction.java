package code;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 * Clase que se encarga de otorgar las acciones abstractas para cada cliente
 * @author SrExtibax
 */

public class PersonAction extends AbstractAction{
    
    private Person person;
    private UI ui;
    
    
    public PersonAction(UI ui, Person person){
        super(person.getNombre());
        this.person = person;
        this.ui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
        try {
            ui.loadDataPanel(MyConnection.get(), person.getIdPerson());
            //Remuevo los componentes y los recargo para poner en default el color
            //que es cambiado por el mouse Entered
            ui.pnlBandeja.removeAll();
            ui.loadPnlCustomers(MyConnection.get(), "");
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(PersonAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
