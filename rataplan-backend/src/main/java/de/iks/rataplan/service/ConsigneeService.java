package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.domain.Vote;

import java.util.List;

public interface ConsigneeService {
    public List<Vote> getVotesForConsignee(AuthUser authUser);
    public void transcribeConsigneesToBackendUserAccesses(Vote vote);
}
