
import java.util.UUID;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

public class Reproducer {
    public static void main(String[] args) {
        var printerName = "UnknownPrinter-" + UUID.randomUUID();
        var attributes = new HashPrintServiceAttributeSet();
        attributes.add(new PrinterName(printerName, null));

        System.out.println("Starting lookup loop for: " + printerName);
        while (true) {
            for (PrintService service : PrintServiceLookup.lookupPrintServices(null, attributes)) {
                if (service.getName().equals(printerName)) {
                    System.err.println("Found non-existent printer. " + printerName);
                    System.exit(1);
                }
            }
        }
    }
}
