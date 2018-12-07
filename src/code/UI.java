package code;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @version 1.2
 * @author SrExtibax Contact extibax@gmail.com
 */
public final class UI extends javax.swing.JFrame {

    public ImageIcon icono;
    String pathPdf = "";
    int ss, mm, hh;
    public int idObligaciones = 0;
    HiddenIcon hiddenIcon = new HiddenIcon();
    Properties p = new Properties();
    Date checkDate;

    /**
     *
     * @throws AWTException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException Contructor de la clase principal Uso de
     * ServerSocket para evitar la ejecucion del programa dos veces al mismo
     * tiempo
     */
    public UI() throws AWTException, SQLException, ClassNotFoundException, FileNotFoundException {

        MyServerSocket serverSocket = new MyServerSocket();
        serverSocket.MyServerSocket(this);
        serverSocket.start();
        setState(ICONIFIED);
        initComponents();
        MyConnection.DatabaseCustomers(MyConnection.get());
        MyConnection.DatabasePdf(MyConnection.get());
        MyConnection.DatabaseObligation(MyConnection.get());
        loadPnlCustomers(MyConnection.get(), "");
        setLocationRelativeTo(null);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/logo.jpg")));
        btnGuardarCambiosShowC.setVisible(false);
        btnCancelarShowC.setVisible(false);
        btnGuardarPdfShowC.setVisible(false);
        btnCancelarPdfShowC.setVisible(false);
        reminders(MyConnection.get());
        hiddenIcon.run(this);
        hiddenIcon.start();

    }

    /**
     * Metodo que carga los botones principales del pnlBandeja
     *
     * @param myConnection
     * @param nameSearch
     */
    public void loadPnlCustomers(Connection myConnection, String nameSearch) {

        try {

            Statement miStatement = myConnection.createStatement();
            ResultSet miRs = miStatement.executeQuery("SELECT * FROM customers WHERE name LIKE '%" + nameSearch + "%'");
            int iterator = 1;

            while (miRs.next()) {

                int id = miRs.getInt("id");
                String name = miRs.getString("name");
                String ruc = miRs.getString("ruc");
                String repLegal = miRs.getString("repLegal");
                String nombreContactoEmpresa = miRs.getString("nombreContactoEmpresa");
                String correoContactoEmpresa = miRs.getString("correoContactoEmpresa");
                String numeroContacto = miRs.getString("numeroContacto");
                String cedulaRepLegal = miRs.getString("cedulaRepLegal");
                String nit = miRs.getString("nit");
                String noContribuyenteMuniPa = miRs.getString("noContribuyenteMuniPa");
                String contraMuniPa = miRs.getString("contraMuniPa");

                Person person = new Person(id, name, ruc, repLegal, nombreContactoEmpresa, correoContactoEmpresa,
                        numeroContacto, cedulaRepLegal, nit, noContribuyenteMuniPa, contraMuniPa);

                PersonAction personAction = new PersonAction(UI.this, person);
                CustomButton customButton = new CustomButton(personAction);
                customButton.setText(name);

                iterator++;

                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridwidth = 1;
                c.weightx = 1.0;
                c.insets = new Insets(3, 10, 3, 10);
                c.gridy = iterator;
                c.gridx = 0;
                pnlBandeja.add(customButton, c);
                pnlBandeja.revalidate();
                pnlBandeja.repaint();

            }

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    /**
     * Metodo que carga la informacion de cada cliente en el pnlShowCustomer
     *
     * @param myConnection
     * @param idPerson
     */
    public void loadDataPanel(Connection myConnection, int idPerson) {

        new ChangePanel(pnlBoxFrame, pnlShowCustomer);

        try {

            Statement miStatement = myConnection.createStatement();
            ResultSet miRs = miStatement.executeQuery("SELECT * FROM customers WHERE id = '" + idPerson + "'");

            while (miRs.next()) {

                int id = miRs.getInt("id");
                String name = miRs.getString("name");
                String ruc = miRs.getString("ruc");
                String repLegal = miRs.getString("repLegal");
                String nombreContactoEmpresa = miRs.getString("nombreContactoEmpresa");
                String correoContactoEmpresa = miRs.getString("correoContactoEmpresa");
                String numeroContacto = miRs.getString("numeroContacto");
                String cedulaRepLegal = miRs.getString("cedulaRepLegal");
                String nit = miRs.getString("nit");
                String noContribuyenteMuniPa = miRs.getString("noContribuyenteMuniPa");
                String contraMuniPa = miRs.getString("contraMuniPa");

                lblIDCustomers.setText(String.valueOf(id));
                lblNameShowC.setText(name);
                txtRucShowC.setText(ruc);
                txtRepLegalShowC.setText(repLegal);
                txtNombreContactoShowC.setText(nombreContactoEmpresa);
                txtCorreoContactoShowC.setText(correoContactoEmpresa);
                txtNumeroContactoShowC.setText(numeroContacto);
                txtCedulaRepLegalShowC.setText(cedulaRepLegal);
                txtNitShowC.setText(nit);
                txtNoContribuyenteShowC.setText(noContribuyenteMuniPa);
                txtContraseñaMuniPaShowC.setText(contraMuniPa);

            }

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    /**
     * Metodo que carga la informacion del cliente en un pnl editable
     *
     * @param myConnection
     * @param idCostumers
     */
    public void loadPnlEdit(Connection myConnection, int idCostumers) {

        new ChangePanel(pnlBoxFrame, pnlShowCustomer);

        try {

            Statement miStatement = myConnection.createStatement();
            ResultSet miRs = miStatement.executeQuery("SELECT * FROM customers WHERE id = '" + idCostumers + "'");

            while (miRs.next()) {

                int id = miRs.getInt("id");
                String name = miRs.getString("name");
                String ruc = miRs.getString("ruc");
                String repLegal = miRs.getString("repLegal");
                String nombreContactoEmpresa = miRs.getString("nombreContactoEmpresa");
                String correoContactoEmpresa = miRs.getString("correoContactoEmpresa");
                String numeroContacto = miRs.getString("numeroContacto");
                String cedulaRepLegal = miRs.getString("cedulaRepLegal");
                String nit = miRs.getString("nit");
                String noContribuyenteMuniPa = miRs.getString("noContribuyenteMuniPa");
                String contraMuniPa = miRs.getString("contraMuniPa");

                txtNombreEditC.setText(name);
                txtRucEditC.setText(ruc);
                txtRepLegalEditC.setText(repLegal);
                txtNombreContactoEditC.setText(nombreContactoEmpresa);
                txtCorreoEditC.setText(correoContactoEmpresa);
                txtNumeroContactoEditC.setText(numeroContacto);
                txtCedulaRepEditC.setText(cedulaRepLegal);
                txtNitEditC.setText(nit);
                txtNoContribuyenteEditC.setText(noContribuyenteMuniPa);
                txtContraseñaEditC.setText(contraMuniPa);

            }

        } catch (SQLException e) {

            System.out.println(e);

        }

    }

    /**
     * Metodo que inseta los datos del nuevo cliente a la Base de datos SQLite
     *
     * @param myConnection
     * @param nameI
     * @param rucI
     * @param repLegalI
     * @param nombreContactoEmpresaI
     * @param correoContactoEmpresaI
     * @param numeroContactoI
     * @param cedulaRepLegalI
     * @param nitI
     * @param noContribuyenteMuniPaI
     * @param contraMuniPaI
     */
    public void insertCustomers(Connection myConnection, String nameI, String rucI, String repLegalI, String nombreContactoEmpresaI,
            String correoContactoEmpresaI, String numeroContactoI, String cedulaRepLegalI, String nitI, String noContribuyenteMuniPaI,
            String contraMuniPaI) {

        try {

            PreparedStatement pstmt = myConnection.prepareStatement("INSERT INTO customers"
                    + " (name, ruc, repLegal, nombreContactoEmpresa, correoContactoEmpresa, "
                    + "numeroContacto, cedulaRepLegal, nit, noContribuyenteMuniPa,"
                    + "contraMuniPa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            pstmt.setString(1, nameI);
            pstmt.setString(2, rucI);
            pstmt.setString(3, repLegalI);
            pstmt.setString(4, nombreContactoEmpresaI);
            pstmt.setString(5, correoContactoEmpresaI);
            pstmt.setString(6, numeroContactoI);
            pstmt.setString(7, cedulaRepLegalI);
            pstmt.setString(8, nitI);
            pstmt.setString(9, noContribuyenteMuniPaI);
            pstmt.setString(10, contraMuniPaI);
            pstmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(e);

        }
    }

    /**
     * Metodo que remueve clientes
     *
     * @param myConnection
     * @param idCustomers
     */
    public void removeCustomers(Connection myConnection, int idCustomers) {

        try {

            PreparedStatement pst = myConnection.prepareStatement("DELETE FROM customers "
                    + "WHERE id = '" + idCustomers + "'");
            pst.executeUpdate();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, e);

        }
    }

    /**
     * Metodo que guarda los cambios hechos en el pnlEditCustomers
     *
     * @param myConnection
     * @param idCustomers
     */
    public void updateCustomers(Connection myConnection, int idCustomers) {

        String sql = "UPDATE customers SET "
                + "name ='" + txtNombreEditC.getText() + "' "
                + ",ruc ='" + txtRucEditC.getText() + "' "
                + ",repLegal ='" + txtRepLegalEditC.getText() + "' "
                + ",nombreContactoEmpresa ='" + txtNombreContactoEditC.getText() + "' "
                + ",correoContactoEmpresa ='" + txtCorreoEditC.getText() + "' "
                + ",numeroContacto ='" + txtNumeroContactoEditC.getText() + "' "
                + ",cedulaRepLegal ='" + txtCedulaRepEditC.getText() + "' "
                + ",nit ='" + txtNitEditC.getText() + "' "
                + ",noContribuyenteMuniPa ='" + txtNoContribuyenteEditC.getText() + "' "
                + ",contraMuniPa ='" + txtContraseñaEditC.getText() + "' "
                + "WHERE id = '" + idCustomers + "'";

        try {

            PreparedStatement pst = myConnection.prepareStatement(sql);
            pst.executeUpdate();

        } catch (SQLException ex) {

            Logger.getLogger(JTable.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR AL ACTUALIZAR " + ex);

        }

    }

    /**
     * Metodo que abre los PDF
     *
     * @param myConnection
     * @param nombrePDF
     */
    public void openPdf(Connection myConnection, String nombrePDF) {

        byte[] bite = null;

        try {

            PreparedStatement pstmt = myConnection.prepareStatement("SELECT archivopdf FROM PDF WHERE nombrepdf = '" + nombrePDF + "'");
            ResultSet miResultSet;
            miResultSet = pstmt.executeQuery();

            while (miResultSet.next()) {

                bite = miResultSet.getBytes("archivopdf");

            }
            try (InputStream bos = new ByteArrayInputStream(bite)) {

                int tamanoInput = bos.available();
                byte[] datosPDF = new byte[tamanoInput];
                bos.read(datosPDF, 0, tamanoInput);
                try (OutputStream out = new FileOutputStream("new.pdf")) {

                    out.write(datosPDF);

                }
            }
        } catch (IOException | NumberFormatException | SQLException ex) {

            System.out.println("Error al abrir archivo PDF " + ex.getMessage());

        }
    }

    /**
     * Metodo que selecciona el PDF desde un gestor de archivos
     */
    public void selectPdf() {

        JFileChooser j = new JFileChooser();
        FileNameExtensionFilter fi = new FileNameExtensionFilter("pdf", "pdf");
        j.setFileFilter(fi);

        int se = j.showOpenDialog(this);

        if (se == 0) {

            btnSeleccionarPdf.setText("" + j.getSelectedFile().getName());
            pathPdf = j.getSelectedFile().getAbsolutePath();

        } else {

        }
    }

    /**
     * Metodo que guarda el PDF, lo convierte en Byte y utiliza el metodo
     * insertPdf para guardarlo en la base de datos
     *
     * @param nombre
     * @param ruta
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void savePdf(String nombre, File ruta) throws SQLException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException, ClassNotFoundException {

        PdfGet gets = new PdfGet();
        gets.setNombrepdf(nombre);

        try {

            byte[] pdf = new byte[(int) ruta.length()];

            InputStream input = new FileInputStream(ruta);
            input.read(pdf);

            gets.setArchivopdf(pdf);

            insertPdf(MyConnection.get(), gets);

        } catch (IOException ex) {

            gets.setArchivopdf(null);

        }

        pnlBandejaPdf.removeAll();
        loadPnlPdf(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());
    }

    /**
     * Metodo que inserta el PDF en la Base de datos
     *
     * @param myConnection
     * @param vo
     */
    public void insertPdf(Connection myConnection, PdfGet vo) {

        try {

            Statement miStatement = myConnection.createStatement();
            PreparedStatement pstmt = myConnection.prepareStatement("INSERT INTO PDF (nombreclienteid ,nombrepdf, "
                    + "archivopdf) VALUES(?, ?, ?);");
            pstmt.setString(1, lblIDCustomers.getText() + lblNameShowC.getText());
            pstmt.setString(2, vo.getNombrepdf());
            pstmt.setBytes(3, vo.getArchivopdf());
            pstmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(e);

        }
    }

    /**
     * Metodo que carga los botones de los Pdf en el pnlBandejaPdf
     *
     * @param myConnection
     * @param nombreClienteId
     */
    public void loadPnlPdf(Connection myConnection, String nombreClienteId) {

        try {

            Statement miStatement = myConnection.createStatement();
            ResultSet miRs = miStatement.executeQuery("SELECT * FROM PDF "
                    + "WHERE nombreclienteid = '" + nombreClienteId + "'");
            int iterator2 = 1;

            while (miRs.next()) {

                Pdf personLoadPdf = new Pdf(miRs.getString("nombreclienteid"),
                        miRs.getString("nombrepdf"), miRs.getBytes("archivopdf"));

                PdfAction pa = new PdfAction(UI.this, personLoadPdf);

                CustomButton personButtonLoad = new CustomButton(pa);
                personButtonLoad.setText(miRs.getString("nombrepdf"));
                personButtonLoad.setComponentPopupMenu(jPopMenuBtn);

                GridBagConstraints c = new GridBagConstraints();
                iterator2++;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 2.0;
                c.insets = new Insets(3, 10, 3, 10);
                c.gridy = iterator2;
                personButtonLoad.setName(String.valueOf(miRs.getInt("id")));
                pnlBandejaPdf.add(personButtonLoad, c);
                pnlBandejaPdf.revalidate();
                pnlBandejaPdf.repaint();

            }

        } catch (SQLException e) {

            System.out.println(e);

        }

    }

    public void updatePdfWithNameid(Connection myConnection, String nombreclienteid, String nuevonombre) {

        String sql = "UPDATE PDF SET "
                + "nombreclienteid ='" + lblIDCustomers.getText() + nuevonombre + "' "
                + "WHERE nombreclienteid = '" + nombreclienteid + "'";

        try {

            PreparedStatement pst;

            try {

                Statement miStatementIns = myConnection.createStatement();
                pst = myConnection.prepareStatement(sql);
                pst.executeUpdate();

            } catch (SQLException ex) {

                Logger.getLogger(JTable.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR AL ACTUALIZAR " + ex);

            }

        } catch (HeadlessException e) {
            //Do nothing
        }
    }

    /**
     * Metodo que remueve el pdf
     *
     * @param myConnection
     * @param idPdf
     */
    void removePdf(Connection myConnection, int idPdf) {

        try {

            PreparedStatement pst = myConnection.prepareStatement("DELETE FROM PDF "
                    + "WHERE id = '" + idPdf + "'");
            pst.executeUpdate();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, e);

        }
    }

    /**
     * Metodo que remueve todos los Pdf pertenecientes a un cliente
     *
     * @param myConecction
     * @param nombreclienteid
     */
    void removeCustomerPdf(Connection myConecction, String nombreclienteid) {

        try {
            PreparedStatement pst = myConecction.prepareStatement("DELETE FROM PDF "
                    + "WHERE nombreclienteid = '" + nombreclienteid + "'");
            pst.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Metodo que insertala nueva obligacion en la base de datos
     *
     * @param myConecction
     * @param obligacionI
     * @param fechaI
     * @param horaI
     * @param sendI
     */
    public void insertObligation(Connection myConecction, String obligacionI, String fechaI, String horaI, String sendI) {

        try {
            Statement miStatement = myConecction.createStatement();
            PreparedStatement pstmt = myConecction.prepareStatement("INSERT INTO Obligaciones"
                    + " (nombreclienteid, nombrecliente, obligacion, fecha, hora, send) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, lblIDCustomers.getText() + lblNameShowC.getText());
            pstmt.setString(2, lblNameShowC.getText());
            pstmt.setString(3, obligacionI);
            pstmt.setString(4, fechaI);
            pstmt.setString(5, horaI);
            pstmt.setString(6, sendI);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Metodo que carga los botones de las obligaciones en el
     * pnlBandejaObligaciones
     *
     * @param myConnection
     * @param nombreClienteId
     * @throws IOException
     */
    public void loadPnlObligacion(Connection myConnection, String nombreClienteId) throws IOException {

        try {

            Statement miStatement = myConnection.createStatement();
            ResultSet miRs = miStatement.executeQuery("SELECT * FROM Obligaciones "
                    + "WHERE nombreclienteid = '" + nombreClienteId + "'");
            int iterator3 = 1;

            try (FileReader reader = new FileReader("config.properties")) {

                Properties properties = new Properties();
                properties.load(reader);
                txtEmailAEnviar.setText(properties.getProperty("correo"));

                while (miRs.next()) {

                    Obligation personObligacion = new Obligation(miRs.getInt("id"), miRs.getString("nombrecliente"), miRs.getString("obligacion"),
                            miRs.getString("fecha"), miRs.getString("hora"), miRs.getString("send"));
                    ObligationAction personObliAction = new ObligationAction(UI.this, personObligacion);

                    CustomButton personButtonLoad = new CustomButton(personObliAction);

                    if (miRs.getString("send").equals("sent")) {

                        personButtonLoad.setForeground(new Color(211, 0, 3));

                    } else if (miRs.getString("send").equals("notsent")) {

                        personButtonLoad.setForeground(new Color(13, 193, 67));

                    }
                    personButtonLoad.setText(miRs.getString("obligacion")
                            + " (" + miRs.getString("fecha") + ")");
                    GridBagConstraints c = new GridBagConstraints();
                    iterator3++;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.weightx = 2.0;
                    c.insets = new Insets(3, 10, 3, 10);
                    c.gridy = iterator3;
                    pnlBandejaObligaciones.add(personButtonLoad, c);
                    pnlBandejaObligaciones.revalidate();
                    pnlBandejaObligaciones.repaint();
                }

            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Metodo que carga el panel emergente el cual muestra la informacion de la
     * obligacion
     *
     * @param myConnection
     * @param id
     * @throws SQLException
     */
    public void loadPopupPnl(Connection myConnection, int id) throws SQLException {

        try (Statement miStatement = myConnection.createStatement();
                ResultSet miRs = miStatement.executeQuery("SELECT * FROM Obligaciones "
                        + "WHERE id = '" + id + "'");) {

            pnlTxtShowObligacion.setText(miRs.getString("obligacion"));
            lblIDObligacion.setText(String.valueOf(miRs.getInt("id")));
            String send = miRs.getString("send");

            if (send.equals("sent")) {

                lblShowFecha.setForeground(new Color(211, 0, 3));
                lblShowHora.setForeground(new Color(211, 0, 3));
                btnObligacionCompletada.setForeground(new Color(211, 0, 3));

            } else {

                lblShowFecha.setForeground(new Color(13, 193, 67));
                lblShowHora.setForeground(new Color(13, 193, 67));
                btnObligacionCompletada.setForeground(new Color(13, 193, 67));

            }

            lblShowFecha.setText(miRs.getString("fecha"));
            lblShowHora.setText(miRs.getString("hora"));
            dialogObligacion.setLocationRelativeTo(pnlBoxShowC);
            
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Metodo que remueve la obligacion indicada
     *
     * @param myConecction
     * @param id
     */
    public void removeObligationWithId(Connection myConecction, int id) {

        try {
            
            PreparedStatement pst = myConecction.prepareStatement("DELETE FROM Obligaciones "
                    + "WHERE id = '" + id + "'");
            pst.executeUpdate();

        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
            
        }
    }

    /**
     * Metodo que remueve todas las obligaciones pertenecientes al cliente
     *
     * @param myConecction
     * @param nombreclienteid
     */
    void RemoveCustomersObligaciones(Connection myConecction, String nombreclienteid) {

        try {
            
            PreparedStatement pst = myConecction.prepareStatement("DELETE FROM Obligaciones "
                    + "WHERE nombreclienteid = '" + nombreclienteid + "'");
            pst.executeUpdate();

        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, e);
            
        }
    }

    /**
     * Metodo que actualiza los datos introducidos en el panel emergente
     *
     * @param myConecction
     * @param id
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void UpdateObligation(Connection myConecction, int id) throws SQLException, ClassNotFoundException, ParseException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        SimpleDateFormat FormatterDate = new SimpleDateFormat("dd-MM-yyyy");
        
        String userDateEdited = editFechaPicker.getDate().format(formatter);
        String userTimeEdited = editHoraPicker.getTimeStringOrEmptyString();
        Date dateEdited = FormatterDate.parse(userDateEdited + " " + userTimeEdited);

        if (!checkDate.equals(dateEdited)) {

            updateObligationSendingOrNot(MyConnection.get(), idObligaciones, "notsent");

        }

        String sql = "UPDATE Obligaciones SET "
                + "obligacion ='" + pnlTxtShowObligacionEditar.getText() + "' "
                + ",fecha ='" + userDateEdited + "' "
                + ",hora ='" + userTimeEdited + "' "
                + "WHERE id = '" + id + "'";

        try {

            PreparedStatement pst;

            try {

                Statement miStatementIns = myConecction.createStatement();
                pst = myConecction.prepareStatement(sql);
                pst.executeUpdate();

            } catch (SQLException ex) {

                Logger.getLogger(JTable.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR AL ACTUALIZAR " + ex);

            }
        } catch (HeadlessException e) {
            //Do nothing
        }
    }

    /**
     * Metodo que actualiza la columna send que muestra si al usuario se le ha
     * notificado o no el vencimiento de la obligacion
     *
     * @param myConnection
     * @param id
     * @param send
     */
    public void updateObligationSendingOrNot(Connection myConnection, int id, String send) {

        String sql = "UPDATE Obligaciones SET "
                + "send ='" + send + "' "
                + "WHERE id = '" + id + "'";

        try {

            PreparedStatement pst;

            try {

                Statement miStatementIns = myConnection.createStatement();
                pst = myConnection.prepareStatement(sql);
                pst.executeUpdate();

            } catch (SQLException ex) {

                Logger.getLogger(JTable.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR AL ACTUALIZAR " + ex);

            }

        } catch (HeadlessException e) {
            
            //Do nothing
            
        }
    }

    public void updateObligationWithName(Connection myConnection, String nombreclienteid, String nuevonombre) {

        String sql = "UPDATE Obligaciones SET "
                + "nombrecliente ='" + nuevonombre + "', "
                + "nombreclienteid ='" + lblIDCustomers.getText() + nuevonombre + "' "
                + "WHERE nombreclienteid = '" + nombreclienteid + "'";

        try {

            PreparedStatement pst;

            try {

                Statement miStatementIns = myConnection.createStatement();
                pst = myConnection.prepareStatement(sql);
                pst.executeUpdate();

            } catch (SQLException ex) {

                Logger.getLogger(JTable.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "ERROR AL ACTUALIZAR " + ex);

            }

        } catch (HeadlessException e) {
            
            //Do nothing
            
        }
    }

    /**
     * Metodo que en tiempo de ejecucion verifica la fecha y hora de cada
     * obligacion de cada cliente y se encarga de usar los metodos para poder
     * notificar al usuario
     *
     * @param myConnection
     */
    public void reminders(Connection myConnection) {

        String sql = "SELECT * FROM Obligaciones";

        ActionListener timerReminders = (ActionEvent e) -> {

            try {

                Statement stmt = myConnection.createStatement();
                ResultSet miRs = stmt.executeQuery(sql);

                Calendar calendarioNow = Calendar.getInstance();
                SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
                Date dateNow = new Date();
                String time = formatterTime.format(dateNow);
                calendarioNow.setTime(dateNow);
                lblTime.setText(time);
                dayShow.setCalendar(calendarioNow);

                while (miRs.next()) {

                    SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    Calendar calendario = Calendar.getInstance();

                    String nombreCliente, obligacion, fecha, hora, send;
                    nombreCliente = miRs.getString("nombrecliente");
                    obligacion = miRs.getString("obligacion");
                    fecha = miRs.getString("fecha");
                    hora = miRs.getString("hora");
                    send = miRs.getString("send");

                    try {

                        calendario.setTime(formatterDate.parse(fecha + " " + hora));

                    } catch (ParseException ex) {

                        //Do nothing
                        
                    }

                    Date dateUser = calendario.getTime();

                    if (send.equals("notsent")) {

                        if (dateUser.before(dateNow)) {

                            updateObligationSendingOrNot(MyConnection.get(),
                                    miRs.getInt("id"), "sent");

                            pnlBandejaObligaciones.removeAll();
                            loadPnlObligacion(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());
                            loadPopupPnl(MyConnection.get(), idObligaciones);
                            dialogObligacion.revalidate();
                            dialogObligacion.repaint();
                            String titulo = "Obligacion con: " + nombreCliente;
                            String contenido = obligacion + "\nPara el Dia: " + fecha + " a las: " + hora;
                            hiddenIcon.trayIcon.displayMessage(titulo, contenido, TrayIcon.MessageType.NONE);
                            EmailSender emailS = new EmailSender();
                            emailS.EmailSender("Obligacion hoy con: " + nombreCliente, contenido, MyConnection.get());
                            emailS.start();

                        }
                    }
                }
            } catch (SQLException ev) {
                
                

            } catch (ClassNotFoundException | FileNotFoundException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                
            }

        };

        Timer timer = new Timer(1000, timerReminders);
        timer.setInitialDelay(0);
        timer.start();

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlNewCustumer = new javax.swing.JPanel();
        pnlJointNewC = new javax.swing.JPanel();
        pnlTopNewC = new javax.swing.JPanel();
        lblClientManagerNewC = new javax.swing.JLabel();
        btnReturnNewC = new javax.swing.JButton();
        pnlHeaderNewC = new javax.swing.JPanel();
        lblNameNewC = new javax.swing.JLabel();
        btnGuardarNewC = new javax.swing.JButton();
        btnCancelarNewC = new javax.swing.JButton();
        pnlMenuNewC = new javax.swing.JPanel();
        btnInformacionNewC = new javax.swing.JButton();
        pnlBoxNewC = new javax.swing.JPanel();
        pnlFormNewC = new javax.swing.JPanel();
        txtContraseñaMuniPaNewC = new javax.swing.JTextField();
        lblRucNewC = new javax.swing.JLabel();
        txtNoContribuyenteNewC = new javax.swing.JTextField();
        txtNitNewC = new javax.swing.JTextField();
        lblNombreContactoNewC = new javax.swing.JLabel();
        lblNitNewC = new javax.swing.JLabel();
        lblRepLegalNewC = new javax.swing.JLabel();
        txtNombreContactoNewC = new javax.swing.JTextField();
        txtCorreoNewC = new javax.swing.JTextField();
        lblContraseñaMuniPaNewC = new javax.swing.JLabel();
        txtRepLegalNewC = new javax.swing.JTextField();
        lblCedulaRepNewC = new javax.swing.JLabel();
        txtRucNewC = new javax.swing.JTextField();
        txtCedulaRepNewC = new javax.swing.JTextField();
        lblNumeroContactoNewC = new javax.swing.JLabel();
        lblCorreoContactoNewC = new javax.swing.JLabel();
        txtNumeroContactoNewC = new javax.swing.JTextField();
        lblNoContribuyenteNewC = new javax.swing.JLabel();
        lblInformacionNewC = new javax.swing.JLabel();
        txtNombreNewC = new javax.swing.JTextField();
        lblNombreNewC = new javax.swing.JLabel();
        pnlShowCustomer = new javax.swing.JPanel();
        pnlJointShowC = new javax.swing.JPanel();
        pnlTopShowC = new javax.swing.JPanel();
        lblClientManagerShowC = new javax.swing.JLabel();
        btnReturnShowC = new javax.swing.JButton();
        pnlHeaderShowC = new javax.swing.JPanel();
        lblNameShowC = new javax.swing.JLabel();
        btnEditarShowC = new javax.swing.JButton();
        btnEliminarShowC = new javax.swing.JButton();
        btnGuardarCambiosShowC = new javax.swing.JButton();
        btnCancelarShowC = new javax.swing.JButton();
        btnGuardarPdfShowC = new javax.swing.JButton();
        btnCancelarPdfShowC = new javax.swing.JButton();
        btnGuardarObligacionShowC = new javax.swing.JButton();
        pnlMenuShowC = new javax.swing.JPanel();
        btnInformacionShowC = new javax.swing.JButton();
        btnAvisoDeOperacionesShowC = new javax.swing.JButton();
        btnObligacionesFiscalesShowC = new javax.swing.JButton();
        pnlBoxShowC = new javax.swing.JPanel();
        pnlFormShowC = new javax.swing.JPanel();
        txtContraseñaMuniPaShowC = new javax.swing.JTextField();
        lblRucShowC = new javax.swing.JLabel();
        txtNoContribuyenteShowC = new javax.swing.JTextField();
        txtNitShowC = new javax.swing.JTextField();
        lblNombreContactoShowC = new javax.swing.JLabel();
        lblNitShowC = new javax.swing.JLabel();
        lblRepLegalShowC = new javax.swing.JLabel();
        txtNombreContactoShowC = new javax.swing.JTextField();
        txtCorreoContactoShowC = new javax.swing.JTextField();
        lblContraseñaMuniPaShowC = new javax.swing.JLabel();
        txtRepLegalShowC = new javax.swing.JTextField();
        lblCedulaRepShowC = new javax.swing.JLabel();
        txtRucShowC = new javax.swing.JTextField();
        txtCedulaRepLegalShowC = new javax.swing.JTextField();
        lblNumeroContactoShowC = new javax.swing.JLabel();
        lblCorreoShowC = new javax.swing.JLabel();
        txtNumeroContactoShowC = new javax.swing.JTextField();
        lblNoContribuyenteShowC = new javax.swing.JLabel();
        lblInformacionShowC = new javax.swing.JLabel();
        lblIDCustomers = new javax.swing.JLabel();
        pnlEditCustomer = new javax.swing.JPanel();
        txtContraseñaEditC = new javax.swing.JTextField();
        lblRucEditC = new javax.swing.JLabel();
        txtNoContribuyenteEditC = new javax.swing.JTextField();
        txtNitEditC = new javax.swing.JTextField();
        lblNombreContactoEditC = new javax.swing.JLabel();
        lblNitEditC = new javax.swing.JLabel();
        lblRepLegalEditC = new javax.swing.JLabel();
        txtNombreContactoEditC = new javax.swing.JTextField();
        txtCorreoEditC = new javax.swing.JTextField();
        lblContraseñaMuniPaEditC = new javax.swing.JLabel();
        txtRepLegalEditC = new javax.swing.JTextField();
        lblCedulaRepEditC = new javax.swing.JLabel();
        txtRucEditC = new javax.swing.JTextField();
        txtCedulaRepEditC = new javax.swing.JTextField();
        lblNumeroContactoEditC = new javax.swing.JLabel();
        lblCorreoEditC = new javax.swing.JLabel();
        txtNumeroContactoEditC = new javax.swing.JTextField();
        lblNoContribuyente = new javax.swing.JLabel();
        lblEditarInformacionEditC = new javax.swing.JLabel();
        txtNombreEditC = new javax.swing.JTextField();
        lblNombreEditC = new javax.swing.JLabel();
        jPopMenuBtn = new javax.swing.JPopupMenu();
        jPopOptions = new javax.swing.JMenu();
        jPopMenuItemEliminar = new javax.swing.JMenuItem();
        pnlAvisoDeOperaciones = new javax.swing.JPanel();
        lblAvisosDeOperacionesHelp = new javax.swing.JLabel();
        pnlAvisoDeOperacionesHeader = new javax.swing.JPanel();
        lblSeleccionarPDF = new javax.swing.JLabel();
        txtNombrePdf = new javax.swing.JTextField();
        btnSeleccionarPdf = new javax.swing.JButton();
        lblNombreDelPDF = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        scrollPanePdf = new javax.swing.JScrollPane();
        pnlBandejaPdf = new javax.swing.JPanel();
        lblAvisosDeOperaciones = new javax.swing.JLabel();
        pnlObligacionesFiscales = new javax.swing.JPanel();
        lblObligacionesFiscales = new javax.swing.JLabel();
        pnlObligacionesFiscalesHeader = new javax.swing.JPanel();
        lblAgregarObligacion = new javax.swing.JLabel();
        txtObligacion = new javax.swing.JTextField();
        lblFecha = new javax.swing.JLabel();
        Locale locale = new Locale("es");
        DatePickerSettings settings = new DatePickerSettings(locale);
        datePickerNewObligation = new com.github.lgooddatepicker.components.DatePicker(settings);
        timePickerNewObligation = new com.github.lgooddatepicker.components.TimePicker();
        lblObligacionesFiscalesHelper = new javax.swing.JLabel();
        lblConfigEmail = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        scrollPaneObligaciones = new javax.swing.JScrollPane();
        pnlBandejaObligaciones = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dialogObligacion = new javax.swing.JDialog(this);
        pnlBoxDialogObligacion = new javax.swing.JPanel();
        pnlDialogShowObligacion = new javax.swing.JPanel();
        pnlTopObligacionPopup = new javax.swing.JPanel();
        lblInformacionDeLaObligacion = new javax.swing.JLabel();
        btnCloseDialog = new javax.swing.JButton();
        pnlEmergenteBox = new javax.swing.JPanel();
        pnlEmergenteInfo = new javax.swing.JPanel();
        lblShowHora = new javax.swing.JLabel();
        scrollPaneDialog = new javax.swing.JScrollPane();
        pnlTxtShowObligacion = new javax.swing.JTextPane();
        lblShowFecha = new javax.swing.JLabel();
        btnObligacionCompletada = new javax.swing.JButton();
        btnEditarObligacion = new javax.swing.JButton();
        lblIDObligacion = new javax.swing.JLabel();
        pnlDialogObligacionEditar = new javax.swing.JPanel();
        pnlObligacionEditarHeader = new javax.swing.JPanel();
        lblInformacionDeLaObligacionEditar = new javax.swing.JLabel();
        pnlEmergenteBox1 = new javax.swing.JPanel();
        pnlEmergenteInfo1 = new javax.swing.JPanel();
        lblHoraEditar = new javax.swing.JLabel();
        scrollPaneObligaciontEdit = new javax.swing.JScrollPane();
        pnlTxtShowObligacionEditar = new javax.swing.JTextPane();
        lblEditFecha = new javax.swing.JLabel();
        btnGuardarCambioObligacion = new javax.swing.JButton();
        btnCancelarEditar = new javax.swing.JButton();
        Locale locale2 = new Locale("es");
        DatePickerSettings settings2 = new DatePickerSettings(locale2);
        editFechaPicker = new com.github.lgooddatepicker.components.DatePicker(settings2);
        editHoraPicker = new com.github.lgooddatepicker.components.TimePicker();
        lblID = new javax.swing.JLabel();
        dialogAboutMe = new javax.swing.JDialog(this);
        pnlContainerAboutMe = new javax.swing.JPanel();
        pnlAboutMeHeader = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnCloseAboutMe = new javax.swing.JButton();
        pnlAboutMeComponents = new javax.swing.JPanel();
        lblShowWhatsapp = new javax.swing.JLabel();
        lblProgramador = new javax.swing.JLabel();
        lblMiCorreo = new javax.swing.JLabel();
        lblCreadoEl = new javax.swing.JLabel();
        lblFinalizadoEl = new javax.swing.JLabel();
        lblCustomersManager = new javax.swing.JLabel();
        gif = new javax.swing.JLabel();
        dialogAvisoDeOperacionesHelp = new javax.swing.JDialog(this);
        pnlDialogContainerAvisoDeOperaciones = new javax.swing.JPanel();
        pnlManejoDelAvisoDeOperacionesHeader = new javax.swing.JPanel();
        lblManejoDeOperaciones = new javax.swing.JLabel();
        btnCloseAvisoDeOperacionesHelper = new javax.swing.JButton();
        pnlDialogComponents = new javax.swing.JPanel();
        pnlContainerTextAvisoDeOperacionesHelper = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtPaneAvsioHelper = new javax.swing.JTextPane();
        dialogObligacionesHelp = new javax.swing.JDialog(this);
        pnlContainerObligaciones = new javax.swing.JPanel();
        pnlObligacionesHeader = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnClosePnlHelperObligaciones = new javax.swing.JButton();
        pnlObligacionesComponent = new javax.swing.JPanel();
        pnlContainerTextOblifgacion = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtPaneObligacionHelper = new javax.swing.JTextPane();
        dialogConfigObligaciones = new javax.swing.JDialog(this);
        pnlContainerObligaciones1 = new javax.swing.JPanel();
        pnlObligacionesHeader1 = new javax.swing.JPanel();
        lblConfiguracionDeObligaciones = new javax.swing.JLabel();
        btnCloseYGuardarCambiosObligacion = new javax.swing.JButton();
        btnCloseYCancelarCambiosObligacion = new javax.swing.JButton();
        pnlObligacionesComponent1 = new javax.swing.JPanel();
        pnlContainerTextOblifgacion1 = new javax.swing.JPanel();
        lblEmailAEnviar = new javax.swing.JLabel();
        txtEmailAEnviar = new javax.swing.JTextField();
        pnlFrame = new javax.swing.JPanel();
        pnlBoxFrame = new javax.swing.JPanel();
        pnlStart = new javax.swing.JPanel();
        pnlTopStart = new javax.swing.JPanel();
        lblClientManager = new javax.swing.JLabel();
        pnlHeader = new javax.swing.JPanel();
        btnNuevoCliente = new javax.swing.JButton();
        lblNameUser = new javax.swing.JLabel();
        txtCustomers = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnSearchCancel = new javax.swing.JButton();
        pnlContenedorBandeja = new javax.swing.JPanel();
        scrollPaneCustomers = new javax.swing.JScrollPane();
        pnlBandeja = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlMenuStart = new javax.swing.JPanel();
        lblTime = new javax.swing.JLabel();
        dayShow = new com.toedter.calendar.JDayChooser();
        lblTime1 = new javax.swing.JLabel();
        btnAboutMe = new javax.swing.JButton();

        pnlNewCustumer.setBackground(new java.awt.Color(102, 137, 100));
        pnlNewCustumer.setPreferredSize(new java.awt.Dimension(1200, 520));
        pnlNewCustumer.setLayout(new java.awt.GridBagLayout());

        pnlJointNewC.setBackground(new java.awt.Color(0, 153, 153));
        pnlJointNewC.setMinimumSize(new java.awt.Dimension(950, 520));
        pnlJointNewC.setOpaque(false);
        pnlJointNewC.setPreferredSize(new java.awt.Dimension(950, 520));
        pnlJointNewC.setLayout(new java.awt.GridBagLayout());

        pnlTopNewC.setBackground(new java.awt.Color(211, 0, 3));
        pnlTopNewC.setLayout(new java.awt.GridBagLayout());

        lblClientManagerNewC.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblClientManagerNewC.setText("Nuevo cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlTopNewC.add(lblClientManagerNewC, gridBagConstraints);

        btnReturnNewC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Back_To_32px_1.png"))); // NOI18N
        btnReturnNewC.setBorder(null);
        btnReturnNewC.setBorderPainted(false);
        btnReturnNewC.setContentAreaFilled(false);
        btnReturnNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReturnNewC.setFocusPainted(false);
        btnReturnNewC.setFocusable(false);
        btnReturnNewC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Back_To_28px_2.png"))); // NOI18N
        btnReturnNewC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnNewCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlTopNewC.add(btnReturnNewC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlJointNewC.add(pnlTopNewC, gridBagConstraints);

        pnlHeaderNewC.setBackground(new java.awt.Color(47, 43, 45));
        pnlHeaderNewC.setLayout(new java.awt.GridBagLayout());

        lblNameNewC.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblNameNewC.setForeground(new java.awt.Color(255, 255, 255));
        lblNameNewC.setText("Nuevo cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlHeaderNewC.add(lblNameNewC, gridBagConstraints);

        btnGuardarNewC.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardarNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnGuardarNewC.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarNewC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_28px.png"))); // NOI18N
        btnGuardarNewC.setText("Guardar Cliente");
        btnGuardarNewC.setBorder(null);
        btnGuardarNewC.setBorderPainted(false);
        btnGuardarNewC.setContentAreaFilled(false);
        btnGuardarNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarNewC.setFocusPainted(false);
        btnGuardarNewC.setFocusable(false);
        btnGuardarNewC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_22px_1.png"))); // NOI18N
        btnGuardarNewC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarNewCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderNewC.add(btnGuardarNewC, gridBagConstraints);

        btnCancelarNewC.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelarNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCancelarNewC.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarNewC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_28px.png"))); // NOI18N
        btnCancelarNewC.setText("Cancelar");
        btnCancelarNewC.setBorder(null);
        btnCancelarNewC.setBorderPainted(false);
        btnCancelarNewC.setContentAreaFilled(false);
        btnCancelarNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarNewC.setFocusPainted(false);
        btnCancelarNewC.setFocusable(false);
        btnCancelarNewC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_22px.png"))); // NOI18N
        btnCancelarNewC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarNewCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderNewC.add(btnCancelarNewC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlJointNewC.add(pnlHeaderNewC, gridBagConstraints);

        pnlMenuNewC.setBackground(new java.awt.Color(47, 43, 45));
        pnlMenuNewC.setMinimumSize(new java.awt.Dimension(50, 50));
        pnlMenuNewC.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlMenuNewC.setLayout(new java.awt.GridBagLayout());

        btnInformacionNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnInformacionNewC.setForeground(new java.awt.Color(255, 255, 255));
        btnInformacionNewC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Contacts_28px_2.png"))); // NOI18N
        btnInformacionNewC.setText("    Informacion");
        btnInformacionNewC.setBorderPainted(false);
        btnInformacionNewC.setContentAreaFilled(false);
        btnInformacionNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInformacionNewC.setFocusPainted(false);
        btnInformacionNewC.setFocusable(false);
        btnInformacionNewC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInformacionNewC.setInheritsPopupMenu(true);
        btnInformacionNewC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Contacts_22px.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMenuNewC.add(btnInformacionNewC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        pnlJointNewC.add(pnlMenuNewC, gridBagConstraints);

        pnlBoxNewC.setBackground(new java.awt.Color(255, 51, 51));
        pnlBoxNewC.setPreferredSize(new java.awt.Dimension(890, 465));
        pnlBoxNewC.setLayout(new javax.swing.BoxLayout(pnlBoxNewC, javax.swing.BoxLayout.LINE_AXIS));

        pnlFormNewC.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormNewC.setLayout(new java.awt.GridBagLayout());

        txtContraseñaMuniPaNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtContraseñaMuniPaNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtContraseñaMuniPaNewC.setToolTipText("");
        txtContraseñaMuniPaNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtContraseñaMuniPaNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtContraseñaMuniPaNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtContraseñaMuniPaNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtContraseñaMuniPaNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtContraseñaMuniPaNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtContraseñaMuniPaNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 20, 0);
        pnlFormNewC.add(txtContraseñaMuniPaNewC, gridBagConstraints);

        lblRucNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRucNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblRucNewC.setText("RUC:");
        lblRucNewC.setFocusable(false);
        lblRucNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRucNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblRucNewC, gridBagConstraints);

        txtNoContribuyenteNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtNoContribuyenteNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNoContribuyenteNewC.setToolTipText("");
        txtNoContribuyenteNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNoContribuyenteNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNoContribuyenteNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNoContribuyenteNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNoContribuyenteNewC.setNextFocusableComponent(txtContraseñaMuniPaNewC);
        txtNoContribuyenteNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNoContribuyenteNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNoContribuyenteNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtNoContribuyenteNewC, gridBagConstraints);

        txtNitNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtNitNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNitNewC.setToolTipText("");
        txtNitNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNitNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNitNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNitNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNitNewC.setNextFocusableComponent(txtNoContribuyenteNewC);
        txtNitNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNitNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNitNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtNitNewC, gridBagConstraints);

        lblNombreContactoNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNombreContactoNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblNombreContactoNewC.setText("NOMBRE CONTACTO COMPAÑIA:");
        lblNombreContactoNewC.setFocusable(false);
        lblNombreContactoNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNombreContactoNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblNombreContactoNewC, gridBagConstraints);

        lblNitNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNitNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblNitNewC.setText("NIT:");
        lblNitNewC.setFocusable(false);
        lblNitNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNitNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblNitNewC, gridBagConstraints);

        lblRepLegalNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRepLegalNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblRepLegalNewC.setText("REP LEGAL:");
        lblRepLegalNewC.setFocusable(false);
        lblRepLegalNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRepLegalNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblRepLegalNewC, gridBagConstraints);

        txtNombreContactoNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtNombreContactoNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombreContactoNewC.setToolTipText("");
        txtNombreContactoNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNombreContactoNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNombreContactoNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNombreContactoNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNombreContactoNewC.setNextFocusableComponent(txtCorreoNewC);
        txtNombreContactoNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNombreContactoNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombreContactoNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtNombreContactoNewC, gridBagConstraints);

        txtCorreoNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtCorreoNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCorreoNewC.setToolTipText("");
        txtCorreoNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCorreoNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtCorreoNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCorreoNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtCorreoNewC.setNextFocusableComponent(txtNumeroContactoNewC);
        txtCorreoNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtCorreoNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCorreoNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 20, 0);
        pnlFormNewC.add(txtCorreoNewC, gridBagConstraints);

        lblContraseñaMuniPaNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblContraseñaMuniPaNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblContraseñaMuniPaNewC.setText("CONTRASEÑA DEL MUNI DE PA:");
        lblContraseñaMuniPaNewC.setFocusable(false);
        lblContraseñaMuniPaNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblContraseñaMuniPaNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblContraseñaMuniPaNewC, gridBagConstraints);

        txtRepLegalNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtRepLegalNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRepLegalNewC.setToolTipText("");
        txtRepLegalNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRepLegalNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtRepLegalNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRepLegalNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtRepLegalNewC.setNextFocusableComponent(txtNombreContactoNewC);
        txtRepLegalNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtRepLegalNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRepLegalNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtRepLegalNewC, gridBagConstraints);

        lblCedulaRepNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCedulaRepNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblCedulaRepNewC.setText("CEDULA REP LEGAL:");
        lblCedulaRepNewC.setFocusable(false);
        lblCedulaRepNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCedulaRepNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblCedulaRepNewC, gridBagConstraints);

        txtRucNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtRucNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRucNewC.setToolTipText("");
        txtRucNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRucNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtRucNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRucNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtRucNewC.setNextFocusableComponent(txtRepLegalNewC);
        txtRucNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtRucNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRucNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtRucNewC, gridBagConstraints);

        txtCedulaRepNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtCedulaRepNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCedulaRepNewC.setToolTipText("");
        txtCedulaRepNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCedulaRepNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtCedulaRepNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCedulaRepNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtCedulaRepNewC.setNextFocusableComponent(txtNitNewC);
        txtCedulaRepNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtCedulaRepNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCedulaRepNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtCedulaRepNewC, gridBagConstraints);

        lblNumeroContactoNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNumeroContactoNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblNumeroContactoNewC.setText("NUMERO CONTACTO COMPAÑIA:");
        lblNumeroContactoNewC.setFocusable(false);
        lblNumeroContactoNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNumeroContactoNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 50, 0, 0);
        pnlFormNewC.add(lblNumeroContactoNewC, gridBagConstraints);

        lblCorreoContactoNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCorreoContactoNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblCorreoContactoNewC.setText("CORREO CONTACTO COMPAÑIA:");
        lblCorreoContactoNewC.setFocusable(false);
        lblCorreoContactoNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCorreoContactoNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblCorreoContactoNewC, gridBagConstraints);

        txtNumeroContactoNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtNumeroContactoNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNumeroContactoNewC.setToolTipText("");
        txtNumeroContactoNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNumeroContactoNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNumeroContactoNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNumeroContactoNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNumeroContactoNewC.setNextFocusableComponent(txtCedulaRepNewC);
        txtNumeroContactoNewC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNumeroContactoNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNumeroContactoNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtNumeroContactoNewC, gridBagConstraints);

        lblNoContribuyenteNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNoContribuyenteNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblNoContribuyenteNewC.setText("NO CONTRIBUYENTE DEL MUNI DE PA:");
        lblNoContribuyenteNewC.setFocusable(false);
        lblNoContribuyenteNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNoContribuyenteNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblNoContribuyenteNewC, gridBagConstraints);

        lblInformacionNewC.setText("Informacion");
        lblInformacionNewC.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        pnlFormNewC.add(lblInformacionNewC, gridBagConstraints);

        txtNombreNewC.setBackground(new java.awt.Color(223, 227, 230));
        txtNombreNewC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombreNewC.setToolTipText("");
        txtNombreNewC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNombreNewC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNombreNewC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNombreNewC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNombreNewC.setNextFocusableComponent(txtRucNewC);
        txtNombreNewC.setPreferredSize(new java.awt.Dimension(0, 20));
        txtNombreNewC.setRequestFocusEnabled(false);
        txtNombreNewC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombreNewC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormNewC.add(txtNombreNewC, gridBagConstraints);

        lblNombreNewC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNombreNewC.setForeground(new java.awt.Color(102, 102, 102));
        lblNombreNewC.setText("NOMBRE:");
        lblNombreNewC.setFocusable(false);
        lblNombreNewC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNombreNewC.setPreferredSize(new java.awt.Dimension(300, 20));
        lblNombreNewC.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormNewC.add(lblNombreNewC, gridBagConstraints);

        pnlBoxNewC.add(pnlFormNewC);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlJointNewC.add(pnlBoxNewC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 2.0;
        pnlNewCustumer.add(pnlJointNewC, gridBagConstraints);

        pnlShowCustomer.setBackground(new java.awt.Color(102, 137, 100));
        pnlShowCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlShowCustomer.setLayout(new java.awt.GridBagLayout());

        pnlJointShowC.setBackground(new java.awt.Color(0, 153, 153));
        pnlJointShowC.setMinimumSize(new java.awt.Dimension(950, 520));
        pnlJointShowC.setPreferredSize(new java.awt.Dimension(950, 520));
        pnlJointShowC.setOpaque(false);
        pnlJointShowC.setLayout(new java.awt.GridBagLayout());

        pnlTopShowC.setBackground(new java.awt.Color(211, 0, 3));
        pnlTopShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlTopShowC.setLayout(new java.awt.GridBagLayout());

        lblClientManagerShowC.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblClientManagerShowC.setText("Informacion del cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BELOW_BASELINE_LEADING;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        pnlTopShowC.add(lblClientManagerShowC, gridBagConstraints);

        btnReturnShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Back_To_32px_1.png"))); // NOI18N
        btnReturnShowC.setBorder(null);
        btnReturnShowC.setBorderPainted(false);
        btnReturnShowC.setContentAreaFilled(false);
        btnReturnShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReturnShowC.setFocusPainted(false);
        btnReturnShowC.setFocusable(false);
        btnReturnShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Back_To_28px_2.png"))); // NOI18N
        btnReturnShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlTopShowC.add(btnReturnShowC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlJointShowC.add(pnlTopShowC, gridBagConstraints);

        pnlHeaderShowC.setBackground(new java.awt.Color(47, 43, 45));
        pnlHeaderShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlHeaderShowC.setLayout(new java.awt.GridBagLayout());

        lblNameShowC.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblNameShowC.setForeground(new java.awt.Color(255, 255, 255));
        lblNameShowC.setText("Juan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlHeaderShowC.add(lblNameShowC, gridBagConstraints);

        btnEditarShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnEditarShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnEditarShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Edit_28px_1.png"))); // NOI18N
        btnEditarShowC.setText("Editar");
        btnEditarShowC.setBorder(null);
        btnEditarShowC.setBorderPainted(false);
        btnEditarShowC.setContentAreaFilled(false);
        btnEditarShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditarShowC.setFocusPainted(false);
        btnEditarShowC.setFocusable(false);
        btnEditarShowC.setPreferredSize(new java.awt.Dimension(73, 30));
        btnEditarShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Edit_22px_1.png"))); // NOI18N
        btnEditarShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnEditarShowC, gridBagConstraints);

        btnEliminarShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnEliminarShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnEliminarShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Delete_28px.png"))); // NOI18N
        btnEliminarShowC.setText("Eliminar");
        btnEliminarShowC.setBorder(null);
        btnEliminarShowC.setBorderPainted(false);
        btnEliminarShowC.setContentAreaFilled(false);
        btnEliminarShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminarShowC.setFocusPainted(false);
        btnEliminarShowC.setFocusable(false);
        btnEliminarShowC.setPreferredSize(new java.awt.Dimension(88, 30));
        btnEliminarShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Delete_22px.png"))); // NOI18N
        btnEliminarShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnEliminarShowC, gridBagConstraints);

        btnGuardarCambiosShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardarCambiosShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnGuardarCambiosShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarCambiosShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_28px.png"))); // NOI18N
        btnGuardarCambiosShowC.setText("Guardar Cambios");
        btnGuardarCambiosShowC.setBorder(null);
        btnGuardarCambiosShowC.setBorderPainted(false);
        btnGuardarCambiosShowC.setContentAreaFilled(false);
        btnGuardarCambiosShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarCambiosShowC.setEnabled(false);
        btnGuardarCambiosShowC.setFocusPainted(false);
        btnGuardarCambiosShowC.setFocusable(false);
        btnGuardarCambiosShowC.setPreferredSize(new java.awt.Dimension(153, 30));
        btnGuardarCambiosShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_22px_1.png"))); // NOI18N
        btnGuardarCambiosShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCambiosShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnGuardarCambiosShowC, gridBagConstraints);

        btnCancelarShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelarShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCancelarShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_28px.png"))); // NOI18N
        btnCancelarShowC.setText("Cancelar");
        btnCancelarShowC.setBorder(null);
        btnCancelarShowC.setBorderPainted(false);
        btnCancelarShowC.setContentAreaFilled(false);
        btnCancelarShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarShowC.setEnabled(false);
        btnCancelarShowC.setFocusPainted(false);
        btnCancelarShowC.setFocusable(false);
        btnCancelarShowC.setPreferredSize(new java.awt.Dimension(94, 30));
        btnCancelarShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_22px.png"))); // NOI18N
        btnCancelarShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnCancelarShowC, gridBagConstraints);

        btnGuardarPdfShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardarPdfShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnGuardarPdfShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarPdfShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_28px.png"))); // NOI18N
        btnGuardarPdfShowC.setText("Guardar PDF");
        btnGuardarPdfShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnGuardarPdfShowC.setBorderPainted(false);
        btnGuardarPdfShowC.setContentAreaFilled(false);
        btnGuardarPdfShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarPdfShowC.setEnabled(false);
        btnGuardarPdfShowC.setFocusable(false);
        btnGuardarPdfShowC.setPreferredSize(new java.awt.Dimension(123, 30));
        btnGuardarPdfShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_22px_1.png"))); // NOI18N
        btnGuardarPdfShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPdfShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnGuardarPdfShowC, gridBagConstraints);

        btnCancelarPdfShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelarPdfShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCancelarPdfShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelarPdfShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_28px.png"))); // NOI18N
        btnCancelarPdfShowC.setText("Cancelar");
        btnCancelarPdfShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnCancelarPdfShowC.setBorderPainted(false);
        btnCancelarPdfShowC.setContentAreaFilled(false);
        btnCancelarPdfShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarPdfShowC.setEnabled(false);
        btnCancelarPdfShowC.setFocusable(false);
        btnCancelarPdfShowC.setPreferredSize(new java.awt.Dimension(97, 30));
        btnCancelarPdfShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_22px.png"))); // NOI18N
        btnCancelarPdfShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPdfShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnCancelarPdfShowC, gridBagConstraints);

        btnGuardarObligacionShowC.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardarObligacionShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnGuardarObligacionShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarObligacionShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_28px.png"))); // NOI18N
        btnGuardarObligacionShowC.setText("Guardar Obligacion");
        btnGuardarObligacionShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnGuardarObligacionShowC.setBorderPainted(false);
        btnGuardarObligacionShowC.setContentAreaFilled(false);
        btnGuardarObligacionShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarObligacionShowC.setEnabled(false);
        btnGuardarObligacionShowC.setFocusable(false);
        btnGuardarObligacionShowC.setPreferredSize(new java.awt.Dimension(171, 30));
        btnGuardarObligacionShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_22px_1.png"))); // NOI18N
        btnGuardarObligacionShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarObligacionShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlHeaderShowC.add(btnGuardarObligacionShowC, gridBagConstraints);
        btnGuardarObligacionShowC.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlJointShowC.add(pnlHeaderShowC, gridBagConstraints);

        pnlMenuShowC.setBackground(new java.awt.Color(47, 43, 45));
        pnlMenuShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlMenuShowC.setMinimumSize(new java.awt.Dimension(50, 50));
        pnlMenuShowC.setPreferredSize(new java.awt.Dimension(50, 50));
        pnlMenuShowC.setLayout(new java.awt.GridBagLayout());

        btnInformacionShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnInformacionShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnInformacionShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Contacts_28px_2.png"))); // NOI18N
        btnInformacionShowC.setText("    Informacion");
        btnInformacionShowC.setBorderPainted(false);
        btnInformacionShowC.setContentAreaFilled(false);
        btnInformacionShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInformacionShowC.setFocusPainted(false);
        btnInformacionShowC.setFocusable(false);
        btnInformacionShowC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnInformacionShowC.setInheritsPopupMenu(true);
        btnInformacionShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Contacts_22px.png"))); // NOI18N
        btnInformacionShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformacionShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(100, 0, 0, 0);
        pnlMenuShowC.add(btnInformacionShowC, gridBagConstraints);

        btnAvisoDeOperacionesShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnAvisoDeOperacionesShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnAvisoDeOperacionesShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Document_28px.png"))); // NOI18N
        btnAvisoDeOperacionesShowC.setText("Aviso de Operaciones");
        btnAvisoDeOperacionesShowC.setBorderPainted(false);
        btnAvisoDeOperacionesShowC.setContentAreaFilled(false);
        btnAvisoDeOperacionesShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAvisoDeOperacionesShowC.setFocusPainted(false);
        btnAvisoDeOperacionesShowC.setFocusable(false);
        btnAvisoDeOperacionesShowC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAvisoDeOperacionesShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Document_22px.png"))); // NOI18N
        btnAvisoDeOperacionesShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvisoDeOperacionesShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMenuShowC.add(btnAvisoDeOperacionesShowC, gridBagConstraints);

        btnObligacionesFiscalesShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnObligacionesFiscalesShowC.setForeground(new java.awt.Color(255, 255, 255));
        btnObligacionesFiscalesShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Calendar_28px.png"))); // NOI18N
        btnObligacionesFiscalesShowC.setText("Obligaciones Fiscales");
        btnObligacionesFiscalesShowC.setBorderPainted(false);
        btnObligacionesFiscalesShowC.setContentAreaFilled(false);
        btnObligacionesFiscalesShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnObligacionesFiscalesShowC.setFocusPainted(false);
        btnObligacionesFiscalesShowC.setFocusable(false);
        btnObligacionesFiscalesShowC.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnObligacionesFiscalesShowC.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Calendar_22px.png"))); // NOI18N
        btnObligacionesFiscalesShowC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObligacionesFiscalesShowCActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 100, 0);
        pnlMenuShowC.add(btnObligacionesFiscalesShowC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        pnlJointShowC.add(pnlMenuShowC, gridBagConstraints);

        pnlBoxShowC.setBackground(new java.awt.Color(255, 51, 51));
        pnlBoxShowC.setPreferredSize(new java.awt.Dimension(890, 465));
        pnlBoxShowC.setLayout(new javax.swing.BoxLayout(pnlBoxShowC, javax.swing.BoxLayout.LINE_AXIS));

        pnlFormShowC.setBackground(new java.awt.Color(255, 255, 255));
        pnlFormShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlFormShowC.setMinimumSize(new java.awt.Dimension(400, 400));
        pnlFormShowC.setPreferredSize(new java.awt.Dimension(0, 0));
        pnlFormShowC.setLayout(new java.awt.GridBagLayout());

        txtContraseñaMuniPaShowC.setEditable(false);
        txtContraseñaMuniPaShowC.setColumns(2);
        txtContraseñaMuniPaShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtContraseñaMuniPaShowC.setToolTipText("");
        txtContraseñaMuniPaShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtContraseñaMuniPaShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtContraseñaMuniPaShowC.setOpaque(false);
        txtContraseñaMuniPaShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtContraseñaMuniPaShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 100, 0, 0);
        pnlFormShowC.add(txtContraseñaMuniPaShowC, gridBagConstraints);

        lblRucShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRucShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblRucShowC.setText("RUC:");
        lblRucShowC.setFocusable(false);
        lblRucShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRucShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblRucShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormShowC.add(lblRucShowC, gridBagConstraints);

        txtNoContribuyenteShowC.setEditable(false);
        txtNoContribuyenteShowC.setColumns(2);
        txtNoContribuyenteShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNoContribuyenteShowC.setToolTipText("");
        txtNoContribuyenteShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNoContribuyenteShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNoContribuyenteShowC.setOpaque(false);
        txtNoContribuyenteShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNoContribuyenteShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 100, 0, 0);
        pnlFormShowC.add(txtNoContribuyenteShowC, gridBagConstraints);

        txtNitShowC.setEditable(false);
        txtNitShowC.setColumns(2);
        txtNitShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNitShowC.setToolTipText("");
        txtNitShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNitShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNitShowC.setOpaque(false);
        txtNitShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNitShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 100, 0, 0);
        pnlFormShowC.add(txtNitShowC, gridBagConstraints);

        lblNombreContactoShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNombreContactoShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblNombreContactoShowC.setText("NOMBRE CONTACTO COMPAÑIA:");
        lblNombreContactoShowC.setFocusable(false);
        lblNombreContactoShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNombreContactoShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblNombreContactoShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormShowC.add(lblNombreContactoShowC, gridBagConstraints);

        lblNitShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNitShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblNitShowC.setText("NIT:");
        lblNitShowC.setFocusable(false);
        lblNitShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNitShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblNitShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 100, 0, 0);
        pnlFormShowC.add(lblNitShowC, gridBagConstraints);

        lblRepLegalShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRepLegalShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblRepLegalShowC.setText("REP LEGAL:");
        lblRepLegalShowC.setFocusable(false);
        lblRepLegalShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRepLegalShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblRepLegalShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormShowC.add(lblRepLegalShowC, gridBagConstraints);

        txtNombreContactoShowC.setEditable(false);
        txtNombreContactoShowC.setColumns(2);
        txtNombreContactoShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombreContactoShowC.setText("vkvlubvylbg");
        txtNombreContactoShowC.setToolTipText("");
        txtNombreContactoShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNombreContactoShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNombreContactoShowC.setOpaque(false);
        txtNombreContactoShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombreContactoShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormShowC.add(txtNombreContactoShowC, gridBagConstraints);

        txtCorreoContactoShowC.setEditable(false);
        txtCorreoContactoShowC.setColumns(2);
        txtCorreoContactoShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCorreoContactoShowC.setToolTipText("");
        txtCorreoContactoShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCorreoContactoShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCorreoContactoShowC.setOpaque(false);
        txtCorreoContactoShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCorreoContactoShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormShowC.add(txtCorreoContactoShowC, gridBagConstraints);

        lblContraseñaMuniPaShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblContraseñaMuniPaShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblContraseñaMuniPaShowC.setText("CONTRASEÑA DEL MUNI DE PA:");
        lblContraseñaMuniPaShowC.setFocusable(false);
        lblContraseñaMuniPaShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblContraseñaMuniPaShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblContraseñaMuniPaShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 100, 0, 0);
        pnlFormShowC.add(lblContraseñaMuniPaShowC, gridBagConstraints);

        txtRepLegalShowC.setEditable(false);
        txtRepLegalShowC.setColumns(2);
        txtRepLegalShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRepLegalShowC.setText("sadawfgagwshg<h");
        txtRepLegalShowC.setToolTipText("");
        txtRepLegalShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRepLegalShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRepLegalShowC.setOpaque(false);
        txtRepLegalShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRepLegalShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormShowC.add(txtRepLegalShowC, gridBagConstraints);

        lblCedulaRepShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCedulaRepShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblCedulaRepShowC.setText("CEDULA REP LEGAL:");
        lblCedulaRepShowC.setFocusable(false);
        lblCedulaRepShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCedulaRepShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblCedulaRepShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 100, 0, 0);
        pnlFormShowC.add(lblCedulaRepShowC, gridBagConstraints);

        txtRucShowC.setEditable(false);
        txtRucShowC.setColumns(2);
        txtRucShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRucShowC.setText("adawdwad");
        txtRucShowC.setToolTipText("");
        txtRucShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRucShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRucShowC.setOpaque(false);
        txtRucShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRucShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlFormShowC.add(txtRucShowC, gridBagConstraints);

        txtCedulaRepLegalShowC.setEditable(false);
        txtCedulaRepLegalShowC.setColumns(2);
        txtCedulaRepLegalShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCedulaRepLegalShowC.setText("dwawgfagagwa");
        txtCedulaRepLegalShowC.setToolTipText("");
        txtCedulaRepLegalShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCedulaRepLegalShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCedulaRepLegalShowC.setOpaque(false);
        txtCedulaRepLegalShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCedulaRepLegalShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 100, 0, 0);
        pnlFormShowC.add(txtCedulaRepLegalShowC, gridBagConstraints);

        lblNumeroContactoShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNumeroContactoShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblNumeroContactoShowC.setText("NUMERO CONTACTO COMPAÑIA:");
        lblNumeroContactoShowC.setFocusable(false);
        lblNumeroContactoShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNumeroContactoShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblNumeroContactoShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormShowC.add(lblNumeroContactoShowC, gridBagConstraints);

        lblCorreoShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCorreoShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblCorreoShowC.setText("CORREO CONTACTO COMPAÑIA:");
        lblCorreoShowC.setFocusable(false);
        lblCorreoShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCorreoShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlFormShowC.add(lblCorreoShowC, gridBagConstraints);

        txtNumeroContactoShowC.setEditable(false);
        txtNumeroContactoShowC.setColumns(2);
        txtNumeroContactoShowC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNumeroContactoShowC.setToolTipText("");
        txtNumeroContactoShowC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNumeroContactoShowC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNumeroContactoShowC.setOpaque(false);
        txtNumeroContactoShowC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNumeroContactoShowC.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 20, 0);
        pnlFormShowC.add(txtNumeroContactoShowC, gridBagConstraints);

        lblNoContribuyenteShowC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNoContribuyenteShowC.setForeground(new java.awt.Color(102, 102, 102));
        lblNoContribuyenteShowC.setText("NO CONTRIBUYENTE DEL MUNI DE PA:");
        lblNoContribuyenteShowC.setFocusable(false);
        lblNoContribuyenteShowC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNoContribuyenteShowC.setMinimumSize(new java.awt.Dimension(222, 19));
        lblNoContribuyenteShowC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 100, 0, 207);
        pnlFormShowC.add(lblNoContribuyenteShowC, gridBagConstraints);

        lblInformacionShowC.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblInformacionShowC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Contacts_28px_2.png"))); // NOI18N
        lblInformacionShowC.setText("Informacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        pnlFormShowC.add(lblInformacionShowC, gridBagConstraints);

        lblIDCustomers.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lblIDCustomers.setForeground(new java.awt.Color(164, 164, 164));
        lblIDCustomers.setText("IDCustomers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        pnlFormShowC.add(lblIDCustomers, gridBagConstraints);

        pnlBoxShowC.add(pnlFormShowC);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlJointShowC.add(pnlBoxShowC, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 2.0;
        pnlShowCustomer.add(pnlJointShowC, gridBagConstraints);

        pnlEditCustomer.setBackground(new java.awt.Color(255, 255, 255));
        pnlEditCustomer.setMinimumSize(new java.awt.Dimension(612, 398));
        pnlEditCustomer.setPreferredSize(new java.awt.Dimension(604, 443));
        pnlEditCustomer.setLayout(new java.awt.GridBagLayout());

        txtContraseñaEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtContraseñaEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtContraseñaEditC.setToolTipText("");
        txtContraseñaEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtContraseñaEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtContraseñaEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtContraseñaEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtContraseñaEditC.setNextFocusableComponent(txtNombreEditC);
        txtContraseñaEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtContraseñaEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtContraseñaEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 20, 0);
        pnlEditCustomer.add(txtContraseñaEditC, gridBagConstraints);

        lblRucEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRucEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblRucEditC.setText("RUC:");
        lblRucEditC.setFocusable(false);
        lblRucEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRucEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblRucEditC, gridBagConstraints);

        txtNoContribuyenteEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtNoContribuyenteEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNoContribuyenteEditC.setToolTipText("");
        txtNoContribuyenteEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNoContribuyenteEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNoContribuyenteEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNoContribuyenteEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNoContribuyenteEditC.setNextFocusableComponent(txtContraseñaEditC);
        txtNoContribuyenteEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNoContribuyenteEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNoContribuyenteEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtNoContribuyenteEditC, gridBagConstraints);

        txtNitEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtNitEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNitEditC.setToolTipText("");
        txtNitEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNitEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNitEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNitEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNitEditC.setNextFocusableComponent(txtNoContribuyenteEditC);
        txtNitEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNitEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNitEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtNitEditC, gridBagConstraints);

        lblNombreContactoEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNombreContactoEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblNombreContactoEditC.setText("NOMBRE CONTACTO COMPAÑIA:");
        lblNombreContactoEditC.setFocusable(false);
        lblNombreContactoEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNombreContactoEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblNombreContactoEditC, gridBagConstraints);

        lblNitEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNitEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblNitEditC.setText("NIT:");
        lblNitEditC.setFocusable(false);
        lblNitEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNitEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblNitEditC, gridBagConstraints);

        lblRepLegalEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblRepLegalEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblRepLegalEditC.setText("REP LEGAL:");
        lblRepLegalEditC.setFocusable(false);
        lblRepLegalEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblRepLegalEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblRepLegalEditC, gridBagConstraints);

        txtNombreContactoEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtNombreContactoEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombreContactoEditC.setToolTipText("");
        txtNombreContactoEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNombreContactoEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNombreContactoEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNombreContactoEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNombreContactoEditC.setNextFocusableComponent(txtCorreoEditC);
        txtNombreContactoEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNombreContactoEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombreContactoEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtNombreContactoEditC, gridBagConstraints);

        txtCorreoEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtCorreoEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCorreoEditC.setToolTipText("");
        txtCorreoEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCorreoEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtCorreoEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCorreoEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtCorreoEditC.setNextFocusableComponent(txtNumeroContactoEditC);
        txtCorreoEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtCorreoEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCorreoEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 20, 0);
        pnlEditCustomer.add(txtCorreoEditC, gridBagConstraints);

        lblContraseñaMuniPaEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblContraseñaMuniPaEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblContraseñaMuniPaEditC.setText("CONTRASEÑA DEL MUNI DE PA:");
        lblContraseñaMuniPaEditC.setFocusable(false);
        lblContraseñaMuniPaEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblContraseñaMuniPaEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblContraseñaMuniPaEditC, gridBagConstraints);

        txtRepLegalEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtRepLegalEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRepLegalEditC.setToolTipText("");
        txtRepLegalEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRepLegalEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtRepLegalEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRepLegalEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtRepLegalEditC.setNextFocusableComponent(txtNombreContactoEditC);
        txtRepLegalEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtRepLegalEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRepLegalEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtRepLegalEditC, gridBagConstraints);

        lblCedulaRepEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCedulaRepEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblCedulaRepEditC.setText("CEDULA REP LEGAL:");
        lblCedulaRepEditC.setFocusable(false);
        lblCedulaRepEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCedulaRepEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblCedulaRepEditC, gridBagConstraints);

        txtRucEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtRucEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRucEditC.setToolTipText("");
        txtRucEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtRucEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtRucEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRucEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtRucEditC.setNextFocusableComponent(txtRepLegalEditC);
        txtRucEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtRucEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtRucEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtRucEditC, gridBagConstraints);

        txtCedulaRepEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtCedulaRepEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCedulaRepEditC.setToolTipText("");
        txtCedulaRepEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtCedulaRepEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtCedulaRepEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtCedulaRepEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtCedulaRepEditC.setNextFocusableComponent(txtNitEditC);
        txtCedulaRepEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtCedulaRepEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCedulaRepEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtCedulaRepEditC, gridBagConstraints);

        lblNumeroContactoEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNumeroContactoEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblNumeroContactoEditC.setText("NUMERO CONTACTO COMPAÑIA:");
        lblNumeroContactoEditC.setFocusable(false);
        lblNumeroContactoEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNumeroContactoEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 50, 0, 0);
        pnlEditCustomer.add(lblNumeroContactoEditC, gridBagConstraints);

        lblCorreoEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblCorreoEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblCorreoEditC.setText("CORREO CONTACTO COMPAÑIA:");
        lblCorreoEditC.setFocusable(false);
        lblCorreoEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblCorreoEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblCorreoEditC, gridBagConstraints);

        txtNumeroContactoEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtNumeroContactoEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNumeroContactoEditC.setToolTipText("");
        txtNumeroContactoEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNumeroContactoEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNumeroContactoEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNumeroContactoEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNumeroContactoEditC.setNextFocusableComponent(txtCedulaRepEditC);
        txtNumeroContactoEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNumeroContactoEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNumeroContactoEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtNumeroContactoEditC, gridBagConstraints);

        lblNoContribuyente.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNoContribuyente.setForeground(new java.awt.Color(102, 102, 102));
        lblNoContribuyente.setText("NO CONTRIBUYENTE DEL MUNI DE PA:");
        lblNoContribuyente.setFocusable(false);
        lblNoContribuyente.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNoContribuyente.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblNoContribuyente, gridBagConstraints);

        lblEditarInformacionEditC.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblEditarInformacionEditC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Edit_28px_1.png"))); // NOI18N
        lblEditarInformacionEditC.setText("Editar informacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 0, 0);
        pnlEditCustomer.add(lblEditarInformacionEditC, gridBagConstraints);

        txtNombreEditC.setBackground(new java.awt.Color(223, 227, 230));
        txtNombreEditC.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombreEditC.setToolTipText("");
        txtNombreEditC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        txtNombreEditC.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNombreEditC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtNombreEditC.setMinimumSize(new java.awt.Dimension(0, 20));
        txtNombreEditC.setNextFocusableComponent(txtRucEditC);
        txtNombreEditC.setPreferredSize(new java.awt.Dimension(0, 30));
        txtNombreEditC.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombreEditC.setSelectionColor(new java.awt.Color(223, 227, 230));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 300;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 50, 0, 0);
        pnlEditCustomer.add(txtNombreEditC, gridBagConstraints);

        lblNombreEditC.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblNombreEditC.setForeground(new java.awt.Color(102, 102, 102));
        lblNombreEditC.setText("NOMBRE:");
        lblNombreEditC.setFocusable(false);
        lblNombreEditC.setMaximumSize(new java.awt.Dimension(300, 20));
        lblNombreEditC.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 50, 0, 0);
        pnlEditCustomer.add(lblNombreEditC, gridBagConstraints);

        jPopOptions.setText("Opciones");

        jPopMenuItemEliminar.setText("Eliminar este PDF");
        jPopMenuItemEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPopMenuItemEliminarActionPerformed(evt);
            }
        });
        jPopOptions.add(jPopMenuItemEliminar);

        jPopMenuBtn.add(jPopOptions);

        pnlAvisoDeOperaciones.setBackground(new java.awt.Color(255, 255, 255));
        pnlAvisoDeOperaciones.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlAvisoDeOperaciones.setPreferredSize(new java.awt.Dimension(725, 418));
        pnlAvisoDeOperaciones.setLayout(new java.awt.GridBagLayout());

        lblAvisosDeOperacionesHelp.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblAvisosDeOperacionesHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Help_28px.png"))); // NOI18N
        lblAvisosDeOperacionesHelp.setText("Ayuda");
        lblAvisosDeOperacionesHelp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAvisosDeOperacionesHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAvisosDeOperacionesHelpMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 25);
        pnlAvisoDeOperaciones.add(lblAvisosDeOperacionesHelp, gridBagConstraints);

        pnlAvisoDeOperacionesHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlAvisoDeOperacionesHeader.setLayout(new java.awt.GridBagLayout());

        lblSeleccionarPDF.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblSeleccionarPDF.setText("Seleccionar PDF:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlAvisoDeOperacionesHeader.add(lblSeleccionarPDF, gridBagConstraints);

        txtNombrePdf.setCaretColor(new java.awt.Color(211, 0, 3));
        txtNombrePdf.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtNombrePdf.setMinimumSize(new java.awt.Dimension(250, 25));
        txtNombrePdf.setPreferredSize(new java.awt.Dimension(250, 25));
        txtNombrePdf.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtNombrePdf.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 20);
        pnlAvisoDeOperacionesHeader.add(txtNombrePdf, gridBagConstraints);

        btnSeleccionarPdf.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnSeleccionarPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_PDF_28px.png"))); // NOI18N
        btnSeleccionarPdf.setText("Elegir PDF");
        btnSeleccionarPdf.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSeleccionarPdf.setBorderPainted(false);
        btnSeleccionarPdf.setContentAreaFilled(false);
        btnSeleccionarPdf.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSeleccionarPdf.setFocusPainted(false);
        btnSeleccionarPdf.setFocusable(false);
        btnSeleccionarPdf.setMaximumSize(new java.awt.Dimension(107, 25));
        btnSeleccionarPdf.setMinimumSize(new java.awt.Dimension(107, 25));
        btnSeleccionarPdf.setPreferredSize(new java.awt.Dimension(107, 25));
        btnSeleccionarPdf.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_PDF_22px.png"))); // NOI18N
        btnSeleccionarPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarPdfActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        pnlAvisoDeOperacionesHeader.add(btnSeleccionarPdf, gridBagConstraints);

        lblNombreDelPDF.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblNombreDelPDF.setText("Nombre del PDF:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlAvisoDeOperacionesHeader.add(lblNombreDelPDF, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlAvisoDeOperaciones.add(pnlAvisoDeOperacionesHeader, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        scrollPanePdf.setBorder(null);
        scrollPanePdf.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlBandejaPdf.setBackground(new java.awt.Color(255, 255, 255));
        pnlBandejaPdf.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlBandejaPdf.setFocusable(false);
        pnlBandejaPdf.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        pnlBandejaPdf.setLayout(new java.awt.GridBagLayout());
        scrollPanePdf.setViewportView(pnlBandejaPdf);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(40, 80, 40, 80);
        jPanel1.add(scrollPanePdf, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAvisoDeOperaciones.add(jPanel1, gridBagConstraints);

        lblAvisosDeOperaciones.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblAvisosDeOperaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Document_28px.png"))); // NOI18N
        lblAvisosDeOperaciones.setText("Avisos de operaciones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 25, 0);
        pnlAvisoDeOperaciones.add(lblAvisosDeOperaciones, gridBagConstraints);

        pnlObligacionesFiscales.setBackground(new java.awt.Color(255, 255, 255));
        pnlObligacionesFiscales.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlObligacionesFiscales.setPreferredSize(new java.awt.Dimension(725, 418));
        pnlObligacionesFiscales.setLayout(new java.awt.GridBagLayout());

        lblObligacionesFiscales.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblObligacionesFiscales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Calendar_28px.png"))); // NOI18N
        lblObligacionesFiscales.setText("Obligaciones fiscales");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 50, 25, 0);
        pnlObligacionesFiscales.add(lblObligacionesFiscales, gridBagConstraints);

        pnlObligacionesFiscalesHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlObligacionesFiscalesHeader.setMinimumSize(new java.awt.Dimension(712, 60));
        pnlObligacionesFiscalesHeader.setPreferredSize(new java.awt.Dimension(712, 60));
        pnlObligacionesFiscalesHeader.setLayout(new java.awt.GridBagLayout());

        lblAgregarObligacion.setText("Agregar obligacion:");
        lblAgregarObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlObligacionesFiscalesHeader.add(lblAgregarObligacion, gridBagConstraints);

        txtObligacion.setCaretColor(new java.awt.Color(211, 0, 3));
        txtObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtObligacion.setMinimumSize(new java.awt.Dimension(150, 25));
        txtObligacion.setPreferredSize(new java.awt.Dimension(150, 25));
        txtObligacion.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtObligacion.setSelectionColor(new java.awt.Color(255, 255, 255));
        txtObligacion.setToolTipText("Obligacion hacia el cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 20);
        pnlObligacionesFiscalesHeader.add(txtObligacion, gridBagConstraints);

        lblFecha.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblFecha.setText("Fecha:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlObligacionesFiscalesHeader.add(lblFecha, gridBagConstraints);

        datePickerNewObligation.setMinimumSize(new java.awt.Dimension(170, 25));
        datePickerNewObligation.setPreferredSize(new java.awt.Dimension(170, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlObligacionesFiscalesHeader.add(datePickerNewObligation, gridBagConstraints);

        timePickerNewObligation.setMinimumSize(new java.awt.Dimension(170, 25));
        timePickerNewObligation.setPreferredSize(new java.awt.Dimension(170, 25));
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.use24HourClockFormat();
        timeSettings.setFormatForDisplayTime("HH:mm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 190, 0, 0);
        pnlObligacionesFiscalesHeader.add(timePickerNewObligation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlObligacionesFiscales.add(pnlObligacionesFiscalesHeader, gridBagConstraints);

        lblObligacionesFiscalesHelper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Help_28px.png"))); // NOI18N
        lblObligacionesFiscalesHelper.setText("Ayuda");
        lblObligacionesFiscalesHelper.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblObligacionesFiscalesHelper.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblObligacionesFiscalesHelper.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblObligacionesFiscalesHelperMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 25);
        pnlObligacionesFiscales.add(lblObligacionesFiscalesHelper, gridBagConstraints);

        lblConfigEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Settings_28px.png"))); // NOI18N
        lblConfigEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblConfigEmail.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblConfigEmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblConfigEmailMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlObligacionesFiscales.add(lblConfigEmail, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        scrollPaneObligaciones.getViewport().setBackground(Color.white);
        scrollPaneObligaciones.setBorder(null);
        scrollPaneObligaciones.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlBandejaObligaciones.setBackground(new java.awt.Color(255, 255, 255));
        pnlBandejaObligaciones.setLayout(new java.awt.GridBagLayout());
        scrollPaneObligaciones.setViewportView(pnlBandejaObligaciones);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(40, 80, 40, 80);
        jPanel2.add(scrollPaneObligaciones, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlObligacionesFiscales.add(jPanel2, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Gmail_28px_1.png"))); // NOI18N
        jLabel1.setText("Establecer Gmail: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 46);
        pnlObligacionesFiscales.add(jLabel1, gridBagConstraints);

        dialogObligacion.setTitle("Obligacion");
        dialogObligacion.setFocusable(false);
        dialogObligacion.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dialogObligacion.setMinimumSize(new java.awt.Dimension(560, 310));
        dialogObligacion.setUndecorated(true);
        dialogObligacion.setResizable(false);
        dialogObligacion.setSize(new java.awt.Dimension(0, 0));
        dialogObligacion.getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlBoxDialogObligacion.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlBoxDialogObligacion.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlBoxDialogObligacion.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlBoxDialogObligacion.setLayout(new javax.swing.BoxLayout(pnlBoxDialogObligacion, javax.swing.BoxLayout.LINE_AXIS));

        pnlDialogShowObligacion.setFocusable(false);
        pnlDialogShowObligacion.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlDialogShowObligacion.setLayout(new java.awt.GridBagLayout());

        pnlTopObligacionPopup.setBackground(new java.awt.Color(211, 0, 3));
        pnlTopObligacionPopup.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlTopObligacionPopup.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlTopObligacionPopup.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlTopObligacionPopup.setLayout(new java.awt.GridBagLayout());

        lblInformacionDeLaObligacion.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblInformacionDeLaObligacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInformacionDeLaObligacion.setText("Informacion de la Obligacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlTopObligacionPopup.add(lblInformacionDeLaObligacion, gridBagConstraints);

        btnCloseDialog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_28px.png"))); // NOI18N
        btnCloseDialog.setBackground(new java.awt.Color(255, 255, 255));
        btnCloseDialog.setBorder(null);
        btnCloseDialog.setBorderPainted(false);
        btnCloseDialog.setContentAreaFilled(false);
        btnCloseDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloseDialog.setFocusPainted(false);
        btnCloseDialog.setFocusable(false);
        btnCloseDialog.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCloseDialog.setForeground(new java.awt.Color(255, 255, 255));
        btnCloseDialog.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_22px_1.png"))); // NOI18N
        btnCloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseDialogActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlTopObligacionPopup.add(btnCloseDialog, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 560;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogShowObligacion.add(pnlTopObligacionPopup, gridBagConstraints);

        pnlEmergenteBox.setLayout(new javax.swing.BoxLayout(pnlEmergenteBox, javax.swing.BoxLayout.LINE_AXIS));

        pnlEmergenteInfo.setBackground(new java.awt.Color(255, 255, 255));
        pnlEmergenteInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlEmergenteInfo.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblShowHora.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Alarm_Clock_28px.png"))); // NOI18N
        lblShowHora.setText("Hora");
        lblShowHora.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblShowHora.setForeground(new java.awt.Color(13, 193, 67));
        pnlEmergenteInfo.add(lblShowHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 540, 30));

        pnlTxtShowObligacion.setEditable(false);
        pnlTxtShowObligacion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        pnlTxtShowObligacion.setCaretColor(new java.awt.Color(211, 0, 3));
        pnlTxtShowObligacion.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        pnlTxtShowObligacion.setMargin(new java.awt.Insets(5, 5, 5, 5));
        pnlTxtShowObligacion.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        pnlTxtShowObligacion.setSelectionColor(new java.awt.Color(255, 255, 255));
        pnlTxtShowObligacion.setToolTipText("");
        scrollPaneDialog.setViewportView(pnlTxtShowObligacion);
        scrollPaneDialog.setBorder(null);
        StyledDocument doc1 = pnlTxtShowObligacion.getStyledDocument();
        SimpleAttributeSet center1 = new SimpleAttributeSet();
        StyleConstants.setAlignment(center1, StyleConstants.ALIGN_CENTER);
        doc1.setParagraphAttributes(0, doc1.getLength(), center1, false);

        pnlEmergenteInfo.add(scrollPaneDialog, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 540, 60));

        lblShowFecha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Calendar_28px.png"))); // NOI18N
        lblShowFecha.setText("Fecha");
        lblShowFecha.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblShowFecha.setForeground(new java.awt.Color(13, 193, 67));
        pnlEmergenteInfo.add(lblShowFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 540, 30));

        btnObligacionCompletada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Checkmark_28px.png"))); // NOI18N
        btnObligacionCompletada.setText("Completada");
        btnObligacionCompletada.setBackground(new java.awt.Color(255, 255, 255));
        btnObligacionCompletada.setBorderPainted(false);
        btnObligacionCompletada.setContentAreaFilled(false);
        btnObligacionCompletada.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnObligacionCompletada.setFocusPainted(false);
        btnObligacionCompletada.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnObligacionCompletada.setForeground(new java.awt.Color(13, 193, 67));
        btnObligacionCompletada.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Checkmark_22px.png"))); // NOI18N
        btnObligacionCompletada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObligacionCompletadaActionPerformed(evt);
            }
        });
        pnlEmergenteInfo.add(btnObligacionCompletada, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 220, -1, -1));

        btnEditarObligacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Edit_28px_1.png"))); // NOI18N
        btnEditarObligacion.setText("Editar");
        btnEditarObligacion.setBackground(new java.awt.Color(255, 255, 255));
        btnEditarObligacion.setBorderPainted(false);
        btnEditarObligacion.setContentAreaFilled(false);
        btnEditarObligacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditarObligacion.setFocusPainted(false);
        btnEditarObligacion.setFocusable(false);
        btnEditarObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnEditarObligacion.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Edit_22px_1.png"))); // NOI18N
        btnEditarObligacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarObligacionActionPerformed(evt);
            }
        });
        pnlEmergenteInfo.add(btnEditarObligacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 220, -1, -1));

        lblIDObligacion.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lblIDObligacion.setForeground(new java.awt.Color(164, 164, 164));
        lblIDObligacion.setText("IDObligacion");
        pnlEmergenteInfo.add(lblIDObligacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, -1));

        pnlEmergenteBox.add(pnlEmergenteInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogShowObligacion.add(pnlEmergenteBox, gridBagConstraints);

        pnlBoxDialogObligacion.add(pnlDialogShowObligacion);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        dialogObligacion.getContentPane().add(pnlBoxDialogObligacion, gridBagConstraints);

        pnlDialogObligacionEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlDialogObligacionEditar.setFocusable(false);
        pnlDialogObligacionEditar.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlDialogObligacionEditar.setLayout(new java.awt.GridBagLayout());

        pnlObligacionEditarHeader.setBackground(new java.awt.Color(211, 0, 3));
        pnlObligacionEditarHeader.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlObligacionEditarHeader.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlObligacionEditarHeader.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlObligacionEditarHeader.setLayout(new java.awt.GridBagLayout());

        lblInformacionDeLaObligacionEditar.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblInformacionDeLaObligacionEditar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInformacionDeLaObligacionEditar.setText("Informacion de la Obligacion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlObligacionEditarHeader.add(lblInformacionDeLaObligacionEditar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 560;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogObligacionEditar.add(pnlObligacionEditarHeader, gridBagConstraints);

        pnlEmergenteBox1.setLayout(new javax.swing.BoxLayout(pnlEmergenteBox1, javax.swing.BoxLayout.LINE_AXIS));

        pnlEmergenteInfo1.setBackground(new java.awt.Color(255, 255, 255));
        pnlEmergenteInfo1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlEmergenteInfo1.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo1.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo1.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlEmergenteInfo1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHoraEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Alarm_Clock_28px.png"))); // NOI18N
        lblHoraEditar.setText("Hora:");
        lblHoraEditar.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        pnlEmergenteInfo1.add(lblHoraEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 90, 80, 30));

        pnlTxtShowObligacionEditar.setBorder(null);
        pnlTxtShowObligacionEditar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        pnlTxtShowObligacionEditar.setCaretColor(new java.awt.Color(211, 0, 3));
        pnlTxtShowObligacionEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        pnlTxtShowObligacionEditar.setMargin(new java.awt.Insets(5, 5, 5, 5));
        pnlTxtShowObligacionEditar.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        pnlTxtShowObligacionEditar.setSelectionColor(new java.awt.Color(255, 255, 255));
        scrollPaneObligaciontEdit.setViewportView(pnlTxtShowObligacionEditar);
        StyledDocument doc2 = pnlTxtShowObligacionEditar.getStyledDocument();
        SimpleAttributeSet center2 = new SimpleAttributeSet();
        StyleConstants.setAlignment(center2, StyleConstants.ALIGN_CENTER);
        doc2.setParagraphAttributes(0, doc2.getLength(), center2, false);

        pnlEmergenteInfo1.add(scrollPaneObligaciontEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 540, 60));

        lblEditFecha.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblEditFecha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Calendar_28px.png"))); // NOI18N
        lblEditFecha.setText("Fecha:");
        pnlEmergenteInfo1.add(lblEditFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 90, 30));

        btnGuardarCambioObligacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_28px.png"))); // NOI18N
        btnGuardarCambioObligacion.setText("Guardar Cambios");
        btnGuardarCambioObligacion.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardarCambioObligacion.setBorder(null);
        btnGuardarCambioObligacion.setBorderPainted(false);
        btnGuardarCambioObligacion.setContentAreaFilled(false);
        btnGuardarCambioObligacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarCambioObligacion.setFocusPainted(false);
        btnGuardarCambioObligacion.setFocusable(false);
        btnGuardarCambioObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnGuardarCambioObligacion.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Save_22px_1.png"))); // NOI18N
        btnGuardarCambioObligacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCambioObligacionActionPerformed(evt);
            }
        });
        pnlEmergenteInfo1.add(btnGuardarCambioObligacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 220, -1, -1));

        btnCancelarEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_28px.png"))); // NOI18N
        btnCancelarEditar.setText("Cancelar");
        btnCancelarEditar.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelarEditar.setBorder(null);
        btnCancelarEditar.setBorderPainted(false);
        btnCancelarEditar.setContentAreaFilled(false);
        btnCancelarEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelarEditar.setFocusPainted(false);
        btnCancelarEditar.setFocusable(false);
        btnCancelarEditar.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCancelarEditar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_22px.png"))); // NOI18N
        btnCancelarEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEditarActionPerformed(evt);
            }
        });
        pnlEmergenteInfo1.add(btnCancelarEditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 220, -1, -1));

        editFechaPicker.setPreferredSize(new java.awt.Dimension(140, 25));
        pnlEmergenteInfo1.add(editFechaPicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 170, -1));

        TimePickerSettings timeSettings2 = new TimePickerSettings();
        timeSettings2.use24HourClockFormat();
        timeSettings2.setFormatForDisplayTime("HH:mm");
        editHoraPicker.setMinimumSize(new java.awt.Dimension(140, 25));
        editHoraPicker.setPreferredSize(new java.awt.Dimension(140, 25));
        pnlEmergenteInfo1.add(editHoraPicker, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 170, -1));

        pnlEmergenteBox1.add(pnlEmergenteInfo1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogObligacionEditar.add(pnlEmergenteBox1, gridBagConstraints);

        lblID.setText("jLabel5");

        dialogAboutMe.setTitle("Obligacion");
        dialogAboutMe.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        dialogAboutMe.setFocusable(false);
        dialogAboutMe.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dialogAboutMe.setMinimumSize(new java.awt.Dimension(560, 310));
        dialogAboutMe.setUndecorated(true);
        dialogAboutMe.setResizable(false);
        dialogAboutMe.setSize(new java.awt.Dimension(560, 310));
        dialogAboutMe.getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlContainerAboutMe.setFocusable(false);
        pnlContainerAboutMe.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerAboutMe.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlAboutMeHeader.setBackground(new java.awt.Color(211, 0, 3));
        pnlAboutMeHeader.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlAboutMeHeader.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlAboutMeHeader.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlAboutMeHeader.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlAboutMeHeader.setLayout(new java.awt.GridBagLayout());

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Laptop_32px.png"))); // NOI18N
        jLabel5.setText("Acerca del programa");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlAboutMeHeader.add(jLabel5, gridBagConstraints);

        btnCloseAboutMe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_28px.png"))); // NOI18N
        btnCloseAboutMe.setBackground(new java.awt.Color(255, 255, 255));
        btnCloseAboutMe.setBorderPainted(false);
        btnCloseAboutMe.setContentAreaFilled(false);
        btnCloseAboutMe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloseAboutMe.setFocusPainted(false);
        btnCloseAboutMe.setFocusable(false);
        btnCloseAboutMe.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCloseAboutMe.setForeground(new java.awt.Color(255, 255, 255));
        btnCloseAboutMe.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_22px_1.png"))); // NOI18N
        btnCloseAboutMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseAboutMeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlAboutMeHeader.add(btnCloseAboutMe, gridBagConstraints);

        pnlContainerAboutMe.add(pnlAboutMeHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 40));

        pnlAboutMeComponents.setBackground(new java.awt.Color(47, 43, 45));
        pnlAboutMeComponents.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlAboutMeComponents.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlAboutMeComponents.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlAboutMeComponents.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlAboutMeComponents.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblShowWhatsapp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_WhatsApp_28px.png"))); // NOI18N
        lblShowWhatsapp.setText("<html><FONT COLOR=#40C351>Whatsapp: <br></FONT><FONT COLOR=#FFFFFF>+50762545505</FONT></html>");
        lblShowWhatsapp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblShowWhatsapp.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblShowWhatsapp.setForeground(new java.awt.Color(255, 255, 255));
        lblShowWhatsapp.setToolTipText("Click para copiar la informacion");
        lblShowWhatsapp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowWhatsappMouseClicked(evt);
            }
        });
        pnlAboutMeComponents.add(lblShowWhatsapp, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 230, 30));

        lblProgramador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Workstation_28px.png"))); // NOI18N
        lblProgramador.setText("<html><FONT COLOR=#3F51B5>Programador Junior: <br></FONT><FONT COLOR=#BBDEFB>Juan Bedoya (Extibax)</FONT></html>");
        lblProgramador.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblProgramador.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblProgramador.setForeground(new java.awt.Color(255, 255, 255));
        pnlAboutMeComponents.add(lblProgramador, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 230, 40));

        lblMiCorreo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Gmail_28px.png"))); // NOI18N
        lblMiCorreo.setText("<html><FONT COLOR=#E95652>Correo:: <br></FONT><FONT COLOR=#F5F5F5>extibax@gmail.com</FONT></html>");
        lblMiCorreo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblMiCorreo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblMiCorreo.setForeground(new java.awt.Color(255, 255, 255));
        lblMiCorreo.setToolTipText("Click para copiar la informacion");
        lblMiCorreo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMiCorreoMouseClicked(evt);
            }
        });
        pnlAboutMeComponents.add(lblMiCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 230, 40));

        lblCreadoEl.setText("Creado el: 26/9/2018");
        lblCreadoEl.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblCreadoEl.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCreadoEl.setForeground(new java.awt.Color(255, 255, 255));
        pnlAboutMeComponents.add(lblCreadoEl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 140, 30));

        lblFinalizadoEl.setText("Finalizado el: 27/10/2018");
        lblFinalizadoEl.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblFinalizadoEl.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblFinalizadoEl.setForeground(new java.awt.Color(255, 255, 255));
        pnlAboutMeComponents.add(lblFinalizadoEl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 170, 30));

        lblCustomersManager.setText("Administrador de clientes 1.1");
        lblCustomersManager.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblCustomersManager.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCustomersManager.setForeground(new java.awt.Color(255, 255, 255));
        pnlAboutMeComponents.add(lblCustomersManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 210, 30));

        gif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/tenor.gif"))); // NOI18N
        gif.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlAboutMeComponents.add(gif, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, 300, 210));

        pnlContainerAboutMe.add(pnlAboutMeComponents, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, -1, 270));

        dialogAboutMe.getContentPane().add(pnlContainerAboutMe, new java.awt.GridBagConstraints());

        dialogAvisoDeOperacionesHelp.setTitle("Obligacion");
        dialogAvisoDeOperacionesHelp.setFocusable(false);
        dialogAvisoDeOperacionesHelp.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dialogAvisoDeOperacionesHelp.setUndecorated(true);
        dialogAvisoDeOperacionesHelp.setResizable(false);
        dialogAvisoDeOperacionesHelp.setSize(new java.awt.Dimension(560, 310));
        dialogAvisoDeOperacionesHelp.getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlDialogContainerAvisoDeOperaciones.setFocusable(false);
        pnlDialogContainerAvisoDeOperaciones.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlDialogContainerAvisoDeOperaciones.setLayout(new java.awt.GridBagLayout());

        pnlManejoDelAvisoDeOperacionesHeader.setBackground(new java.awt.Color(211, 0, 3));
        pnlManejoDelAvisoDeOperacionesHeader.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlManejoDelAvisoDeOperacionesHeader.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlManejoDelAvisoDeOperacionesHeader.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlManejoDelAvisoDeOperacionesHeader.setLayout(new java.awt.GridBagLayout());

        lblManejoDeOperaciones.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblManejoDeOperaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblManejoDeOperaciones.setText("Manejo del Aviso de Operaciones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlManejoDelAvisoDeOperacionesHeader.add(lblManejoDeOperaciones, gridBagConstraints);

        btnCloseAvisoDeOperacionesHelper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_28px.png"))); // NOI18N
        btnCloseAvisoDeOperacionesHelper.setBackground(new java.awt.Color(255, 255, 255));
        btnCloseAvisoDeOperacionesHelper.setBorderPainted(false);
        btnCloseAvisoDeOperacionesHelper.setContentAreaFilled(false);
        btnCloseAvisoDeOperacionesHelper.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloseAvisoDeOperacionesHelper.setFocusPainted(false);
        btnCloseAvisoDeOperacionesHelper.setFocusable(false);
        btnCloseAvisoDeOperacionesHelper.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCloseAvisoDeOperacionesHelper.setForeground(new java.awt.Color(255, 255, 255));
        btnCloseAvisoDeOperacionesHelper.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_22px_1.png"))); // NOI18N
        btnCloseAvisoDeOperacionesHelper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseAvisoDeOperacionesHelperActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlManejoDelAvisoDeOperacionesHeader.add(btnCloseAvisoDeOperacionesHelper, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 560;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogContainerAvisoDeOperaciones.add(pnlManejoDelAvisoDeOperacionesHeader, gridBagConstraints);

        pnlDialogComponents.setLayout(new javax.swing.BoxLayout(pnlDialogComponents, javax.swing.BoxLayout.LINE_AXIS));

        pnlContainerTextAvisoDeOperacionesHelper.setBackground(new java.awt.Color(255, 255, 255));
        pnlContainerTextAvisoDeOperacionesHelper.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlContainerTextAvisoDeOperacionesHelper.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextAvisoDeOperacionesHelper.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextAvisoDeOperacionesHelper.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlContainerTextAvisoDeOperacionesHelper.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtPaneAvsioHelper.setEditable(false);
        txtPaneAvsioHelper.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtPaneAvsioHelper.setText("Añadir nuevo Aviso de Operaciones:\n\nRellenar la casilla de \"Nombre del PDF\", La cual le dara un sobrenombre para el PDF a su gusto.\nDespues dar click en \"Elegir PDF\", Se mostrara un \"seleccionador de archivos\", En el cual tienes que seleccionar tu Aviso de Operaciones en formato PDF, Despues para\nGuardar el PDF tienes que dar click en el Boton \"Guardar PDF\" que esta en la cabecera de esta seccion, Si no hay un sobrenombre para el PDF no podra guardarlo, Al igual pasara si no seleccionar un PDF.\n\nEliminar Pdf:\n\nPara eliminar cualquier Aviso de Operaciones que tenga es su lista, Tan solo tiene que poner el Mouse sobre el Boton del PDF y dar \"Click DERECHO\", Se le mostrara un menu emergente en que debe dar click en \"Opciones\" y despues en \"Eliminar este PDF\".");
        txtPaneAvsioHelper.setCaretColor(new java.awt.Color(211, 0, 3));
        txtPaneAvsioHelper.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtPaneAvsioHelper.setSelectionColor(new java.awt.Color(255, 255, 255));
        jScrollPane4.setViewportView(txtPaneAvsioHelper);

        pnlContainerTextAvisoDeOperacionesHelper.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 270));

        pnlDialogComponents.add(pnlContainerTextAvisoDeOperacionesHelper);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlDialogContainerAvisoDeOperaciones.add(pnlDialogComponents, gridBagConstraints);

        dialogAvisoDeOperacionesHelp.getContentPane().add(pnlDialogContainerAvisoDeOperaciones, new java.awt.GridBagConstraints());

        dialogObligacionesHelp.setTitle("Obligacion");
        dialogObligacionesHelp.setFocusable(false);
        dialogObligacionesHelp.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dialogObligacionesHelp.setUndecorated(true);
        dialogObligacionesHelp.setResizable(false);
        dialogObligacionesHelp.setSize(new java.awt.Dimension(560, 310));
        dialogObligacionesHelp.getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlContainerObligaciones.setFocusable(false);
        pnlContainerObligaciones.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerObligaciones.setLayout(new java.awt.GridBagLayout());

        pnlObligacionesHeader.setBackground(new java.awt.Color(211, 0, 3));
        pnlObligacionesHeader.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlObligacionesHeader.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlObligacionesHeader.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlObligacionesHeader.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Manejo de Obligaciones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlObligacionesHeader.add(jLabel9, gridBagConstraints);

        btnClosePnlHelperObligaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_28px.png"))); // NOI18N
        btnClosePnlHelperObligaciones.setBackground(new java.awt.Color(255, 255, 255));
        btnClosePnlHelperObligaciones.setBorderPainted(false);
        btnClosePnlHelperObligaciones.setContentAreaFilled(false);
        btnClosePnlHelperObligaciones.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClosePnlHelperObligaciones.setFocusPainted(false);
        btnClosePnlHelperObligaciones.setFocusable(false);
        btnClosePnlHelperObligaciones.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnClosePnlHelperObligaciones.setForeground(new java.awt.Color(255, 255, 255));
        btnClosePnlHelperObligaciones.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_22px_1.png"))); // NOI18N
        btnClosePnlHelperObligaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClosePnlHelperObligacionesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlObligacionesHeader.add(btnClosePnlHelperObligaciones, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 560;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlContainerObligaciones.add(pnlObligacionesHeader, gridBagConstraints);

        pnlObligacionesComponent.setLayout(new javax.swing.BoxLayout(pnlObligacionesComponent, javax.swing.BoxLayout.LINE_AXIS));

        pnlContainerTextOblifgacion.setBackground(new java.awt.Color(255, 255, 255));
        pnlContainerTextOblifgacion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlContainerTextOblifgacion.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtPaneObligacionHelper.setEditable(false);
        txtPaneObligacionHelper.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtPaneObligacionHelper.setText("Añadir nuevo Aviso de Operaciones:\n\nRellenar la casilla \"Agregar Obligacion\" con la tarea a cumplir en un futuro, Depues\nSeleccionas la fecha y la hora en el boton que aparece al lado de \"Fecha\" Ah seleccionaras el dia del mes que sea y la hora\ncon AM o PM despues solo le das al boton \"Guardar Obligacion\" que aparece en el hedear de esta seccion.\n\nEste gestor de obligaciones le avisara con una Notificacion al Escritorio(PC) y tambien le enviara un correo moestrandole \ncon quien es su obligacion y que conlleva a hacer.\n\nEliminar o Marcar como completada la obligacion:\n\nAl crear una obligacion, Aparecera en forma de boton debajo de el formulario de creacion, Ahi podra interacturar con \nla obligacion dandole click, Se le mostrara la Obligacion completa con su fecha y hora especifica,\nPodra eliminarlo o marcarlo como hecho en esa misma ventana emergente, Al dar en \"Completada\" se eliminara la obligacion.");
        txtPaneObligacionHelper.setCaretColor(new java.awt.Color(211, 0, 3));
        txtPaneObligacionHelper.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtPaneObligacionHelper.setSelectionColor(new java.awt.Color(255, 255, 255));
        jScrollPane5.setViewportView(txtPaneObligacionHelper);

        pnlContainerTextOblifgacion.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 270));

        pnlObligacionesComponent.add(pnlContainerTextOblifgacion);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlContainerObligaciones.add(pnlObligacionesComponent, gridBagConstraints);

        dialogObligacionesHelp.getContentPane().add(pnlContainerObligaciones, new java.awt.GridBagConstraints());

        dialogConfigObligaciones.setTitle("Obligacion");
        dialogConfigObligaciones.setFocusable(false);
        dialogConfigObligaciones.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        dialogConfigObligaciones.setUndecorated(true);
        dialogConfigObligaciones.setResizable(false);
        dialogConfigObligaciones.setSize(new java.awt.Dimension(560, 310));
        dialogConfigObligaciones.getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlContainerObligaciones1.setFocusable(false);
        pnlContainerObligaciones1.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerObligaciones1.setLayout(new java.awt.GridBagLayout());

        pnlObligacionesHeader1.setBackground(new java.awt.Color(211, 0, 3));
        pnlObligacionesHeader1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pnlObligacionesHeader1.setMinimumSize(new java.awt.Dimension(0, 32));
        pnlObligacionesHeader1.setPreferredSize(new java.awt.Dimension(0, 32));
        pnlObligacionesHeader1.setLayout(new java.awt.GridBagLayout());

        lblConfiguracionDeObligaciones.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblConfiguracionDeObligaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConfiguracionDeObligaciones.setText("Configuraciones de Obligaciones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlObligacionesHeader1.add(lblConfiguracionDeObligaciones, gridBagConstraints);

        btnCloseYGuardarCambiosObligacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_28px.png"))); // NOI18N
        btnCloseYGuardarCambiosObligacion.setBackground(new java.awt.Color(255, 255, 255));
        btnCloseYGuardarCambiosObligacion.setBorderPainted(false);
        btnCloseYGuardarCambiosObligacion.setContentAreaFilled(false);
        btnCloseYGuardarCambiosObligacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloseYGuardarCambiosObligacion.setFocusPainted(false);
        btnCloseYGuardarCambiosObligacion.setFocusable(false);
        btnCloseYGuardarCambiosObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCloseYGuardarCambiosObligacion.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Ok_22px_1.png"))); // NOI18N
        btnCloseYGuardarCambiosObligacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseYGuardarCambiosObligacionActionPerformed(evt);
            }
        });
        pnlObligacionesHeader1.add(btnCloseYGuardarCambiosObligacion, new java.awt.GridBagConstraints());

        btnCloseYCancelarCambiosObligacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_28px.png"))); // NOI18N
        btnCloseYCancelarCambiosObligacion.setBackground(new java.awt.Color(255, 255, 255));
        btnCloseYCancelarCambiosObligacion.setBorderPainted(false);
        btnCloseYCancelarCambiosObligacion.setContentAreaFilled(false);
        btnCloseYCancelarCambiosObligacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCloseYCancelarCambiosObligacion.setFocusPainted(false);
        btnCloseYCancelarCambiosObligacion.setFocusable(false);
        btnCloseYCancelarCambiosObligacion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnCloseYCancelarCambiosObligacion.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_22px.png"))); // NOI18N
        btnCloseYCancelarCambiosObligacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseYCancelarCambiosObligacionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlObligacionesHeader1.add(btnCloseYCancelarCambiosObligacion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 560;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlContainerObligaciones1.add(pnlObligacionesHeader1, gridBagConstraints);

        pnlObligacionesComponent1.setLayout(new javax.swing.BoxLayout(pnlObligacionesComponent1, javax.swing.BoxLayout.LINE_AXIS));

        pnlContainerTextOblifgacion1.setBackground(new java.awt.Color(255, 255, 255));
        pnlContainerTextOblifgacion1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlContainerTextOblifgacion1.setMaximumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion1.setMinimumSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion1.setPreferredSize(new java.awt.Dimension(560, 310));
        pnlContainerTextOblifgacion1.setLayout(new java.awt.GridBagLayout());

        lblEmailAEnviar.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblEmailAEnviar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Gmail_28px.png"))); // NOI18N
        lblEmailAEnviar.setText("E-mail a enviar:");
        pnlContainerTextOblifgacion1.add(lblEmailAEnviar, new java.awt.GridBagConstraints());

        txtEmailAEnviar.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtEmailAEnviar.setToolTipText("Correo electronico al cual se enviaran las obligaciones.");
        txtEmailAEnviar.setCaretColor(new java.awt.Color(211, 0, 3));
        txtEmailAEnviar.setMinimumSize(new java.awt.Dimension(0, 30));
        txtEmailAEnviar.setPreferredSize(new java.awt.Dimension(0, 30));
        txtEmailAEnviar.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtEmailAEnviar.setSelectionColor(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 300;
        pnlContainerTextOblifgacion1.add(txtEmailAEnviar, gridBagConstraints);

        pnlObligacionesComponent1.add(pnlContainerTextOblifgacion1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = -40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlContainerObligaciones1.add(pnlObligacionesComponent1, gridBagConstraints);

        dialogConfigObligaciones.getContentPane().add(pnlContainerObligaciones1, new java.awt.GridBagConstraints());

        setTitle("EAM Consultores - Clientes");
        setMinimumSize(new java.awt.Dimension(975, 540));
        setPreferredSize(new java.awt.Dimension(975, 540));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pnlFrame.setBackground(new java.awt.Color(102, 137, 100));
        pnlFrame.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        pnlFrame.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlFrame.setPreferredSize(new java.awt.Dimension(1200, 600));
        pnlFrame.setLayout(new java.awt.GridBagLayout());

        pnlBoxFrame.setBackground(new java.awt.Color(0, 102, 153));
        pnlBoxFrame.setOpaque(false);
        pnlBoxFrame.setPreferredSize(new java.awt.Dimension(950, 520));
        pnlBoxFrame.setLayout(new javax.swing.BoxLayout(pnlBoxFrame, javax.swing.BoxLayout.LINE_AXIS));

        pnlStart.setBackground(new java.awt.Color(0, 153, 153));
        pnlStart.setMinimumSize(new java.awt.Dimension(950, 520));
        pnlStart.setOpaque(false);
        pnlStart.setPreferredSize(new java.awt.Dimension(950, 520));
        pnlStart.setLayout(new java.awt.GridBagLayout());

        pnlTopStart.setBackground(new java.awt.Color(211, 0, 3));
        pnlTopStart.setPreferredSize(new java.awt.Dimension(242, 32));

        lblClientManager.setText("EAM Consultores - Clientes");
        lblClientManager.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        pnlTopStart.add(lblClientManager);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        pnlStart.add(pnlTopStart, gridBagConstraints);

        pnlHeader.setBackground(new java.awt.Color(47, 43, 45));
        pnlHeader.setFocusable(false);
        pnlHeader.setMinimumSize(new java.awt.Dimension(273, 28));
        pnlHeader.setPreferredSize(new java.awt.Dimension(273, 28));
        pnlHeader.setLayout(new java.awt.GridBagLayout());

        btnNuevoCliente.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnNuevoCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Add_User_Group_Man_Man_28px.png"))); // NOI18N
        btnNuevoCliente.setText("Nuevo cliente");
        btnNuevoCliente.setContentAreaFilled(false);
        btnNuevoCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoCliente.setFocusPainted(false);
        btnNuevoCliente.setFocusable(false);
        btnNuevoCliente.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-add-user-group-man-man-22.png"))); // NOI18N
        btnNuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoClienteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlHeader.add(btnNuevoCliente, gridBagConstraints);

        lblNameUser.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblNameUser.setForeground(new java.awt.Color(255, 255, 255));
        lblNameUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Briefcase_34px_1.png"))); // NOI18N
        lblNameUser.setText("Esther Avila");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlHeader.add(lblNameUser, gridBagConstraints);

        txtCustomers.setCaretColor(new java.awt.Color(211, 0, 3));
        txtCustomers.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCustomers.setPreferredSize(new java.awt.Dimension(0, 25));
        txtCustomers.setSelectedTextColor(new java.awt.Color(211, 0, 3));
        txtCustomers.setSelectionColor(new java.awt.Color(223, 227, 230));
        txtCustomers.setToolTipText("Buscar cliente");
        txtCustomers.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCustomersKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 200;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        pnlHeader.add(txtCustomers, gridBagConstraints);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Search_34px.png"))); // NOI18N
        btnSearch.setToolTipText("Buscar");
        btnSearch.setBorder(null);
        btnSearch.setBorderPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlHeader.add(btnSearch, gridBagConstraints);

        btnSearchCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Cancel_34px.png"))); // NOI18N
        btnSearchCancel.setToolTipText("Cancelar y restablecer");
        btnSearchCancel.setBorder(null);
        btnSearchCancel.setBorderPainted(false);
        btnSearchCancel.setContentAreaFilled(false);
        btnSearchCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchCancel.setFocusPainted(false);
        btnSearchCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlHeader.add(btnSearchCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlStart.add(pnlHeader, gridBagConstraints);

        pnlContenedorBandeja.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorBandeja.setLayout(new java.awt.GridBagLayout());

        scrollPaneCustomers.getViewport().setBackground(Color.white);
        scrollPaneCustomers.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneCustomers.setBackground(new java.awt.Color(255, 255, 255));
        scrollPaneCustomers.getVerticalScrollBar().setUnitIncrement(16);
        scrollPaneCustomers.setBorder(null);

        pnlBandeja.setBackground(new java.awt.Color(255, 255, 255));
        pnlBandeja.setLayout(new java.awt.GridBagLayout());
        scrollPaneCustomers.setViewportView(pnlBandeja);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 80, 40, 60);
        pnlContenedorBandeja.add(scrollPaneCustomers, gridBagConstraints);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logoPantallaInicial.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        pnlContenedorBandeja.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlStart.add(pnlContenedorBandeja, gridBagConstraints);

        pnlMenuStart.setBackground(new java.awt.Color(47, 43, 45));
        pnlMenuStart.setMinimumSize(new java.awt.Dimension(400, 400));
        pnlMenuStart.setPreferredSize(new java.awt.Dimension(400, 400));
        pnlMenuStart.setLayout(new java.awt.GridBagLayout());

        lblTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Alarm_Clock_28px.png"))); // NOI18N
        lblTime.setText("Time");
        lblTime.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTime.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 142;
        gridBagConstraints.insets = new java.awt.Insets(53, 0, 0, 0);
        pnlMenuStart.add(lblTime, gridBagConstraints);

        dayShow.setForeground(new java.awt.Color(47, 43, 45));
        dayShow.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        dayShow.setEnabled(false);
        dayShow.setFocusable(false);
        dayShow.setMinimumSize(new java.awt.Dimension(0, 0));
        dayShow.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 185;
        gridBagConstraints.ipady = 135;
        gridBagConstraints.insets = new java.awt.Insets(46, 0, 0, 0);
        pnlMenuStart.add(dayShow, gridBagConstraints);

        lblTime1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTime1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Christmas_Star_28px.png"))); // NOI18N
        lblTime1.setText("Bienvenida");
        lblTime1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblTime1.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 3;
        pnlMenuStart.add(lblTime1, gridBagConstraints);

        btnAboutMe.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        btnAboutMe.setForeground(new java.awt.Color(255, 255, 255));
        btnAboutMe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_Laptop_32px.png"))); // NOI18N
        btnAboutMe.setText("Acerca de..");
        btnAboutMe.setContentAreaFilled(false);
        btnAboutMe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAboutMe.setFocusable(false);
        btnAboutMe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutMeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(46, 0, 10, 0);
        pnlMenuStart.add(btnAboutMe, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        pnlStart.add(pnlMenuStart, gridBagConstraints);

        pnlBoxFrame.add(pnlStart);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlFrame.add(pnlBoxFrame, gridBagConstraints);

        getContentPane().add(pnlFrame);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Se encarga de mostrar la pantalla de forma maximizada al ser desocultada
     *
     * @param evt
     */

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified

        setExtendedState(MAXIMIZED_BOTH);

    }//GEN-LAST:event_formWindowDeiconified

    /**
     * Se encarga de mostrar el panel con el formulario para un nuevo cliente
     *
     * @param evt
     */

    private void btnNuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoClienteActionPerformed

        new ChangePanel(pnlBoxFrame, pnlNewCustumer);
        txtNombreNewC.requestFocus();

    }//GEN-LAST:event_btnNuevoClienteActionPerformed

    /**
     * se ecnarga de eliminar un cliente y su contenido
     *
     * @param evt
     */

    private void btnEliminarShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarShowCActionPerformed

        int eleccion;
        URL iconURL = getClass().getResource("/img/icons8_High_Priority_48px.png");
        ImageIcon icon = new ImageIcon(iconURL);
        eleccion = JOptionPane.showConfirmDialog(pnlJointShowC, "Se eliminara permanentemente, ¿Estas seguro?", "Eliminar Cliente", WIDTH, HEIGHT, icon);
        switch (eleccion) {
            case 0:

                String nameRemove;

                try {

                    removeCustomers(MyConnection.get(), Integer.valueOf(lblIDCustomers.getText()));
                    removeCustomerPdf(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());
                    RemoveCustomersObligaciones(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());

                    pnlBandeja.removeAll();
                    loadPnlCustomers(MyConnection.get(), "");
                    pnlBandeja.revalidate();
                    pnlBandeja.repaint();

                    new ChangePanel(pnlBoxFrame, pnlStart);

                } catch (ClassNotFoundException | SQLException e) {
                    
                    JOptionPane.showMessageDialog(null, e);
                    
                }

                break;
        }

    }//GEN-LAST:event_btnEliminarShowCActionPerformed

    /**
     * se encarga de regresar al panel principal
     *
     * @param evt
     */

    private void btnReturnShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnShowCActionPerformed

        new ChangePanel(pnlBoxFrame, pnlStart);
        new ChangePanel(pnlBoxShowC, pnlFormShowC);

        txtNombrePdf.setText("");
        btnSeleccionarPdf.setText("Elegir PDF");
        txtObligacion.setText("");

        pnlBandejaPdf.removeAll();
        btnGuardarCambiosShowC.setVisible(false);
        btnCancelarShowC.setVisible(false);
        btnGuardarCambiosShowC.setEnabled(false);
        btnCancelarShowC.setEnabled(false);
        btnEditarShowC.setEnabled(true);
        btnEliminarShowC.setEnabled(true);
        btnInformacionShowC.doClick();

    }//GEN-LAST:event_btnReturnShowCActionPerformed

    /**
     * se encarga de mostrar el panel editable
     *
     * @param evt
     */

    private void btnEditarShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarShowCActionPerformed

        new ChangePanel(pnlBoxShowC, pnlEditCustomer);

        txtNombreEditC.requestFocus();
        btnGuardarCambiosShowC.setVisible(true);
        btnCancelarShowC.setVisible(true);
        btnGuardarCambiosShowC.setEnabled(true);
        btnCancelarShowC.setEnabled(true);
        btnEditarShowC.setEnabled(false);
        btnEliminarShowC.setEnabled(false);

        try {

            loadPnlEdit(MyConnection.get(), Integer.valueOf(lblIDCustomers.getText()));

        } catch (SQLException | ClassNotFoundException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_btnEditarShowCActionPerformed

    /**
     * se encarga de guardar los cambioes hechos en el panel editable
     *
     * @param evt
     */

    private void btnGuardarCambiosShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCambiosShowCActionPerformed

        try {

            updateCustomers(MyConnection.get(), Integer.valueOf(lblIDCustomers.getText()));
            updateObligationWithName(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText(), txtNombreEditC.getText());
            updatePdfWithNameid(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText(), txtNombreEditC.getText());
            pnlBandeja.removeAll();
            loadPnlCustomers(MyConnection.get(), "");

        } catch (SQLException | ClassNotFoundException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

        try {

            loadDataPanel(MyConnection.get(), Integer.valueOf(lblIDCustomers.getText()));

        } catch (SQLException | ClassNotFoundException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

        ChangePanel cambiaPanel = new ChangePanel(pnlBoxShowC, pnlFormShowC);

        btnGuardarCambiosShowC.setVisible(false);
        btnCancelarShowC.setVisible(false);
        btnGuardarCambiosShowC.setEnabled(false);
        btnCancelarShowC.setEnabled(false);
        btnEditarShowC.setEnabled(true);
        btnEliminarShowC.setEnabled(true);

        txtNombreEditC.setText("");
        txtRucEditC.setText("");
        txtRepLegalEditC.setText("");
        txtNombreContactoEditC.setText("");
        txtCorreoEditC.setText("");
        txtNumeroContactoEditC.setText("");
        txtCedulaRepEditC.setText("");
        txtNitEditC.setText("");
        txtNoContribuyenteEditC.setText("");
        txtContraseñaEditC.setText("");

    }//GEN-LAST:event_btnGuardarCambiosShowCActionPerformed

    /**
     * se encarga de cancelar la edicion del cliente
     *
     * @param evt
     */

    private void btnCancelarShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarShowCActionPerformed

        new ChangePanel(pnlBoxShowC, pnlFormShowC);

        btnGuardarCambiosShowC.setVisible(false);
        btnCancelarShowC.setVisible(false);
        btnGuardarCambiosShowC.setEnabled(false);
        btnCancelarShowC.setEnabled(false);
        btnEditarShowC.setEnabled(true);
        btnEliminarShowC.setEnabled(true);

        txtNombreEditC.setText("");
        txtRucEditC.setText("");
        txtRepLegalEditC.setText("");
        txtNombreContactoEditC.setText("");
        txtCorreoEditC.setText("");
        txtNumeroContactoEditC.setText("");
        txtCedulaRepEditC.setText("");
        txtNitEditC.setText("");
        txtNoContribuyenteEditC.setText("");
        txtContraseñaEditC.setText("");

    }//GEN-LAST:event_btnCancelarShowCActionPerformed

    /**
     * se encarga de regresar a la pantalla de inicio desde la pantalla de nuevo
     * cliente
     *
     * @param evt
     */

    private void btnReturnNewCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnNewCActionPerformed

        new ChangePanel(pnlBoxFrame, pnlStart);

        txtNombreNewC.setText("");
        txtRucNewC.setText("");
        txtRepLegalNewC.setText("");
        txtNombreContactoNewC.setText("");
        txtCorreoNewC.setText("");
        txtNumeroContactoNewC.setText("");
        txtCedulaRepNewC.setText("");
        txtNitNewC.setText("");
        txtNoContribuyenteNewC.setText("");
        txtContraseñaMuniPaNewC.setText("");

    }//GEN-LAST:event_btnReturnNewCActionPerformed

    /**
     * se encarga de guardar el nuevo cliente
     *
     * @param evt
     */

    private void btnGuardarNewCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarNewCActionPerformed

        String name = txtNombreNewC.getText();
        String ruc = txtRucNewC.getText();
        String repLegal = txtRepLegalNewC.getText();
        String nombreContactoEmpresa = txtNombreContactoNewC.getText();
        String correoContactoEmpresa = txtCorreoNewC.getText();
        String numeroContacto = txtNumeroContactoNewC.getText();
        String cedulaRepLegal = txtCedulaRepNewC.getText();
        String nit = txtNitNewC.getText();
        String noContribuyenteMuniPa = txtNoContribuyenteNewC.getText();
        String contraMuniPa = txtContraseñaMuniPaNewC.getText();

        if (txtNombreNewC.getText().equals("")) {

            JOptionPane.showMessageDialog(null, "El cliente tiene que tener obligatoriamente un nombre");

        } else {

            try {

                insertCustomers(MyConnection.get(), name, ruc, repLegal,
                        nombreContactoEmpresa, correoContactoEmpresa, numeroContacto,
                        cedulaRepLegal, nit, noContribuyenteMuniPa, contraMuniPa);

            } catch (SQLException | ClassNotFoundException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            }

            pnlBandeja.removeAll();

            try {

                loadPnlCustomers(MyConnection.get(), "");

            } catch (SQLException | ClassNotFoundException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            }

            pnlBandeja.revalidate();
            pnlBandeja.repaint();

            txtNombreNewC.setText("");
            txtRucNewC.setText("");
            txtRepLegalNewC.setText("");
            txtNombreContactoNewC.setText("");
            txtCorreoNewC.setText("");
            txtNumeroContactoNewC.setText("");
            txtCedulaRepNewC.setText("");
            txtNitNewC.setText("");
            txtNoContribuyenteNewC.setText("");
            txtContraseñaMuniPaNewC.setText("");

            new ChangePanel(pnlBoxFrame, pnlStart);

        }

    }//GEN-LAST:event_btnGuardarNewCActionPerformed

    /**
     * se encarga de cancelar el formulario de nuevo cliente y de regresar a la
     * pantalla de inicio
     *
     * @param evt
     */

    private void btnCancelarNewCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarNewCActionPerformed

        new ChangePanel(pnlBoxFrame, pnlStart);

        txtNombreNewC.setText("");
        txtRucNewC.setText("");
        txtRepLegalNewC.setText("");
        txtNombreContactoNewC.setText("");
        txtCorreoNewC.setText("");
        txtNumeroContactoNewC.setText("");
        txtCedulaRepNewC.setText("");
        txtNitNewC.setText("");
        txtNoContribuyenteNewC.setText("");
        txtContraseñaMuniPaNewC.setText("");

    }//GEN-LAST:event_btnCancelarNewCActionPerformed

    /**
     * se encarga regresar a el panel que muestra la informacion del cliente
     *
     * @param evt
     */

    private void btnInformacionShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionShowCActionPerformed

        btnGuardarPdfShowC.setVisible(false);
        btnCancelarPdfShowC.setVisible(false);
        btnGuardarObligacionShowC.setVisible(false);
        btnEditarShowC.setVisible(true);
        btnEliminarShowC.setVisible(true);

        new ChangePanel(pnlBoxShowC, pnlFormShowC);

    }//GEN-LAST:event_btnInformacionShowCActionPerformed

    /**
     * se encarga de mostrar el administrador de archivos para seleccionar el
     * pdf
     *
     * @param evt
     */

    private void btnSeleccionarPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarPdfActionPerformed

        selectPdf();

    }//GEN-LAST:event_btnSeleccionarPdfActionPerformed

    /**
     * se encarga de cancelar la creacion de un nuevo pdf
     *
     * @param evt
     */

    private void btnCancelarPdfShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPdfShowCActionPerformed

        txtNombrePdf.setText("");
        btnSeleccionarPdf.setText("Elegir PDF");

    }//GEN-LAST:event_btnCancelarPdfShowCActionPerformed

    /**
     * se encarga de mostrar el panel de los pdf
     *
     * @param evt
     */

    private void btnAvisoDeOperacionesShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvisoDeOperacionesShowCActionPerformed

        try {

            btnGuardarPdfShowC.setVisible(true);
            btnCancelarPdfShowC.setVisible(true);
            btnGuardarPdfShowC.setEnabled(true);
            btnCancelarPdfShowC.setEnabled(true);

            loadDataPanel(MyConnection.get(), Integer.valueOf(lblIDCustomers.getText()));

            new ChangePanel(pnlBoxShowC, pnlAvisoDeOperaciones);

            txtNombrePdf.requestFocus();
            btnEditarShowC.setVisible(false);
            btnEliminarShowC.setVisible(false);
            btnGuardarObligacionShowC.setVisible(false);

            pnlBandejaPdf.removeAll();
            loadPnlPdf(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());

        } catch (SQLException | ClassNotFoundException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_btnAvisoDeOperacionesShowCActionPerformed

    /**
     * se encarga de guardar el nuevo pdf
     *
     * @param evt
     */

    private void btnGuardarPdfShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPdfShowCActionPerformed

        String nombre = txtNombrePdf.getText();
        File ruta = new File(pathPdf);
        if (nombre.trim().length() != 0 && pathPdf.trim().length() != 0) {

            try {

                savePdf(nombre, ruta);
                pathPdf = "";

            } catch (SQLException | ClassNotFoundException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            }
        } else {

            JOptionPane.showMessageDialog(null, "Falta el Nombre o el Archivo PDF");
            
        }

        txtNombrePdf.setText("");
        btnSeleccionarPdf.setText("Elegir PDF");

    }//GEN-LAST:event_btnGuardarPdfShowCActionPerformed

    /**
     * se encarga de eliminar el pdf seleccionado
     *
     * @param evt
     */

    private void jPopMenuItemEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPopMenuItemEliminarActionPerformed

        try {

            Component invoker = jPopMenuBtn.getInvoker();
            JButton invJb = (JButton) jPopMenuBtn.getInvoker();

            removePdf(MyConnection.get(), Integer.valueOf(invJb.getName()));

            pnlBandejaPdf.remove(invoker);
            pnlBandejaPdf.repaint();
            pnlBandejaPdf.revalidate();

        } catch (SQLException | ClassNotFoundException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_jPopMenuItemEliminarActionPerformed

    /**
     * se encarga de mostrar el panel de olbigaciones y de crear el archivo
     * .properties es caso tal de que no exista
     *
     * @param evt
     */

    private void btnObligacionesFiscalesShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObligacionesFiscalesShowCActionPerformed

        try {
            
            btnEditarShowC.setVisible(false);
            btnEliminarShowC.setVisible(false);
            btnGuardarPdfShowC.setVisible(false);
            btnCancelarPdfShowC.setVisible(false);
            btnGuardarObligacionShowC.setVisible(true);
            btnGuardarObligacionShowC.setEnabled(true);
            
            datePickerNewObligation.setDateToToday();
            timePickerNewObligation.setTimeToNow();

            File fileProperty = new File("config.properties");
            if (fileProperty.exists()) {
                
                //if the file property exists, do nothing.
                
            } else {

                fileProperty.createNewFile();

            }

            pnlBandejaObligaciones.removeAll();
            loadPnlObligacion(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());

            new ChangePanel(pnlBoxShowC, pnlObligacionesFiscales);

            txtObligacion.requestFocus();

        } catch (SQLException | ClassNotFoundException | IOException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_btnObligacionesFiscalesShowCActionPerformed

    /**
     * se encarga de guardar la obligacion con la fecha y hora
     *
     * @param evt
     */

    private void btnGuardarObligacionShowCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarObligacionShowCActionPerformed

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        //Obtiene la fecha y hora ingresadas por el usuario para la obligacion
        String obligacion = txtObligacion.getText();
        String userDateNewObligation = datePickerNewObligation.getDate().format(formatter);
        String userTimeNewObligation = timePickerNewObligation.getTimeStringOrEmptyString();
        
        //Date dateInput = FormatterDate.parse(userDate+" "+userTime);

        //Reviso si el usuario a puesto una obligacion o no.
        if (txtObligacion.getText().isEmpty()) {

            JOptionPane.showMessageDialog(pnlBoxShowC, "Debe haber una tarea para continuar");

        } else {

            try {

                //Inserto los datos ingresados para la creacion de una obligacion en la Base de datos
                insertObligation(MyConnection.get(), obligacion, userDateNewObligation, userTimeNewObligation, "notsent");

                pnlBandejaObligaciones.removeAll();
                loadPnlObligacion(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());
                pnlBandejaObligaciones.revalidate();
                pnlBandejaObligaciones.repaint();

                //Limpio y seteo el formulario de la creacion de obligaciones
                txtObligacion.setText("");
                datePickerNewObligation.setDateToToday();
                timePickerNewObligation.setTimeToNow();
                txtObligacion.requestFocus();

            } catch (SQLException | ClassNotFoundException | IOException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

    }//GEN-LAST:event_btnGuardarObligacionShowCActionPerformed

    /**
     * se encarga de mostrar el panel emergente de forma editable
     *
     * @param evt
     */

    private void btnEditarObligacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarObligacionActionPerformed

        try {

            new ChangePanel(pnlBoxDialogObligacion, pnlDialogObligacionEditar);

            SimpleDateFormat FormatterDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            String userDate = lblShowFecha.getText();
            String userTime = lblShowHora.getText();
            Date dateShowed = FormatterDate.parse(userDate + " " + userTime);

            LocalDate date = dateShowed.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime time = LocalDateTime.ofInstant(dateShowed.toInstant(), ZoneId.systemDefault()).toLocalTime();
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            checkDate = dateShowed;
            pnlTxtShowObligacionEditar.setText(pnlTxtShowObligacion.getText());

            editFechaPicker.setDate(date);
            editHoraPicker.setTime(time);

        } catch (ParseException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        }

    }//GEN-LAST:event_btnEditarObligacionActionPerformed

    /**
     * se encarga de ocultar el panel emergente de la obligacion
     *
     * @param evt
     */

    private void btnCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseDialogActionPerformed

        dialogObligacion.dispose();

    }//GEN-LAST:event_btnCloseDialogActionPerformed

    /**
     * se encarga de guardar los cambios aplicados en el panel editable de la
     * obligacion
     *
     * @param evt
     */

    private void btnGuardarCambioObligacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCambioObligacionActionPerformed

        try {

            UpdateObligation(MyConnection.get(), Integer.valueOf(lblIDObligacion.getText()));

            try {

                loadPopupPnl(MyConnection.get(), Integer.valueOf(lblIDObligacion.getText()));
                dialogObligacion.revalidate();
                dialogObligacion.repaint();
                pnlBandejaObligaciones.removeAll();

                loadPnlObligacion(MyConnection.get(), lblIDCustomers.getText() + lblNameShowC.getText());
                pnlBandejaObligaciones.revalidate();
                pnlBandejaObligaciones.repaint();

            } catch (SQLException | IOException ex) {

                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

            }

            new ChangePanel(pnlBoxDialogObligacion, pnlDialogShowObligacion);

        } catch (SQLException | ClassNotFoundException | ParseException ex) {

            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);

        }


    }//GEN-LAST:event_btnGuardarCambioObligacionActionPerformed

    /**
     * se encarga de cancelar la edicion del panel emergente
     *
     * @param evt
     */

    private void btnCancelarEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEditarActionPerformed

        new ChangePanel(pnlBoxDialogObligacion, pnlDialogShowObligacion);

    }//GEN-LAST:event_btnCancelarEditarActionPerformed

    /**
     * se encarga de marcar como completada la obligacion y de eliminarla
     *
     * @param evt
     */

    private void btnObligacionCompletadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObligacionCompletadaActionPerformed

        String sql = "SELECT * FROM Obligaciones "
                + "WHERE obligacion = '" + pnlTxtShowObligacion + "'";

        try {
            
            Statement stmt = MyConnection.get().createStatement();
            int eleccion;
            URL iconURL = getClass().getResource("/img/icons8_High_Priority_48px.png");
            ImageIcon icon = new ImageIcon(iconURL);
            eleccion = JOptionPane.showConfirmDialog(dialogObligacion, "¿Estas seguro de marcarlo como completado?, Se quitara de tu lista de Obligaciones", "Eliminar Cliente", WIDTH, HEIGHT, icon);
            
            switch (eleccion) {

                case 0:
                    dialogObligacion.dispose();
                    removeObligationWithId(MyConnection.get(), idObligaciones);
                    btnObligacionesFiscalesShowC.doClick();
                    break;
                    
            }
            
        } catch (SQLException | ClassNotFoundException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        }

    }//GEN-LAST:event_btnObligacionCompletadaActionPerformed

    /**
     * se encarga de ocultar el panel emergente aboutme
     *
     * @param evt
     */

    private void btnCloseAboutMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseAboutMeActionPerformed

        dialogAboutMe.dispose();

    }//GEN-LAST:event_btnCloseAboutMeActionPerformed

    /**
     * se encarga de autocopiar el numero de contacto Extibax
     *
     * @param evt
     */

    private void lblShowWhatsappMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowWhatsappMouseClicked

        StringSelection stringSelection = new StringSelection("+50762545505");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        hiddenIcon.trayIcon.displayMessage("Whatsapp Extibax ha sido copiado automaticamente", "Peguelo donde desee", TrayIcon.MessageType.NONE);

    }//GEN-LAST:event_lblShowWhatsappMouseClicked

    /**
     * se encarga de autocopiar el correo de contacto Extibax
     *
     * @param evt
     */

    private void lblMiCorreoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMiCorreoMouseClicked

        StringSelection stringSelection = new StringSelection("extibax@gmail.com");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        hiddenIcon.trayIcon.displayMessage("Correo Extibax ha sido copiado automaticamente", "Peguelo donde desee", TrayIcon.MessageType.NONE);

    }//GEN-LAST:event_lblMiCorreoMouseClicked

    /**
     * se encarga se mostrar el panel emergente aboutme
     *
     * @param evt
     */

    private void btnAboutMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutMeActionPerformed

        dialogAboutMe.setLocationRelativeTo(pnlContenedorBandeja);
        dialogAboutMe.setVisible(true);

    }//GEN-LAST:event_btnAboutMeActionPerformed

    /**
     * se encarga de ocultar el panel emergente helper de obligaciones
     *
     * @param evt
     */

    private void btnClosePnlHelperObligacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClosePnlHelperObligacionesActionPerformed

        dialogObligacionesHelp.dispose();

    }//GEN-LAST:event_btnClosePnlHelperObligacionesActionPerformed

    /**
     * se encarga de mostrar el panel emergente helper de Avisos de operaciones
     *
     * @param evt
     */

    private void lblAvisosDeOperacionesHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAvisosDeOperacionesHelpMouseClicked

        dialogAvisoDeOperacionesHelp.setLocationRelativeTo(pnlAvisoDeOperaciones);
        dialogAvisoDeOperacionesHelp.setVisible(true);

    }//GEN-LAST:event_lblAvisosDeOperacionesHelpMouseClicked

    /**
     * se encarga de mostrar el panel emergente helper de obligaciones fiscales
     *
     * @param evt
     */

    private void lblObligacionesFiscalesHelperMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblObligacionesFiscalesHelperMouseClicked

        dialogObligacionesHelp.setLocationRelativeTo(pnlObligacionesFiscales);
        dialogObligacionesHelp.setVisible(true);

    }//GEN-LAST:event_lblObligacionesFiscalesHelperMouseClicked

    /**
     * se encarga de ocultar el panel helper de aviso de operaciones
     *
     * @param evt
     */

    private void btnCloseAvisoDeOperacionesHelperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseAvisoDeOperacionesHelperActionPerformed

        dialogAvisoDeOperacionesHelp.dispose();

    }//GEN-LAST:event_btnCloseAvisoDeOperacionesHelperActionPerformed

    /**
     * se encarga de mostrar el panel emergente para modificar el correo a
     * enviar el aviso de obligaciones
     *
     * @param evt
     */

    private void lblConfigEmailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConfigEmailMouseClicked

        dialogConfigObligaciones.setLocationRelativeTo(pnlObligacionesFiscales);
        dialogConfigObligaciones.setVisible(true);

    }//GEN-LAST:event_lblConfigEmailMouseClicked

    /**
     * se encarga de guardar el correo indicado en el archivo .properties
     *
     * @param evt
     */

    private void btnCloseYGuardarCambiosObligacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseYGuardarCambiosObligacionActionPerformed

        dialogConfigObligaciones.dispose();

        Properties properties = new Properties();

        try {
            
            properties.setProperty("correo", txtEmailAEnviar.getText());
            properties.store(new FileWriter("config.properties"), "un comentario");
            
        } catch (FileNotFoundException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        }

    }//GEN-LAST:event_btnCloseYGuardarCambiosObligacionActionPerformed

    /**
     * oculta el panel emergente que modifica el correo
     *
     * @param evt
     */

    private void btnCloseYCancelarCambiosObligacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseYCancelarCambiosObligacionActionPerformed

        dialogConfigObligaciones.dispose();

    }//GEN-LAST:event_btnCloseYCancelarCambiosObligacionActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        try {

            pnlBandeja.removeAll();
            pnlBandeja.repaint();
            loadPnlCustomers(MyConnection.get(), txtCustomers.getText());

        } catch (SQLException | ClassNotFoundException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearchCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchCancelActionPerformed

        txtCustomers.setText("");
        pnlBandeja.removeAll();
        pnlBandeja.repaint();

        try {

            loadPnlCustomers(MyConnection.get(), "");

        } catch (SQLException | ClassNotFoundException ex) {
            
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            
        }

    }//GEN-LAST:event_btnSearchCancelActionPerformed

    private void txtCustomersKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomersKeyPressed

        btnSearch.doClick();

    }//GEN-LAST:event_txtCustomersKeyPressed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            
            try {
                
                new UI().setVisible(true);
                
            } catch (AWTException | SQLException | ClassNotFoundException | FileNotFoundException ex) {
                
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAboutMe;
    private javax.swing.JButton btnAvisoDeOperacionesShowC;
    private javax.swing.JButton btnCancelarEditar;
    private javax.swing.JButton btnCancelarNewC;
    private javax.swing.JButton btnCancelarPdfShowC;
    private javax.swing.JButton btnCancelarShowC;
    private javax.swing.JButton btnCloseAboutMe;
    private javax.swing.JButton btnCloseAvisoDeOperacionesHelper;
    private javax.swing.JButton btnCloseDialog;
    private javax.swing.JButton btnClosePnlHelperObligaciones;
    private javax.swing.JButton btnCloseYCancelarCambiosObligacion;
    private javax.swing.JButton btnCloseYGuardarCambiosObligacion;
    private javax.swing.JButton btnEditarObligacion;
    private javax.swing.JButton btnEditarShowC;
    private javax.swing.JButton btnEliminarShowC;
    private javax.swing.JButton btnGuardarCambioObligacion;
    private javax.swing.JButton btnGuardarCambiosShowC;
    private javax.swing.JButton btnGuardarNewC;
    private javax.swing.JButton btnGuardarObligacionShowC;
    private javax.swing.JButton btnGuardarPdfShowC;
    private javax.swing.JButton btnInformacionNewC;
    private javax.swing.JButton btnInformacionShowC;
    private javax.swing.JButton btnNuevoCliente;
    private javax.swing.JButton btnObligacionCompletada;
    private javax.swing.JButton btnObligacionesFiscalesShowC;
    private javax.swing.JButton btnReturnNewC;
    private javax.swing.JButton btnReturnShowC;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchCancel;
    private javax.swing.JButton btnSeleccionarPdf;
    private com.github.lgooddatepicker.components.DatePicker datePickerNewObligation;
    public com.toedter.calendar.JDayChooser dayShow;
    public javax.swing.JDialog dialogAboutMe;
    public javax.swing.JDialog dialogAvisoDeOperacionesHelp;
    public javax.swing.JDialog dialogConfigObligaciones;
    public javax.swing.JDialog dialogObligacion;
    public javax.swing.JDialog dialogObligacionesHelp;
    private com.github.lgooddatepicker.components.DatePicker editFechaPicker;
    private com.github.lgooddatepicker.components.TimePicker editHoraPicker;
    private javax.swing.JLabel gif;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopMenuBtn;
    private javax.swing.JMenuItem jPopMenuItemEliminar;
    private javax.swing.JMenu jPopOptions;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lblAgregarObligacion;
    private javax.swing.JLabel lblAvisosDeOperaciones;
    private javax.swing.JLabel lblAvisosDeOperacionesHelp;
    private javax.swing.JLabel lblCedulaRepEditC;
    private javax.swing.JLabel lblCedulaRepNewC;
    private javax.swing.JLabel lblCedulaRepShowC;
    private javax.swing.JLabel lblClientManager;
    private javax.swing.JLabel lblClientManagerNewC;
    private javax.swing.JLabel lblClientManagerShowC;
    private javax.swing.JLabel lblConfigEmail;
    private javax.swing.JLabel lblConfiguracionDeObligaciones;
    private javax.swing.JLabel lblContraseñaMuniPaEditC;
    private javax.swing.JLabel lblContraseñaMuniPaNewC;
    private javax.swing.JLabel lblContraseñaMuniPaShowC;
    private javax.swing.JLabel lblCorreoContactoNewC;
    private javax.swing.JLabel lblCorreoEditC;
    private javax.swing.JLabel lblCorreoShowC;
    private javax.swing.JLabel lblCreadoEl;
    private javax.swing.JLabel lblCustomersManager;
    private javax.swing.JLabel lblEditFecha;
    private javax.swing.JLabel lblEditarInformacionEditC;
    private javax.swing.JLabel lblEmailAEnviar;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFinalizadoEl;
    private javax.swing.JLabel lblHoraEditar;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblIDCustomers;
    private javax.swing.JLabel lblIDObligacion;
    private javax.swing.JLabel lblInformacionDeLaObligacion;
    private javax.swing.JLabel lblInformacionDeLaObligacionEditar;
    private javax.swing.JLabel lblInformacionNewC;
    private javax.swing.JLabel lblInformacionShowC;
    private javax.swing.JLabel lblManejoDeOperaciones;
    private javax.swing.JLabel lblMiCorreo;
    private javax.swing.JLabel lblNameNewC;
    private javax.swing.JLabel lblNameShowC;
    private javax.swing.JLabel lblNameUser;
    private javax.swing.JLabel lblNitEditC;
    private javax.swing.JLabel lblNitNewC;
    private javax.swing.JLabel lblNitShowC;
    private javax.swing.JLabel lblNoContribuyente;
    private javax.swing.JLabel lblNoContribuyenteNewC;
    private javax.swing.JLabel lblNoContribuyenteShowC;
    private javax.swing.JLabel lblNombreContactoEditC;
    private javax.swing.JLabel lblNombreContactoNewC;
    private javax.swing.JLabel lblNombreContactoShowC;
    private javax.swing.JLabel lblNombreDelPDF;
    private javax.swing.JLabel lblNombreEditC;
    private javax.swing.JLabel lblNombreNewC;
    private javax.swing.JLabel lblNumeroContactoEditC;
    private javax.swing.JLabel lblNumeroContactoNewC;
    private javax.swing.JLabel lblNumeroContactoShowC;
    private javax.swing.JLabel lblObligacionesFiscales;
    private javax.swing.JLabel lblObligacionesFiscalesHelper;
    private javax.swing.JLabel lblProgramador;
    private javax.swing.JLabel lblRepLegalEditC;
    private javax.swing.JLabel lblRepLegalNewC;
    private javax.swing.JLabel lblRepLegalShowC;
    private javax.swing.JLabel lblRucEditC;
    private javax.swing.JLabel lblRucNewC;
    private javax.swing.JLabel lblRucShowC;
    private javax.swing.JLabel lblSeleccionarPDF;
    private javax.swing.JLabel lblShowFecha;
    private javax.swing.JLabel lblShowHora;
    private javax.swing.JLabel lblShowWhatsapp;
    public javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTime1;
    private javax.swing.JPanel pnlAboutMeComponents;
    private javax.swing.JPanel pnlAboutMeHeader;
    private javax.swing.JPanel pnlAvisoDeOperaciones;
    private javax.swing.JPanel pnlAvisoDeOperacionesHeader;
    public javax.swing.JPanel pnlBandeja;
    private javax.swing.JPanel pnlBandejaObligaciones;
    private javax.swing.JPanel pnlBandejaPdf;
    private javax.swing.JPanel pnlBoxDialogObligacion;
    public javax.swing.JPanel pnlBoxFrame;
    private javax.swing.JPanel pnlBoxNewC;
    private javax.swing.JPanel pnlBoxShowC;
    private javax.swing.JPanel pnlContainerAboutMe;
    private javax.swing.JPanel pnlContainerObligaciones;
    private javax.swing.JPanel pnlContainerObligaciones1;
    private javax.swing.JPanel pnlContainerTextAvisoDeOperacionesHelper;
    private javax.swing.JPanel pnlContainerTextOblifgacion;
    private javax.swing.JPanel pnlContainerTextOblifgacion1;
    public javax.swing.JPanel pnlContenedorBandeja;
    private javax.swing.JPanel pnlDialogComponents;
    private javax.swing.JPanel pnlDialogContainerAvisoDeOperaciones;
    private javax.swing.JPanel pnlDialogObligacionEditar;
    private javax.swing.JPanel pnlDialogShowObligacion;
    private javax.swing.JPanel pnlEditCustomer;
    private javax.swing.JPanel pnlEmergenteBox;
    private javax.swing.JPanel pnlEmergenteBox1;
    private javax.swing.JPanel pnlEmergenteInfo;
    private javax.swing.JPanel pnlEmergenteInfo1;
    private javax.swing.JPanel pnlFormNewC;
    private javax.swing.JPanel pnlFormShowC;
    private javax.swing.JPanel pnlFrame;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlHeaderNewC;
    private javax.swing.JPanel pnlHeaderShowC;
    private javax.swing.JPanel pnlJointNewC;
    private javax.swing.JPanel pnlJointShowC;
    private javax.swing.JPanel pnlManejoDelAvisoDeOperacionesHeader;
    private javax.swing.JPanel pnlMenuNewC;
    private javax.swing.JPanel pnlMenuShowC;
    private javax.swing.JPanel pnlMenuStart;
    public javax.swing.JPanel pnlNewCustumer;
    private javax.swing.JPanel pnlObligacionEditarHeader;
    private javax.swing.JPanel pnlObligacionesComponent;
    private javax.swing.JPanel pnlObligacionesComponent1;
    private javax.swing.JPanel pnlObligacionesFiscales;
    private javax.swing.JPanel pnlObligacionesFiscalesHeader;
    private javax.swing.JPanel pnlObligacionesHeader;
    private javax.swing.JPanel pnlObligacionesHeader1;
    public javax.swing.JPanel pnlShowCustomer;
    private javax.swing.JPanel pnlStart;
    private javax.swing.JPanel pnlTopNewC;
    private javax.swing.JPanel pnlTopObligacionPopup;
    private javax.swing.JPanel pnlTopShowC;
    private javax.swing.JPanel pnlTopStart;
    private javax.swing.JTextPane pnlTxtShowObligacion;
    private javax.swing.JTextPane pnlTxtShowObligacionEditar;
    private javax.swing.JScrollPane scrollPaneCustomers;
    private javax.swing.JScrollPane scrollPaneDialog;
    private javax.swing.JScrollPane scrollPaneObligaciones;
    private javax.swing.JScrollPane scrollPaneObligaciontEdit;
    private javax.swing.JScrollPane scrollPanePdf;
    private com.github.lgooddatepicker.components.TimePicker timePickerNewObligation;
    private javax.swing.JTextField txtCedulaRepEditC;
    private javax.swing.JTextField txtCedulaRepLegalShowC;
    private javax.swing.JTextField txtCedulaRepNewC;
    private javax.swing.JTextField txtContraseñaEditC;
    private javax.swing.JTextField txtContraseñaMuniPaNewC;
    private javax.swing.JTextField txtContraseñaMuniPaShowC;
    private javax.swing.JTextField txtCorreoContactoShowC;
    private javax.swing.JTextField txtCorreoEditC;
    private javax.swing.JTextField txtCorreoNewC;
    private javax.swing.JTextField txtCustomers;
    private javax.swing.JTextField txtEmailAEnviar;
    private javax.swing.JTextField txtNitEditC;
    private javax.swing.JTextField txtNitNewC;
    private javax.swing.JTextField txtNitShowC;
    private javax.swing.JTextField txtNoContribuyenteEditC;
    private javax.swing.JTextField txtNoContribuyenteNewC;
    private javax.swing.JTextField txtNoContribuyenteShowC;
    private javax.swing.JTextField txtNombreContactoEditC;
    private javax.swing.JTextField txtNombreContactoNewC;
    private javax.swing.JTextField txtNombreContactoShowC;
    private javax.swing.JTextField txtNombreEditC;
    private javax.swing.JTextField txtNombreNewC;
    private javax.swing.JTextField txtNombrePdf;
    private javax.swing.JTextField txtNumeroContactoEditC;
    private javax.swing.JTextField txtNumeroContactoNewC;
    private javax.swing.JTextField txtNumeroContactoShowC;
    private javax.swing.JTextField txtObligacion;
    private javax.swing.JTextPane txtPaneAvsioHelper;
    private javax.swing.JTextPane txtPaneObligacionHelper;
    private javax.swing.JTextField txtRepLegalEditC;
    private javax.swing.JTextField txtRepLegalNewC;
    private javax.swing.JTextField txtRepLegalShowC;
    private javax.swing.JTextField txtRucEditC;
    private javax.swing.JTextField txtRucNewC;
    private javax.swing.JTextField txtRucShowC;
    // End of variables declaration//GEN-END:variables
}
