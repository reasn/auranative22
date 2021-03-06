package io.auraapp.auraandroid.common;

import android.os.Message;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TimerTest {

    @Test
    public void should_set_timeouts() {
        List<String> records = new ArrayList<>();
        TestUtil.testWithLooper((handler, done) -> {
            Timer timer = new Timer(handler);

            timer.setTimeout(() -> records.add("invocation-a"), 10);
            timer.setTimeout(() -> records.add("invocation-b"), 30);

            handler.postDelayed(() -> records.add("after-20"), 20);
            handler.postDelayed(done, 50);
        });
        assertThat(records, is(Arrays.asList("invocation-a", "after-20", "invocation-b")));
    }

    @Test
    public void should_have_a_handler_that_clears_timeouts() {
        List<String> records = new ArrayList<>();
        TestUtil.testWithLooper((handler, done) -> {
            Message message = Message.obtain(handler, () -> records.add("invocation"));
            message.obj = "test-object";
            handler.sendMessageDelayed(message, 10);
            handler.removeCallbacksAndMessages(message.obj);
            handler.postDelayed(done, 20);
        });
        assertThat(records, is(Collections.emptyList()));
    }

    /**
     * A previous Timer implementation assumed that using strings to set and clear timeouts was
     * a good idea. Apparently it isn't and this case makes that visible in the spec
     */
    @Test
    public void should_have_a_handler_that_doesnt_use_the_equals_function() {
        List<String> records = new ArrayList<>();
        TestUtil.testWithLooper((handler, done) -> {
            Message message = Message.obtain(handler, () -> records.add("invocation"));
            message.obj = "test-object";
            handler.sendMessageDelayed(message, 10);
            // This will fail because "message.obj == new String("test-object")" is false
            handler.removeCallbacksAndMessages(new String("test-object"));
            handler.postDelayed(done, 20);
        });
        assertThat(records, is(Collections.singletonList("invocation")));
    }

    @Test
    public void should_clear_timeouts() {
        List<String> records = new ArrayList<>();
        TestUtil.testWithLooper((handler, done) -> {
            Timer timer = new Timer(handler);
            Timer.Timeout timeout = timer.setTimeout(() -> records.add("invocation"), 10);
            Timer.clear(timeout);
            handler.postDelayed(done, 20);
        });
        assertThat(records, is(Collections.emptyList()));
    }
}
