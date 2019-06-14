package saltlux.ctv.tranSS.util;

import saltlux.ctv.tranSS.exception.BadRequestException;

import java.util.List;

public class ValidationUtil {
    public void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    public static <T> boolean isListNullOrEmpty(List<T> list) {
        return null == list || list.isEmpty();
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static boolean isPositiveId(Long value) {
        return value != null && value > 0;
    }
}
