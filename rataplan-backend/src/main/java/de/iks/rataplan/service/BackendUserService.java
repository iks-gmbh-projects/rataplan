package de.iks.rataplan.service;

import de.iks.rataplan.domain.BackendUser;

public interface BackendUserService {

	public BackendUser createBackendUser(BackendUser backendUser);
	public BackendUser getBackendUserByAuthUserId(Integer authUserId);
	public BackendUser updateBackendUser(BackendUser backendUser);

}
