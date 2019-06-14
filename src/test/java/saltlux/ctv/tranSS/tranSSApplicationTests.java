package saltlux.ctv.tranSS;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import saltlux.ctv.tranSS.service.StorageService;
import saltlux.ctv.tranSS.util.RepoUtil;
import saltlux.ctv.tranSS.util.TransformUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class tranSSApplicationTests {
    @Autowired
    StorageService storageService;

    @Test
    public void contextLoads() {
        String s = "candidate.code";
        String out = RepoUtil.getLastStringAfterDot(s);
        Assert.assertEquals("code", out);
    }
    @Test
    public void testRound(){
        BigDecimal decimal = new BigDecimal("1234.567899000");
        double v = decimal.doubleValue();
        double v2 = decimal.setScale(2, RoundingMode.CEILING).doubleValue();
        double v3 = decimal.stripTrailingZeros().doubleValue();
        int x= 0;
    }

}
