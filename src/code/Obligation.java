package code;

/**
 * Clase complementaria para la funcion de obligaciones
 * @author SrExtibax
 */

    public class Obligation{

        int id;
        String nombreCliente, obligacion, fecha, hora, send;

        /**
         * 
         * @param idI
         * @param nombreClienteI
         * @param obligacionI
         * @param fechaI
         * @param horaI
         * @param sendI 
         */
        
        public Obligation(int idI, String nombreClienteI ,String obligacionI, String fechaI, String horaI, String sendI){
            
            this.id = idI;
            this.nombreCliente = nombreClienteI;
            this.obligacion = obligacionI;
            this.fecha = fechaI;
            this.hora = horaI;
            this.send = sendI;
            
        }
        
        public int getId(){
            return id;
        }

        public String getNombreCliente(){
            return nombreCliente;
        }
        
        public String getObligacion(){
            return obligacion;
        }

        public String getFecha(){
            return fecha;
        }
        
        public String getHora(){
            return hora;
        }

    }
