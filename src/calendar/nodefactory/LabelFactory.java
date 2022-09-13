package calendar.nodefactory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class LabelFactory {
    private double maxWidth;
    private boolean textWrapEnabled;

    public LabelFactory() {
        maxWidth = Integer.MAX_VALUE;
        textWrapEnabled = false;
    }

    public Label getLabel(String text) {
        return getLabel(text);
    }

    public Label getLabel(String text, String... styleClass) {
        return getLabel(text, null, styleClass);
    }

    public Label getLabel(String text, Pos position, String... styleClass) {
        Label label = new Label(text);
        label.setMaxWidth(maxWidth);
        label.setWrapText(textWrapEnabled);

        if(position != null) {
            label.setAlignment(position);
        }

        for (String style:
                styleClass) {
            label.getStyleClass().add(style);
        }

        return label;
    }

    public LabelFactory setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public LabelFactory setTextWrapEnabled(boolean textWrapEnabled) {
        this.textWrapEnabled = textWrapEnabled;
        return this;
    }
}
