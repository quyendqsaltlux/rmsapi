package saltlux.ctv.tranSS.validation;

import org.apache.commons.lang3.EnumUtils;
import saltlux.ctv.tranSS.enums.ProjectProgressEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProjectProgressValidator implements ConstraintValidator<ProjectProgress, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return !(null != s && !EnumUtils.isValidEnum(ProjectProgressEnum.class, s));
    }
}
