import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BooleanSearchEngine implements SearchEngine {
    private List<File> files = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) {
        Collections.addAll(files, Objects.requireNonNull(pdfsDir.listFiles()));
    }

    @Override
    public List<PageEntry> search(String word) throws IOException {
        word = word.toLowerCase();
        List<PageEntry> list = new ArrayList<>();
        for (File file : files) {
            var doc = new PdfDocument(new PdfReader(file));
            int numberOfPages = doc.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                int count = 0;
                PdfPage page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                for (String s : words) {
                    if (s.toLowerCase().equals(word)) {
                        count++;
                    }
                }
                if (count != 0) {
                    PageEntry pageEntry = new PageEntry(file.getName(), i, count);
                    list.add(pageEntry);
                }
            }
        }
        Collections.sort(list);
        return list;
    }
}
