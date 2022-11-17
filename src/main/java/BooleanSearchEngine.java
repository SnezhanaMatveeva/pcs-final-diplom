import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private Map<File, Map<Integer, Map<String, Integer>>> map = new HashMap<>();

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

            Map<Integer, Map<String, Integer>> integerMapHashMap = new HashMap<>();
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
                    int counter = freqs.getOrDefault(word, 0) + 1;
                    freqs.put(word, counter);
                    integerMapHashMap.put(i, freqs);
                    map.put(file, integerMapHashMap);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        word = word.toLowerCase();
        List<PageEntry> list = new ArrayList<>();
        for (Map.Entry<File, Map<Integer, Map<String, Integer>>> fileMapEntry : map.entrySet()) {
            for (Map.Entry<Integer, Map<String, Integer>> integerMapEntry : fileMapEntry.getValue().entrySet()) {
                if (integerMapEntry.getValue().containsKey(word)) {
                    list.add(new PageEntry(fileMapEntry.getKey().getName(), integerMapEntry.getKey(), integerMapEntry.getValue().get(word)));
                }
            }
        }
        Collections.sort(list);
        return list;
    }
}
