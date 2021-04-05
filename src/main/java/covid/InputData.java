package covid;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class InputData {

    public static  <T> T getDataFromUser(String message, Supplier<T> supplier) {
        System.out.println(message);
        return supplier.get();
    }

    public static  <T> T getControlDataFromUser(String message,String error, Supplier<T> supplier, Predicate<T> predicate) {
        T data = getDataFromUser(message, supplier);
        if (predicate.test(data)) {
            return data;
        }
        throw new IllegalStateException(error);
    }

}
