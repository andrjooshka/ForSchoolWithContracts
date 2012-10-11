package tap.execounting.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.internal.OptionModelImpl;

public class StringValueSelectModel implements SelectModel {
	private final Map<String, Object> strings;

	public StringValueSelectModel(final Map<String, Object> strings) {
		this.strings = strings;
	}

	public List<OptionModel> getOptions() {
		final List<OptionModel> options = new ArrayList<OptionModel>();

		for (final String string : this.strings.keySet()) {
			options.add(new OptionModelImpl(string, strings.get(string)));
		}

		return options;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public void visit(final SelectModelVisitor visitor) {
	}
}
