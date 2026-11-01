package tp5;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;

public class AltaCliente {

    public String apellido;
    public String nombres;
    public String Tipo_Doc;
    public long nroDoc;
    public int prov;
    public String estadoCivil;
    public int hijos;
    public String condIVA;
    public double ingreso;
    private List<String> errores = new ArrayList<String>();
    private static List<AltaCliente> clientes = new ArrayList();
    private int cont = 0;

    public AltaCliente(String apellido, String nombres, String tipoDoc, long nroDoc, int prov,
                       String estadoCivil, int hijos, String condIVA, double ingreso) {
        this.apellido = apellido;
        this.nombres = nombres;
        this.Tipo_Doc = tipoDoc;
        this.nroDoc = nroDoc;
        this.prov = prov;
        this.estadoCivil = estadoCivil;
        this.hijos = hijos;
        this.condIVA = condIVA;
        this.ingreso = ingreso;
    }

    public boolean validar() {
        errores.clear();
        if (apellido == null || apellido.trim() == "") {
            errores.add("El apellido es obligatorio");
        }
        if (apellido != null && apellido.length() > 30) {
            errores.add("El apellido no puede superar los 30 caracteres");
        }
        if (nombres == null || nombres.trim() == "") {
            errores.add("Los nombres son obligatorios");
        }
        if (nombres != null && nombres.length() > 30) {
            errores.add("Los nombres no pueden superar los 30 caracteres");
        }
        if (Tipo_Doc == null) {
            errores.add("El tipo de documento es obligatorio");
        } else if (!(Tipo_Doc.equals("DNI") || Tipo_Doc.equals("CI") || Tipo_Doc.equals("LE")
                || Tipo_Doc.equals("LC") || Tipo_Doc.equals("PAS"))) {
            errores.add("Tipo de documento invalido: " + Tipo_Doc);
        }
        if (nroDoc <= 0 || nroDoc > 999999999) {
            errores.add("Numero de documento invalido");
        }
        if (!((prov >= 1 && prov <= 24) || prov == 40)) {
            errores.add("Codigo de provincia invalido");
        }
        if (estadoCivil == "S" || estadoCivil == "C" || estadoCivil == "V"
                || estadoCivil == "D" || estadoCivil == "O") {
            // ok
        } else {
            errores.add("Estado civil invalido");
        }
        if (hijos < 0 || hijos >= 20) {
            errores.add("Cantidad de hijos invalida");
        }
        if (condIVA == null || !(condIVA.equals("RI") || condIVA.equals("RNI") || condIVA.equals("EX"))) {
            errores.add("Condicion de IVA invalida");
        }
        if (ingreso <= 0) {
            errores.add("El ingreso mensual no puede ser negativo");
        }
        return errores.size() == 0;
    }

    public String calcularCategoria() {
        String cat = "";
        if (ingreso > 500000) {
            if (hijos > 3) {
                cat = "A2";
            } else {
                cat = "A1";
            }
        } else {
            if (ingreso > 200000) {
                if (hijos > 3) {
                    cat = "B2";
                } else {
                    cat = "B1";
                }
            } else {
                if (condIVA.equals("EX")) {
                    cat = "C2";
                } else {
                    cat = "C1";
                }
            }
        }
        return cat;
    }

    public String generarCodigo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fecha = sdf.format(new Date());
        String x = Tipo_Doc + nroDoc + fecha;
        cont++;
        return x + "-" + cont;
    }

    public boolean guardar() {
        if (!validar()) {
            mostrarErrores();
            return false;
        }
        for (int i = 0; i < clientes.size(); i++) {
            AltaCliente c = clientes.get(i);
            if (c.Tipo_Doc.equals(Tipo_Doc) && c.nroDoc == nroDoc) {
                System.out.println("El cliente ya existe");
                return false;
            }
        }
        clientes.add(this);
        try {
            File f = new File("clientes.txt");
            FileWriter fw = new FileWriter(f, true);
            fw.write(generarCodigo() + ";" + apellido + ";" + nombres + ";" + Tipo_Doc + ";" + nroDoc
                    + ";" + prov + ";" + estadoCivil + ";" + hijos + ";" + condIVA + ";" + ingreso
                    + ";" + calcularCategoria() + "\n");
        } catch (IOException e) {
        }
        return true;
    }

    public static AltaCliente buscar(String tipo, long nro) {
        for (int i = 0; i < clientes.size(); i++) {
            AltaCliente c = clientes.get(i);
            if (c.Tipo_Doc.equals(tipo) && c.nroDoc == nro) {
                return c;
            }
        }
        return null;
    }

    public static boolean eliminar(String tipo, long nro) {
        for (int i = 0; i < clientes.size(); i++) {
            AltaCliente c = clientes.get(i);
            if (c.Tipo_Doc.equals(tipo) && c.nroDoc == nro) {
                clientes.remove(i);
                return true;
            }
        }
        return false;
    }

    public void mostrarErrores() {
        System.out.println("Se encontraron " + errores.size() + " errores:");
        for (String e : errores) {
            System.out.println(" - " + e);
        }
    }

    public static void main(String[] args) {
        AltaCliente a = new AltaCliente("Perez", "Juan", "DNI", 30123456, 1, "C", 2, "RI", 350000);
        a.guardar();
        AltaCliente b = new AltaCliente("Gomez", "Ana", "PAS", 0, 50, "X", 25, "MT", -100);
        b.guardar();
        AltaCliente c = new AltaCliente("Lopez", "Maria", "DNI", 28999111, 24, "S", 20, "EX", 0);
        c.guardar();
        System.out.println("Clientes registrados: " + clientes.size());
    }
}
