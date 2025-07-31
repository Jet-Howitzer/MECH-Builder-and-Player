import com.opencsv.CSVReader;
import java.io.FileReader;

public class CSVTest {
    public static void main(String[] args) throws Exception {
        CSVReader reader = new CSVReader(new FileReader("test.csv"));
        String[] line = reader.readNext();
        System.out.println("First line: " + String.join(",", line));
    }
}