import java.time.LocalDate;
import java.time.Period;

/**
 * @author zhengzebiao
 * @date 2019/12/17 23:24
 */
public class StringJoinerTest {
    public static void main(String[] args) {
        String fromDate = "2001-08-01";
        String toDate = "2020-01-01";
        Period period = Period.between(LocalDate.parse(fromDate), LocalDate.parse(toDate));
        StringBuffer sb = new StringBuffer();
        sb.append(period.getYears()).append(",")
                .append(period.getMonths()).append(",")
                .append(period.getDays());
        System.out.println(sb.toString());

    }
}