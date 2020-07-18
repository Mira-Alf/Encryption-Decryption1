package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        int index = ConsoleUtil.getIndexOfArgument(args, "-data");
        ExecuteProgramTemplate template = null;
        template = index != -1 ? new DataInputProgram() : new FileInputProgram();
        try {
            template.executeProgram(args);
        } catch(IOException | RuntimeException fo) {
            System.out.println("Error: "+fo.getMessage());
        }
    }
}

enum Operation {
    ENC("enc"), DEC("dec");
    private String name;
    Operation(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}

interface CryptFactory {
    AlgorithmStrategy getStrategy();
    AlgorithmStrategy createAlgorithmStrategy(String algo, int key);
    CryptOperation createCryptOperation(String op, String originalText);
}

class ConcreteCryptFactory implements CryptFactory {

    private AlgorithmStrategy strategy;

    @Override
    public AlgorithmStrategy getStrategy() {
        return this.strategy;
    }
    @Override
    public AlgorithmStrategy createAlgorithmStrategy(String algo, int key) {

        switch(algo) {
            case "shift":
                this.strategy = new ShiftAlgorithm(key);
                break;
            case "unicode":
                this.strategy = new UnicodeAlgorithm(key);
                break;
        }
        return getStrategy();
    }

    @Override
    public CryptOperation createCryptOperation(String operation, String originalText) {
        switch(operation) {
            case "enc":
                return new EncryptOperation(originalText, strategy);
            case "dec":
                return new DecryptOperation(originalText, strategy);
        }
        return null;
    }
}

interface CryptOperation {
    AlgorithmStrategy getStrategy();
    void setStrategy(AlgorithmStrategy strategy);
    Operation getOperation();
    void setOriginalText(String originalText);
    String getTransformedText();
    void perform();
}

class EncryptOperation implements CryptOperation {

    private Operation operation;
    private AlgorithmStrategy strategy;
    private String originalText;
    private String transformedText;

    public EncryptOperation(String originalText, AlgorithmStrategy strategy) {
        setOperation();
        setOriginalText(originalText);
        setStrategy(strategy);
    }
    @Override
    public AlgorithmStrategy getStrategy() {
        return strategy;
    }

    @Override
    public void setStrategy(AlgorithmStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    private void setOperation() {
        this.operation = Operation.ENC;
    }

    @Override
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    @Override
    public String getTransformedText() {
        return transformedText;
    }

    @Override
    public void perform() {
        this.transformedText = strategy.transform(originalText, operation);
    }
}

class DecryptOperation implements CryptOperation {

    private Operation operation;
    private AlgorithmStrategy strategy;
    private String originalText;
    private String transformedText;

    public DecryptOperation(String originalText, AlgorithmStrategy strategy) {
        setOperation();
        setOriginalText(originalText);
        setStrategy(strategy);
    }

    @Override
    public AlgorithmStrategy getStrategy() {
        return strategy;
    }

    @Override
    public void setStrategy(AlgorithmStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    private void setOperation() {
        this.operation = Operation.DEC;
    }

    @Override
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    @Override
    public String getTransformedText() {
        return transformedText;
    }

    @Override
    public void perform() {
        this.transformedText = strategy.transform(originalText, operation);
    }
}

interface AlgorithmStrategy {
    String transform( String originalText, Operation operation );
    void setKey(int key);
}

class ShiftAlgorithm implements AlgorithmStrategy {

    private static final char[] LOWER_CASE = new char[26];
    static {
        int index = 0;
        for( char ch = 'a'; ch <= 'z'; ch++ ) {
            LOWER_CASE[index] =ch;
            index++;
        }
    }

    private int key;

    public ShiftAlgorithm(int key) {
        setKey(key);
    }

    @Override
    public String transform(String originalText, Operation operation) {
        char[] transformedChars = new char[originalText.length()];
        int counter = 0;
        for( char c : originalText.toCharArray() ) {
            boolean isUpperCase = Character.isUpperCase(c);
            char lowerCase = Character.toLowerCase(c);
            int index = lowerCase-'a';
            if(lowerCase >= 'a' && lowerCase <= 'z') {
                key = key % 26;
                if (operation == Operation.ENC) {
                    index += key;
                    index = index % 26;
                } else {
                    index -= key;
                    index = index < 0 ? 26 + index : index;
                }
                transformedChars[counter++] = isUpperCase ? Character.toUpperCase(LOWER_CASE[index]) : LOWER_CASE[index];
            } else
                transformedChars[counter++] = c;
        }
        return String.valueOf(transformedChars);
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }
}

class UnicodeAlgorithm implements  AlgorithmStrategy {

    private int key;

    public UnicodeAlgorithm(int key) {
        setKey(key);
    }

    @Override
    public String transform(String originalText, Operation operation) {
        char[] transformedChars = new char[originalText.length()];
        int index = 0;
        for( char ch : originalText.toCharArray() ) {
            int codeUnit = operation == Operation.ENC ? ch + key : ch - key;
            transformedChars[index] = (char) codeUnit;
            index++;
        }
        return String.valueOf(transformedChars);
    }

    @Override
    public void setKey(int key) {
        this.key = key;
    }
}

class ConsoleUtil {
    public static int getIndexOfArgument(String[] args, String key) {
        int index = 0;
        for( String arg : args ) {
            if( arg.equals(key))
                return index+1;
            index++;
        }
        return -1;
    }
}

class FileUtil {
    public static String readFromFile(String fileName) throws IOException {
        if( fileName.indexOf(".") == -1 )
            throw new RuntimeException("Invalid file name: "+fileName);
        return new String(Files.readAllBytes(Path.of(fileName)));
    }

    public static void writeToFile(String outFile, String text) throws FileNotFoundException {
        if( outFile.indexOf(".") == -1 )
            throw new RuntimeException("Invalid file name: "+outFile);
        File file = new File(outFile);
        PrintWriter pw = new PrintWriter(file);
        pw.println(text);
        pw.close();
    }
}

abstract class ExecuteProgramTemplate {
    private String operation;
    private int key;
    private String algo;
    protected String originalText;

    public void findOperation(String[] args) throws IllegalArgumentException{
        int index = ConsoleUtil.getIndexOfArgument(args, "-mode");
        this.operation = index == -1 ? "enc" : args[index];
    }

    public void findKey(String[] args) throws NumberFormatException {
        int index = ConsoleUtil.getIndexOfArgument(args, "-key");
        this.key = index == -1 ? 0 : Integer.valueOf(args[index]);
    }

    public void findAlgo(String[] args) {
        int index = ConsoleUtil.getIndexOfArgument(args, "-alg");
        this.algo = index == -1 ? "shift" : args[index];
    }

    public void printTransformedText(String transformedText, String[] args) throws FileNotFoundException {
        int index = ConsoleUtil.getIndexOfArgument(args, "-out");
        if(index == -1)
            System.out.println(transformedText);
        else {
            FileUtil.writeToFile(args[index], transformedText);
        }
    }

    public void executeProgram(String[] args) throws IOException {
        findOperation(args);
        findKey(args);
        findAlgo(args);
        getOriginalText(args);
        ConcreteCryptFactory factory = new ConcreteCryptFactory();
        factory.createAlgorithmStrategy(algo, key);
        CryptOperation cryptOperation = factory.createCryptOperation(operation, originalText);
        cryptOperation.perform();
        printTransformedText(cryptOperation.getTransformedText(), args);
    }
    public abstract void getOriginalText(String[] args) throws IOException;
}

class DataInputProgram extends ExecuteProgramTemplate {

    @Override
    public void getOriginalText(String[] args) {
        int index = ConsoleUtil.getIndexOfArgument(args, "-data");
        if( index == -1 )
            throw new IllegalArgumentException("Data not available");
        this.originalText = args[index];
    }

}

class FileInputProgram extends ExecuteProgramTemplate {

    @Override
    public void getOriginalText(String[] args) throws IOException {
        int index = ConsoleUtil.getIndexOfArgument(args, "-in");
        if(index == -1)
            throw new IllegalArgumentException("Data not available");
        String fileName = args[index];
        this.originalText = FileUtil.readFromFile(fileName);
    }

}