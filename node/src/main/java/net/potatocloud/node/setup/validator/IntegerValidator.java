package net.potatocloud.node.setup.validator;

import net.potatocloud.node.setup.SetupAnswerResult;
import net.potatocloud.node.setup.SetupAnswerValidator;

public class IntegerValidator implements SetupAnswerValidator {

    @Override
    public SetupAnswerResult validate(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return SetupAnswerResult.error("Please enter a valid number");
        }
        return SetupAnswerResult.success();
    }
}