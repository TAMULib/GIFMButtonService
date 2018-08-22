package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class PersistedButtonValidator extends BaseModelValidator {

    public PersistedButtonValidator() {
        String nameProperty = "linkText";
        this.addInputValidator(new InputValidator(InputValidationType.required, "A Button requires linkText", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minlength, "Button linkText can not be empty", nameProperty, 1));
    }
}
