package mdt.timeseries.impl;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
@JsonInclude(Include.NON_NULL)
public class LinkedSegment extends AbstractSubmodelElement implements SubmodelElementCollection {
	private static final Reference SEMANTIC_ID
		= new DefaultReference.Builder()
				.type(ReferenceTypes.EXTERNAL_REFERENCE)
				.keys(new DefaultKey.Builder()
									.type(KeyTypes.GLOBAL_REFERENCE)
									.value("https://admin-shell.io/idta/TimeSeries/Segments/LinkedSegment/1/1")
									.build())
				.build();
	
	private List<SubmodelElement> m_fieldValues;
	
	public LinkedSegment() {
		setSemanticId(SEMANTIC_ID);
	}

	@Override
	public List<SubmodelElement> getValue() {
		return m_fieldValues;
	}

	@Override
	public void setValue(List<SubmodelElement> values) {
		m_fieldValues = values;
	}
}
