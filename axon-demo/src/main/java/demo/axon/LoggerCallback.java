package demo.axon;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class LoggerCallback implements CommandCallback<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger("demo.axon.debug");

    public static final LoggerCallback INSTANCE = new LoggerCallback();

    @Override
    public void onSuccess(Object result, CommandContext<Object> context) {
        logger.debug("[Success] {}", string(context.getCommand()));
    }

    @Override
    public void onFailure(Throwable cause, CommandContext<Object> context) {
        logger.debug("[Failure] {}\n{}", string(context.getCommand()), cause);
    }

    String string(Object object) {
        return ToStringBuilder.reflectionToString(object,
            ToStringStyle.SHORT_PREFIX_STYLE);
    }
}