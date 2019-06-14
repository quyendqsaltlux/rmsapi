package saltlux.ctv.tranSS.util;

public class CodeGenerator {
    public static String genCandidateCode(String type, String year, String latestCode) {
        return type + year + latestCode + 1;
    }
}
