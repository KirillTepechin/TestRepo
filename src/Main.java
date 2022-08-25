import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        int number = (int)(Math.random()*1001); //[0-1000]
        String response = getResponse(number);
        System.out.println(response);
        var chars = countChar(response);
        prettyPrintChars(chars);
        //Задача со звездочкой
        float averageFrequency = averageFrequency(chars);
        printAppropriateCharacters(chars, averageFrequency);
    }
    public static String getResponse(int number) {
        byte[] bytes = new byte[0];
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://numbersapi.com/"+number+"/trivia").openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            bytes = new byte[is.available()];
            is.read(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(bytes);
    }
    public static HashMap<Character, Integer> countChar(String string){
        String cleanText = string.replaceAll("\\p{Punct}", "").replaceAll(" ","").toLowerCase();

        HashMap<Character, Integer> chars = new HashMap<>();
        cleanText.chars().forEach(op-> chars.merge((char)op, 1, Integer::sum));
        return chars;
    }
    public static void prettyPrintChars(HashMap<Character, Integer> chars){
        chars.forEach((key, value) -> System.out.printf("'%c' - %d%n", key, value));
    }
    public static float averageFrequency(HashMap<Character, Integer> chars){
        int frequency = 0;
        for(Map.Entry<Character, Integer> entry : chars.entrySet()){
            frequency+=entry.getValue();
        }
        float result = frequency/(float)chars.size();
        System.out.printf("Среднее значение частоты %d/%d = %f\n", frequency, chars.size(), result);
        return result;
    }
    public static void printAppropriateCharacters(HashMap<Character, Integer> chars, float frequency){
        System.out.println("Символы, которые соответствуют условию наиболее близкого значения частоты к среднему значанию:");
        AtomicReference<Float> min = new AtomicReference<>((float) Float.MAX_EXPONENT);
        HashSet<Character> appropriateCharacters = new HashSet<>();
        chars.forEach((key, value) -> {
            if (Math.abs(frequency - value) < min.get()) {
                appropriateCharacters.clear();
                appropriateCharacters.add(key);
                min.set(Math.abs(frequency - value));
            } else if (Math.abs(frequency - value) == min.get()) {
                appropriateCharacters.add(key);
                min.set(Math.abs(frequency - value));
            }
        });

        String result = appropriateCharacters.stream().map(character -> String.format("%c(%d)", character, (int)character))
                .collect(Collectors.joining(", "));
        System.out.println(result);
    }
}
