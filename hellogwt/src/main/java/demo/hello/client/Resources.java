package demo.hello.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * Resources
 */
public interface Resources extends ClientBundle {
    @Source("cross.png")
    @ImageOptions(width = 16, height = 16, repeatStyle = RepeatStyle.None)
    ImageResource cross();

    @Source("Style.css")
    Style style();
}
