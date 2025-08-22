package net.potatocloud.node.setup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SetupAnswerResult {

    private final boolean success;
    private final String errorMessage;

    public static SetupAnswerResult success() {
        return new SetupAnswerResult(true, null);
    }

    public static SetupAnswerResult error(String errorMessage) {
        return new SetupAnswerResult(false, errorMessage);
    }
}
