package code;

/**
 * Clase complementaria para los botones de los clientes
 * @author SrExtibax
 */

    public class Person {

        int idPerson;
        String nombre, identificacion, ruc, repLegal, nombreContacto, correoContacto, numeroContacto,
                cedulaRep, nit, noContribuyente, contraMuni;

        public Person(int idPersonI, String nombreI, String rucI, String repLegalI
        , String nombreContactoI, String correoContactoI, String numeroContactoI
        , String cedulaRepI, String nitI, String noContribuyenteI, String contraMuniI){
            this.idPerson = idPersonI;
            this.nombre = nombreI;
            this.ruc = rucI;
            this.repLegal = repLegalI;
            this.nombreContacto = nombreContactoI;
            this.correoContacto = correoContactoI;
            this.numeroContacto = numeroContactoI;
            this.cedulaRep = cedulaRepI;
            this.nit = nitI;
            this.noContribuyente = noContribuyenteI;
            this.contraMuni = contraMuniI;
        }

        public int getIdPerson(){
            return idPerson;
        }
        
        public String getNombre(){
            return nombre;
        }

        public String getIdentificacion(){
            return identificacion;
        }

    }
