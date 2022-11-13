import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchEngineImpl implements SearchEngine {
    @Override
    public List<PageEntry> search(String word) throws IOException {
        word = word.toLowerCase();
        List<PageEntry> list = new ArrayList<>();
        File pdfs = new File("C:\\Users\\Снежана\\IdeaProjects\\pcs-final-diplom2\\pdfs");
        for (File pdf : pdfs.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdf));
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
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, count);
                    list.add(pageEntry);
                }
            }
        }
        Collections.sort(list);
        return list;
    }
}

