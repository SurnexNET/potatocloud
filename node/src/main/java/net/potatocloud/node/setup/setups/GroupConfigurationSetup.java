package net.potatocloud.node.setup.setups;

import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.setup.Setup;
import net.potatocloud.node.setup.SetupAnswerResult;
import net.potatocloud.node.setup.validator.BooleanValidator;
import net.potatocloud.node.setup.validator.IntegerValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupConfigurationSetup extends Setup {

    private final ServiceGroupManager groupManager;

    private static final String NAME = "Name";
    private static final String PLATFORM = "Platform";
    private static final String MIN_ONLINE_COUNT = "Min Online Count";
    private static final String MAX_ONLINE_COUNT = "Max Online Count";
    private static final String MAX_PLAYERS = "Max Players";
    private static final String MAX_MEMORY = "Max Memory";
    private static final String FALLBACK = "Fallback";
    private static final String STATIC_SERVERS = "Static servers";
    private static final String START_PRIORITY = "Start Priority";
    private static final String START_PERCENTAGE = "Start Percentage";

    public GroupConfigurationSetup(Console console, ScreenManager screenManager, ServiceGroupManager groupManager) {
        super(console, screenManager);
        this.groupManager = groupManager;
    }

    @Override
    public String getName() {
        return "Group Configuration";
    }

    @Override
    public void initQuestions() {
        addQuestion(NAME, "What is the name of the group?", null, null, input -> {
            if (input.isBlank()) {
                return SetupAnswerResult.error("Name cannot be empty");
            }
            if (groupManager.existsServiceGroup(input)) {
                return SetupAnswerResult.error("A service group with the same name already exists");
            }
            return SetupAnswerResult.success();
        });

        final List<String> platformOptions = new ArrayList<>();
        for (PlatformVersions value : PlatformVersions.values()) {
            platformOptions.add(value.platform().getFullName());
        }

        addQuestion(PLATFORM, "What is the platform (server version) of the group?", null, platformOptions, input -> {
            if (!PlatformVersions.exists(input)) {
                return SetupAnswerResult.error("This platform does not exist");
            }
            return SetupAnswerResult.success();
        });

        addQuestion(MIN_ONLINE_COUNT, "What is the min online count of the group?", String.valueOf(1), null, new IntegerValidator());
        addQuestion(MAX_ONLINE_COUNT, "What is the max online count of the group?", String.valueOf(1), null, new IntegerValidator());
        addQuestion(MAX_PLAYERS, "What are the max players of the group?", null, null, new IntegerValidator());

        addQuestion(MAX_MEMORY, "What is the max memory of the group?", null,
                List.of("256", "512", "1024", "1536", "2048", "3072", "4096", "6144", "8192"), new IntegerValidator());

        addQuestion(FALLBACK, "Is this group a fallback?", null, List.of("true", "false", "yes", "no"), new BooleanValidator(), answers -> {
            final String platform = answers.get(PLATFORM);
            return isProxy(platform);
        });

        addQuestion(STATIC_SERVERS, "Is this group static?", null, List.of("true", "false", "yes", "no"), new BooleanValidator());

        addQuestion(START_PRIORITY, "What is the start priority of the group? (higher = starts first)", "1", null, new IntegerValidator());

        addQuestion(START_PERCENTAGE, "At which percentage of online players should new services be started? (-1 = disabled)", "80", null, new IntegerValidator());
    }

    @Override
    protected void onFinish(Map<String, String> answers) {
        groupManager.createServiceGroup(
                answers.get(NAME),
                answers.get(PLATFORM),
                Integer.parseInt(answers.get(MIN_ONLINE_COUNT)),
                Integer.parseInt(answers.get(MAX_ONLINE_COUNT)),
                Integer.parseInt(answers.get(MAX_PLAYERS)),
                Integer.parseInt(answers.get(MAX_MEMORY)),
                Boolean.parseBoolean(answers.getOrDefault(FALLBACK, "false")),
                Boolean.parseBoolean(answers.get(STATIC_SERVERS)),
                Integer.parseInt(answers.get(START_PRIORITY)),
                Integer.parseInt(answers.get(START_PERCENTAGE))
        );
    }

    private boolean isProxy(String input) {
        return PlatformVersions.getPlatformByName(input.strip()).isProxy();
    }
}
