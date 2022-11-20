import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<String, TreeSet<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) {
        List<File> files = new ArrayList<>();
        Collections.addAll(files, Objects.requireNonNull(pdfsDir.listFiles()));
        for (File file : files) {
            PdfDocument doc = null;
            try {
                doc = new PdfDocument(new PdfReader(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int numberOfPages = doc.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                PdfPage page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> stringIntegerEntry : freqs.entrySet()) {
                    if (!map.containsKey(stringIntegerEntry.getKey())) {
                        map.put(stringIntegerEntry.getKey(), new TreeSet<>());
                        map.get(stringIntegerEntry.getKey()).add(new PageEntry(file.getName(), i, stringIntegerEntry.getValue()));
                    } else {
                        map.get(stringIntegerEntry.getKey()).add(new PageEntry(file.getName(), i, stringIntegerEntry.getValue()));
                    }
                }
            }
        }

    }

    @Override
    public List<PageEntry> search(String word) {
        Set<PageEntry> pageEntries = map.get(word);
        if (pageEntries == null) {
            throw new RuntimeException("Такого слова нет");
        }
        return new ArrayList<>(pageEntries);
    }
}
