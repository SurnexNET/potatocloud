package net.potatocloud.node.setup;

import java.util.Map;

public interface SetupQuestionSkipCondition {

    boolean skip(Map<String, String> answers);

}
