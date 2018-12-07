package code;

/**
 * Clase complementaria para la funcion de pdf
 * @author SrExtibax
 */

    public class Pdf {

        String nombrecliente;
        String nombrepdf;
        byte[] archivopdf;

        /**
         * Clase complementaria de la funcion Pdf
         * @param nombrecliente
         * @param nombrepdf
         * @param archivopdf 
         */
        
        public Pdf(String nombrecliente, String nombrepdf, byte[] archivopdf){
            this.nombrecliente = nombrecliente;
            this.nombrepdf = nombrepdf;
            this.archivopdf = archivopdf;
        }

        public String getNombreCliente(){
            return nombrecliente;
        }
        
        public String getNombrePdf(){
            return nombrepdf;
        }

    }
