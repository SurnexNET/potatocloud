package net.potatocloud.node.setup.validator;

import net.potatocloud.node.setup.SetupAnswerResult;
import net.potatocloud.node.setup.SetupAnswerValidator;

public class BooleanValidator implements SetupAnswerValidator {

    @Override
    public SetupAnswerResult validate(String input) {
        final String lower = input.toLowerCase();
        if (!lower.equals("true") && !lower.equals("false") && !lower.equals("yes") && !lower.equals("no")) {
            return SetupAnswerResult.error("Please enter true/false or yes/no");
        }
        return SetupAnswerResult.success();
    }
}