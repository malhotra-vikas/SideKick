package main.java.com.mconsultants.alexa.exception;

public class FileReaderException extends Exception {
    public FileReaderException (String message) {
        super(message);
    }

    public FileReaderException (Throwable throwable) {
        super(throwable);
    }

    public FileReaderException (String message, Throwable throwable) {
        super(message, throwable);
    }




}
