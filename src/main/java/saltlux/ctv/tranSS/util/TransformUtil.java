package saltlux.ctv.tranSS.util;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public class TransformUtil {
    public static <T> List<Field> ignoreNeedFilterFields(T obj, List<FilterRequest> filters) {
        Class c = obj.getClass();
        Field[] properties = c.getDeclaredFields();

        List<Field> candidateProperties2 = new ArrayList<>();
        if (null != properties && properties.length > 0) {
            List<String> filterRequestFields = new ArrayList<>();
            if (filters != null) {
                for (FilterRequest filterRequest : filters) {
                    filterRequestFields.add(filterRequest.getField());
                }
            }
            candidateProperties2 = Arrays.stream(properties)
                    .filter(property -> String.class.isAssignableFrom(property.getType()) &&
                            filterRequestFields.indexOf(property.toString()) < 0)
                    .collect(Collectors.toList());
        }

        return candidateProperties2;
    }

    public static Specification integrate(Specification spec1, Specification spec2) {
        if (null != spec1 && null != spec2) {
            return Specification.where(spec1).and(spec2);
        } else if (null != spec1) {
            return spec1;
        } else {
            return spec2;
        }
    }

    /*SP1904480*/
    /*P1904435*/
    public static String generateCode(
            String prefix,
            String maxCode,
            int suffixSize,
            int yearSize
    ) {
        int yearIndexStart = prefix.length();
        int yearIndexEnd = yearIndexStart + yearSize;

        int currentYear = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));
        int tailCodeNumber;
        String nextCode;
        if (null == maxCode) {
            tailCodeNumber = 1;
            String nextTailCode = String.format("%0" + suffixSize + "d", tailCodeNumber);
            nextCode = prefix + currentYear + nextTailCode;
            return nextCode;
        }
        int latestYear = Integer.parseInt(maxCode.substring(yearIndexStart, yearIndexEnd));
        if (currentYear == latestYear) {
            String tailCodeString = maxCode.substring(yearIndexEnd);
            tailCodeNumber = Integer.parseInt(tailCodeString) + 1;
        } else {
            tailCodeNumber = 1;
        }
        String nextTailCode = String.format("%0" + suffixSize + "d", tailCodeNumber);
        nextCode = prefix + currentYear + nextTailCode;
        return nextCode;
    }

    public static List<String> getDuplicatedColumns(Object object,
                                                    Long objectId,
                                                    List<String> needCheckDuplicatedColumns,
                                                    CandidateRepository repository
    ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        List<String> duplicatedColumns = new ArrayList<>();
        for (String column : needCheckDuplicatedColumns) {
            String columnValue = BeanUtils.getProperty(object, column);
            if (repository.count_Columns(column, columnValue, objectId) > 0) {
                duplicatedColumns.add(column);
            }
        }
        return duplicatedColumns;
    }

    public static List<String> getDuplicatedColumns(Object object,
                                                    Long objectId,
                                                    List<String> needCheckDuplicatedColumns,
                                                    ProjectRepository repository
    ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        List<String> duplicatedColumns = new ArrayList<>();
        for (String column : needCheckDuplicatedColumns) {
            String columnValue = BeanUtils.getProperty(object, column);
            if (repository.count_Columns(column, columnValue, objectId) > 0) {
                duplicatedColumns.add(column);
            }
        }
        return duplicatedColumns;
    }

    public static Pageable createPageable(int page, int size, String orderBy, String sortDirection, String defaultOrder) {
        if (!isNullOrEmpty(orderBy)) {
            return PageRequest.of(page, size,
                    "desc".equals(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        }
        return PageRequest.of(page, size, Sort.Direction.DESC, defaultOrder);
    }

    public static String generatePOCode(
            String prefix,
            String maxCode,
            int suffixSize,
            int yearSize
    ) {
        int yearIndexStart = prefix.length();
        int yearIndexEnd = yearIndexStart + yearSize;

        int currentYear = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));
        int tailCodeNumber;
        String nextCode;
        if (null == maxCode) {
            tailCodeNumber = 1;
            String nextTailCode = String.format("%0" + suffixSize + "d", tailCodeNumber);
            nextCode = prefix + currentYear + nextTailCode;
            return nextCode;
        }
        int latestYear = Integer.parseInt(maxCode.substring(yearIndexStart, yearIndexEnd));
        if (currentYear == latestYear) {
            String tailCodeString = maxCode.substring(yearIndexEnd);
            tailCodeNumber = Integer.parseInt(tailCodeString) + 1;
        } else {
            tailCodeNumber = 1;
        }
        String nextTailCode = String.format("%0" + suffixSize + "d", tailCodeNumber);
        nextCode = prefix + currentYear + nextTailCode;
        return nextCode;
    }

    public static String nextDay() throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String dt = dtf.format(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));
        c.add(Calendar.DATE, 1);  // number of days to add
        dt = sdf.format(c.getTime());

        return dt;
    }

    public static double roundDouble2(double value) {
        return Math.round(value * 100.0) / 100.00;
    }

    public static float roundFloat2(float value) {
        return (float) (Math.round(value * 100) / 100.00);
    }

    public static float roundFloat0(float value) {
        return (float) (Math.round(value * 100) / 100);
    }

    public static BigDecimal roundByCurrency(String currency, BigDecimal total) {
        if ("KRW".equals(currency)) {
            return total.setScale(0, RoundingMode.HALF_UP);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
