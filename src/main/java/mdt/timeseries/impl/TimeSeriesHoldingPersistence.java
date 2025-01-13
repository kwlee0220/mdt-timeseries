package mdt.timeseries.impl;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationInitializationException;
import de.fraunhofer.iosb.ilt.faaast.service.model.SubmodelElementIdentifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.operation.OperationHandle;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.operation.OperationResult;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.Page;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.paging.PagingInfo;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.ResourceNotAContainerElementException;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.ResourceNotFoundException;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.AssetAdministrationShellSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.ConceptDescriptionSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.Persistence;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.SubmodelElementSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.SubmodelSearchCriteria;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemory;

/**
 *
 * @author Kang-Woo Lee (ETRI)
 */
public class TimeSeriesHoldingPersistence implements Persistence<PersistenceTimeSeriesHolderConfig> {
	private final PersistenceInMemory m_basePersistence;
	
	public TimeSeriesHoldingPersistence() {
		m_basePersistence = new PersistenceInMemory();
	}

	@Override
	public void init(CoreConfig coreConfig, PersistenceTimeSeriesHolderConfig config, ServiceContext serviceContext)
		throws ConfigurationInitializationException {
//		m_basePersistence.init(coreConfig, config, serviceContext);
	}

	@Override
	public PersistenceTimeSeriesHolderConfig asConfig() {
		return null;
//		return m_basePersistence.asConfig();
	}

	@Override
	public AssetAdministrationShell getAssetAdministrationShell(String id, QueryModifier modifier)
		throws ResourceNotFoundException {
		return m_basePersistence.getAssetAdministrationShell(id, modifier);
	}

	@Override
	public Page<Reference> getSubmodelRefs(String aasId, PagingInfo paging) throws ResourceNotFoundException {
		return m_basePersistence.getSubmodelRefs(aasId, paging);
	}

	@Override
	public Page<AssetAdministrationShell>
	findAssetAdministrationShells(AssetAdministrationShellSearchCriteria criteria,
									QueryModifier modifier, PagingInfo paging) {
		return m_basePersistence.findAssetAdministrationShells(criteria, modifier, paging);
	}

	@Override
	public void deleteAssetAdministrationShell(String id) throws ResourceNotFoundException {
		m_basePersistence.deleteAssetAdministrationShell(id);
	}

	@Override
	public void save(AssetAdministrationShell assetAdministrationShell) {
		m_basePersistence.save(assetAdministrationShell);
	}

	@Override
	public void deleteConceptDescription(String id) throws ResourceNotFoundException {
		m_basePersistence.deleteAssetAdministrationShell(id);
	}

	@Override
	public ConceptDescription getConceptDescription(String id, QueryModifier modifier)
		throws ResourceNotFoundException {
		return m_basePersistence.getConceptDescription(id, modifier);
	}

	@Override
	public Page<ConceptDescription> findConceptDescriptions(ConceptDescriptionSearchCriteria criteria,
															QueryModifier modifier, PagingInfo paging) {
		return m_basePersistence.findConceptDescriptions(criteria, modifier, paging);
	}

	@Override
	public void save(ConceptDescription conceptDescription) {
		m_basePersistence.save(conceptDescription);
	}

	@Override
	public Submodel getSubmodel(String id, QueryModifier modifier) throws ResourceNotFoundException {
		return m_basePersistence.getSubmodel(id, modifier);
	}

	@Override
	public Page<Submodel> findSubmodels(SubmodelSearchCriteria criteria, QueryModifier modifier, PagingInfo paging) {
		return m_basePersistence.findSubmodels(criteria, modifier, paging);
	}

	@Override
	public void deleteSubmodel(String id) throws ResourceNotFoundException {
		m_basePersistence.deleteSubmodel(id);
	}

	@Override
	public void save(Submodel submodel) {
		m_basePersistence.save(submodel);
	}

	@Override
	public SubmodelElement getSubmodelElement(SubmodelElementIdentifier identifier, QueryModifier modifier)
		throws ResourceNotFoundException {
		return m_basePersistence.getSubmodelElement(identifier, modifier);
	}

	@Override
	public Page<SubmodelElement> findSubmodelElements(SubmodelElementSearchCriteria criteria, QueryModifier modifier,
														PagingInfo paging) throws ResourceNotFoundException {
		return m_basePersistence.findSubmodelElements(criteria, modifier, paging);
	}

	@Override
	public void insert(SubmodelElementIdentifier parentIdentifier, SubmodelElement submodelElement)
		throws ResourceNotFoundException, ResourceNotAContainerElementException {
		m_basePersistence.insert(parentIdentifier, submodelElement);
	}

	@Override
	public void update(SubmodelElementIdentifier identifier, SubmodelElement submodelElement)
		throws ResourceNotFoundException {
		m_basePersistence.update(identifier, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(SubmodelElementIdentifier identifier) throws ResourceNotFoundException {
		m_basePersistence.deleteSubmodelElement(identifier);
	}

	@Override
	public OperationResult getOperationResult(OperationHandle handle) throws ResourceNotFoundException {
		return m_basePersistence.getOperationResult(handle);
	}

	@Override
	public void save(OperationHandle handle, OperationResult result) {
		m_basePersistence.save(handle, result);
	}
}
