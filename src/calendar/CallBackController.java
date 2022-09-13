package calendar;

import java.util.function.Consumer;

public interface CallBackController {

    public void onReturnAction(CallBackController controller, Consumer<CallBackController> action);
}
