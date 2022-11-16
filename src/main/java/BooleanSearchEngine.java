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

            Map<Integer, Map<String, Integer>> mapMap = new HashMap<>();
            for (int i = 1; i <= numberOfPages; i++) {
                int count = 0;
                PdfPage page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>(); // мапа, где ключом будет слово, а значением - частота
                for (String word : words) { // перебираем слова
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
//                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    int counter = freqs.getOrDefault(word, 0) + 1;
                    freqs.put(word, counter);
                    mapMap.put(numberOfPages, freqs);
                    map.put(file, mapMap);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) throws IOException {
        word = word.toLowerCase();
        List<PageEntry> list = new ArrayList<>();
//        for (File file : map.entrySet()) {
//            var doc = new PdfDocument(new PdfReader(file));
//            int numberOfPages = doc.getNumberOfPages();
//
//            for (int i = 1; i <= numberOfPages; i++) {
//                int count = 0;
//                PdfPage page = doc.getPage(i);
//                var text = PdfTextExtractor.getTextFromPage(page);
//                var words = text.split("\\P{IsAlphabetic}+");
//                for (String s : words) {
//                    if (s.toLowerCase().equals(word)) {
//                        count++;
//                    }
//                }
//                if (count != 0) {
//                    PageEntry pageEntry = new PageEntry(file.getName(), i, count);
//                    list.add(pageEntry);
//                }
//            }
//        }
        for (Map.Entry<File, Map<Integer, Map<String, Integer>>> fileMapEntry : map.entrySet()) {
            for (Map.Entry<Integer, Map<String, Integer>> integerMapEntry : fileMapEntry.getValue().entrySet()) {
                for (Map.Entry<String, Integer> stringIntegerEntry : integerMapEntry.getValue().entrySet()) {
                    if (stringIntegerEntry.getKey().equals(word)) {
                        list.add(new PageEntry(fileMapEntry.getKey().getName(), integerMapEntry.getKey(), stringIntegerEntry.getValue()));
                    }
                }
            }
        }
        Collections.sort(list);
        return list;
    }
}
