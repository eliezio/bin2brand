package io.eliez.fintools.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class BINValidator implements ConstraintValidator<BIN, CharSequence> {

    private Pattern pattern;

    @Override
    public void initialize(BIN parameters) {
        this.pattern = Pattern.compile("\\d{6}");
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return pattern.matcher(value).matches();
    }
}
