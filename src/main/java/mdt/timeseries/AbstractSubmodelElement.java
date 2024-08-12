package mdt.timeseries;

import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.digitaltwin.aas4j.v3.model.EmbeddedDataSpecification;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType;
import org.eclipse.digitaltwin.aas4j.v3.model.Qualifier;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import lombok.Data;


/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
@Data
public class AbstractSubmodelElement implements SubmodelElement {
	private String idShort;
	private @Nullable String category;
	private @Nullable Reference semanticId;
	private List<LangStringNameType> displayName;
	private List<LangStringTextType> description;
	private List<Reference> supplementalSemanticIds;
	private List<Qualifier> qualifiers;
	private List<EmbeddedDataSpecification> embeddedDataSpecifications;
	private List<Extension> extensions;
}
