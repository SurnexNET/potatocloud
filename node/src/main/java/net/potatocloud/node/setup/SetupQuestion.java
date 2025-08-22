package net.potatocloud.node.setup;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SetupQuestion {

    private final String name;
    private final String question;
    private final String defaultAnswer;
    private final List<String> possibleChoices;
    private final SetupAnswerValidator validator;
    private final SetupQuestionSkipCondition skipCondition;

    public boolean shouldSkip(Map<String, String> answers) {
        if (skipCondition == null) {
            return false;
        }
        return skipCondition.skip(answers);
    }
}