package de.iks.rataplan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.repository.BackendUserRepository;

@Service
@Transactional
public class BackendUserServiceImpl implements BackendUserService {

	@Autowired
	private BackendUserRepository backendUserRepository;
	
	@Override
	public BackendUser createBackendUser(BackendUser backendUser) {
		return backendUserRepository.saveAndFlush(backendUser);
	}

	@Override
	public BackendUser getBackendUserByAuthUserId(Integer authUserId) {
		return backendUserRepository.findOneByAuthUserId(authUserId);
	}

	@Override
	public BackendUser updateBackendUser(BackendUser backendUser) {
		return this.backendUserRepository.saveAndFlush(backendUser);
	}
}
