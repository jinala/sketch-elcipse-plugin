package sketchPlugin.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class SketchWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

}
